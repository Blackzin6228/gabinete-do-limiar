package com.nikolas.odteste.comandos;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.nikolas.odteste.historia.DadosHistoria;
import com.nikolas.odteste.historia.Grupos;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * Comandos da história, todos só de admin (permissão 2):
 *
 * /odteste grupo criar|adicionar|tirar|apagar|ver
 * /odteste livro dar <jogadores> <livro> · lista (ComandosLivros)
 * /odteste elemento <jogador> [elemento] · revelar <jogador> (ComandosElemento)
 *
 * Para o admin, número aparece (contagens). Para o jogador, nunca.
 */
public final class ComandosHistoria {
    private static final String GRUPO = "grupo";
    private static final String JOGADORES = "jogadores";

    private ComandosHistoria() {}

    public static void iniciar() {
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent e) -> registrar(e.getDispatcher()));
        // O {grupo} dos livros usa o último nome conhecido: atualiza quando o jogador entra com nome novo.
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent e) -> {
            if (!(e.getEntity() instanceof ServerPlayer p)) return;
            DadosHistoria d = DadosHistoria.de(p.server);
            d.grupos.grupoDe(p.getUUID()).ifPresent(g -> {
                if (!p.getGameProfile().getName().equals(g.membros().get(p.getUUID()))) {
                    d.grupos.atualizarNome(p.getUUID(), p.getGameProfile().getName());
                    d.setDirty();
                }
            });
        });
    }

    static void registrar(CommandDispatcher<CommandSourceStack> d) {
        d.register(Commands.literal("odteste").requires(s -> s.hasPermission(2))
                .then(grupo())
                .then(ComandosLivros.livro())
                .then(ComandosElemento.elemento()));
    }

    private static DadosHistoria dados(CommandContext<CommandSourceStack> c) {
        return DadosHistoria.de(c.getSource().getServer());
    }

    private static int responder(CommandContext<CommandSourceStack> c, boolean ok, Component msg) {
        if (ok) {
            c.getSource().sendSuccess(() -> msg, false);
            return 1;
        }
        c.getSource().sendFailure(msg);
        return 0;
    }

    private static int erro(CommandContext<CommandSourceStack> c, String chave, Object... args) {
        return responder(c, false, Component.translatable(chave, args));
    }

    private static int ok(CommandContext<CommandSourceStack> c, String chave, Object... args) {
        return responder(c, true, Component.translatable(chave, args));
    }

    private static RequiredArgumentBuilder<CommandSourceStack, String> argGrupo() {
        return Commands.argument(GRUPO, StringArgumentType.word())
                .suggests((c, b) -> SharedSuggestionProvider.suggest(
                        DadosHistoria.de(c.getSource().getServer()).grupos.todos().stream().map(Grupos.Grupo::nome), b));
    }

    // ------------------------------------------------------------------ grupo

    private static LiteralArgumentBuilder<CommandSourceStack> grupo() {
        return Commands.literal("grupo")
                .then(Commands.literal("criar").then(argGrupo()
                        .executes(c -> criar(c, false))
                        .then(Commands.argument(JOGADORES, EntityArgument.players()).executes(c -> criar(c, true)))))
                .then(Commands.literal("adicionar").then(argGrupo()
                        .then(Commands.argument(JOGADORES, EntityArgument.players()).executes(ComandosHistoria::adicionar))))
                .then(Commands.literal("tirar").then(argGrupo()
                        .then(Commands.argument(JOGADORES, EntityArgument.players()).executes(ComandosHistoria::tirar))))
                .then(Commands.literal("apagar").then(argGrupo().executes(ComandosHistoria::apagar)))
                .then(Commands.literal("ver")
                        .executes(ComandosHistoria::listarGrupos)
                        .then(argGrupo().executes(ComandosHistoria::verGrupo)));
    }

    private static int criar(CommandContext<CommandSourceStack> c, boolean comJogadores) throws CommandSyntaxException {
        String nome = StringArgumentType.getString(c, GRUPO);
        DadosHistoria d = dados(c);
        if (Grupos.normalizar(nome).isEmpty()) return erro(c, "odteste.grupo.erro.nome", nome);
        if (!d.grupos.criar(nome)) return erro(c, "odteste.grupo.erro.existe", nome);
        d.setDirty();
        ok(c, "odteste.grupo.criado", Grupos.normalizar(nome).get());
        return comJogadores ? adicionar(c) : 1;
    }

    private static int adicionar(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        String nome = StringArgumentType.getString(c, GRUPO);
        DadosHistoria d = dados(c);
        if (d.grupos.grupo(nome).isEmpty()) return erro(c, "odteste.grupo.erro.sem_grupo", nome);
        Collection<ServerPlayer> js = EntityArgument.getPlayers(c, JOGADORES);
        for (ServerPlayer p : js) {
            Optional<String> antigo = d.grupos.adicionar(nome, p.getUUID(), p.getGameProfile().getName());
            antigo.ifPresent(a -> ok(c, "odteste.grupo.trocou", p.getDisplayName(), a));
        }
        d.setDirty();
        return ok(c, "odteste.grupo.adicionados", js.size(), nome) > 0 ? js.size() : 0;
    }

    private static int tirar(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        String nome = StringArgumentType.getString(c, GRUPO);
        DadosHistoria d = dados(c);
        int n = 0;
        for (ServerPlayer p : EntityArgument.getPlayers(c, JOGADORES)) {
            if (d.grupos.tirar(nome, p.getUUID())) n++;
        }
        d.setDirty();
        return ok(c, "odteste.grupo.tirados", n, nome) > 0 ? n : 0;
    }

    private static int apagar(CommandContext<CommandSourceStack> c) {
        String nome = StringArgumentType.getString(c, GRUPO);
        DadosHistoria d = dados(c);
        if (!d.grupos.apagar(nome)) return erro(c, "odteste.grupo.erro.sem_grupo", nome);
        d.setDirty();
        return ok(c, "odteste.grupo.apagado", nome);
    }

    private static int listarGrupos(CommandContext<CommandSourceStack> c) {
        DadosHistoria d = dados(c);
        if (d.grupos.todos().isEmpty()) return ok(c, "odteste.grupo.nenhum");
        for (Grupos.Grupo g : d.grupos.todos()) mostrarGrupo(c, g);
        return d.grupos.todos().size();
    }

    private static int verGrupo(CommandContext<CommandSourceStack> c) {
        DadosHistoria d = dados(c);
        String nome = StringArgumentType.getString(c, GRUPO);
        Optional<Grupos.Grupo> g = d.grupos.grupo(nome);
        if (g.isEmpty()) return erro(c, "odteste.grupo.erro.sem_grupo", nome);
        mostrarGrupo(c, g.get());
        return 1;
    }

    private static void mostrarGrupo(CommandContext<CommandSourceStack> c, Grupos.Grupo g) {
        String membros = g.nomes().isEmpty() ? "-" : String.join(", ", g.nomes());
        String marcos = g.marcos().isEmpty() ? "-" : String.join(", ", g.marcos());
        c.getSource().sendSuccess(() -> Component.translatable("odteste.grupo.ver", g.nome(), membros, marcos), false);
    }
}
