package com.nikolas.odteste.historia.mestre;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.nikolas.odteste.dados.DadosJogador;
import com.nikolas.odteste.historia.DadosHistoria;
import com.nikolas.odteste.historia.Grupos;
import com.nikolas.odteste.horror.CamadaHorror;
import com.nikolas.odteste.horror.RegrasCamadaHorror;
import com.nikolas.odteste.sistemas.Degrau;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

/**
 * O mestre (regras em {@link RegrasMestre}). Só servidor.
 * <ul>
 * <li>Avisos automáticos da história ({@link #avisar}) vão só para os mestres, não para todo op.</li>
 * <li>"Mestre ouvindo": em espectador, o mestre recebe o que o grupo recebe perto dele (hoje: os sustos de quem ele
 * assiste) ({@link #ouvindo}, {@link #ouvintesPerto}, {@link #assistindo}). Nunca conta como presente.</li>
 * <li>Comandos: {@code /odteste mestre ouvir on|off} e {@code /odteste mestre status <grupo>}.</li>
 * </ul>
 * Sem lag: tudo é laço curto sobre os jogadores (poucos), chamado só quando algo já vai sair.
 */
public final class Mestre {
    /** Escolha de cada mestre (ligar/desligar o ouvir). Só memória: reiniciar volta ao padrão da config. */
    private static final Map<UUID, Boolean> ESCOLHA = new HashMap<>();

    private Mestre() {}

    /** Chamado no construtor do mod. */
    public static void iniciar() {
        ConfigMestre.registrar();
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent e) -> registrar(e.getDispatcher()));
        NeoForge.EVENT_BUS.addListener((ServerStoppedEvent e) -> ESCOLHA.clear());
    }

    public static boolean ehMestre(ServerPlayer p) {
        return p != null && RegrasMestre.ehMestre(p.hasPermissions(2), p.getGameProfile().getName(), ConfigMestre.mestres());
    }

    /** Mestre em espectador com o ouvir ligado. */
    public static boolean ouvindo(ServerPlayer p) {
        return p != null && p.isSpectator() && RegrasMestre.ouvindo(ehMestre(p), true, ESCOLHA.get(p.getUUID()),
                ConfigMestre.ouvirPadrao());
    }

    /** Mestres ouvindo neste nível a até {@code raio} do ponto, ou com a câmera presa em {@code alvo}. */
    public static List<ServerPlayer> ouvintesPerto(ServerLevel nivel, Vec3 ponto, double raio, ServerPlayer alvo) {
        List<ServerPlayer> r = new ArrayList<>();
        for (ServerPlayer p : nivel.players()) {
            if (!p.isSpectator() || !ouvindo(p)) continue;
            if ((alvo != null && p.getCamera() == alvo) || p.position().distanceToSqr(ponto) <= raio * raio) r.add(p);
        }
        return r;
    }

    /** Mestres ouvindo (de qualquer nível) com a câmera presa neste jogador. */
    public static List<ServerPlayer> assistindo(ServerPlayer alvo) {
        List<ServerPlayer> r = new ArrayList<>();
        for (ServerPlayer p : alvo.server.getPlayerList().getPlayers()) {
            if (p != alvo && p.isSpectator() && p.getCamera() == alvo && ouvindo(p)) r.add(p);
        }
        return r;
    }

    /** Aviso da história só para os mestres online. */
    public static void avisar(MinecraftServer s, Component msg) {
        for (ServerPlayer p : s.getPlayerList().getPlayers()) {
            if (ehMestre(p)) p.sendSystemMessage(msg);
        }
    }

    public static boolean algumOnline(MinecraftServer s) {
        for (ServerPlayer p : s.getPlayerList().getPlayers()) {
            if (ehMestre(p)) return true;
        }
        return false;
    }

    // ------------------------------------------------------------------ comandos

    /** Console, bloco de comando ou mestre. Op que não está na lista não usa. */
    private static boolean podeUsar(CommandSourceStack s) {
        if (!s.hasPermission(2)) return false;
        return !(s.getEntity() instanceof ServerPlayer p) || ehMestre(p);
    }

    static void registrar(CommandDispatcher<CommandSourceStack> d) {
        d.register(Commands.literal("odteste").requires(s -> s.hasPermission(2))
                .then(Commands.literal("mestre").requires(Mestre::podeUsar)
                        .then(Commands.literal("ouvir")
                                .then(Commands.literal("on").executes(c -> ouvir(c, true)))
                                .then(Commands.literal("off").executes(c -> ouvir(c, false))))
                        .then(Commands.literal("status").then(Commands.argument("grupo", StringArgumentType.word())
                                .suggests((c, b) -> SharedSuggestionProvider.suggest(
                                        DadosHistoria.de(c.getSource().getServer()).grupos.todos().stream()
                                                .map(Grupos.Grupo::nome), b))
                                .executes(Mestre::status)))));
    }

    private static int ouvir(CommandContext<CommandSourceStack> c, boolean ligar) {
        if (!(c.getSource().getEntity() instanceof ServerPlayer p)) {
            c.getSource().sendFailure(Component.translatable("odteste.mestre.ouvir.nao_jogador"));
            return 0;
        }
        ESCOLHA.put(p.getUUID(), ligar);
        c.getSource().sendSuccess(() -> Component.translatable(ligar ? "odteste.mestre.ouvir.on" : "odteste.mestre.ouvir.off"), false);
        return 1;
    }

    /** Painel do grupo, só para quem digitou: hora, marcos e cada membro (Exposição, Lucidez, sustos da noite). */
    private static int status(CommandContext<CommandSourceStack> c) {
        MinecraftServer s = c.getSource().getServer();
        String nome = StringArgumentType.getString(c, "grupo");
        Optional<Grupos.Grupo> og = DadosHistoria.de(s).grupos.grupo(nome);
        if (og.isEmpty()) {
            c.getSource().sendFailure(Component.translatable("odteste.grupo.erro.sem_grupo", nome));
            return 0;
        }
        Grupos.Grupo g = og.get();
        long hora = s.overworld().getDayTime();
        boolean noite = RegrasCamadaHorror.noite(hora);
        List<Component> linhas = new ArrayList<>();

        linhas.add(Component.translatable("odteste.mestre.status.titulo", g.nome()).withStyle(ChatFormatting.GOLD));
        linhas.add(Component.translatable(noite ? "odteste.mestre.status.hora.noite" : "odteste.mestre.status.hora.dia",
                RegrasMestre.minutos(RegrasMestre.ateMudar(hora))));
        linhas.add(Component.translatable("odteste.mestre.status.marcos",
                g.marcos().isEmpty() ? "-" : String.join(", ", g.marcos())));
        for (Map.Entry<UUID, String> m : g.membros().entrySet()) {
            ServerPlayer p = s.getPlayerList().getPlayer(m.getKey());
            if (p == null) {
                linhas.add(Component.translatable("odteste.mestre.status.fora", m.getValue()).withStyle(ChatFormatting.DARK_GRAY));
                continue;
            }
            int expo = DadosJogador.exposicao(p);
            linhas.add(Component.translatable("odteste.mestre.status.membro", m.getValue(),
                    expo, Component.translatable("odteste.mestre.degrau." + Degrau.de(expo).name().toLowerCase(Locale.ROOT)),
                    DadosJogador.lucidez(p), DadosJogador.lucidezMax(p), CamadaHorror.sustosNaNoite(p))
                    .withStyle(p.isSpectator() ? ChatFormatting.DARK_GRAY : ChatFormatting.GRAY));
        }
        for (Component l : linhas) c.getSource().sendSuccess(() -> l, false);
        return 1;
    }
}
