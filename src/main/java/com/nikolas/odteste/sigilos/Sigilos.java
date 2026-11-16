package com.nikolas.odteste.sigilos;

import com.nikolas.odteste.dados.DadosJogador;
import com.nikolas.odteste.rede.AbrirTelaSigilo;
import com.nikolas.odteste.registro.Registro;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

/**
 * Sigilos aprendidos e riscados. Lado do servidor: tudo que o jogador manda é conferido aqui.
 *
 * <b>Os eventos do mundo que ensinam ainda não existem:</b> quando existirem, chamam
 * {@link #aprender(ServerPlayer, String)} (uma linha). O comando do mestre usa o mesmo caminho.
 */
public final class Sigilos {
    /** Último pedido de risco de cada jogador (tick do servidor). Só memória; some ao sair. */
    private static final Map<UUID, Long> ULTIMO_RISCO = new HashMap<>();

    private Sigilos() {}

    public static void iniciar() {
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedOutEvent e) -> ULTIMO_RISCO.remove(e.getEntity().getUUID()));
        NeoForge.EVENT_BUS.addListener((ServerStoppedEvent e) -> ULTIMO_RISCO.clear());
    }

    // ---------- o que o jogador sabe ----------

    public static List<String> conhecidos(ServerPlayer p) {
        return DadosJogador.sigilos(p);
    }

    public static boolean conhece(ServerPlayer p, String id) {
        return DadosJogador.sigilos(p).contains(id);
    }

    /**
     * Ensina um sigilo. Devolve true se era novo. Dá um som e uma frase curta (sem número). Nome inexistente: false.
     * É a "porta" para os eventos do mundo.
     */
    public static boolean aprender(ServerPlayer p, String id) {
        return aprender(p, id, true);
    }

    /** Igual a {@link #aprender(ServerPlayer, String)}; {@code avisar = false} aprende em silêncio (comando "todos"). */
    public static boolean aprender(ServerPlayer p, String id, boolean avisar) {
        List<String> antes = DadosJogador.sigilos(p);
        List<String> depois = RegrasSigilos.com(antes, id);
        if (depois == antes) return false;
        DadosJogador.definirSigilos(p, depois);
        if (!avisar) return true;
        p.playNotifySound(SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 0.7f, 0.6f);
        p.displayClientMessage(Component.translatable("odteste.sigilo.aprendeu").withStyle(net.minecraft.ChatFormatting.GRAY,
                net.minecraft.ChatFormatting.ITALIC), true);
        return true;
    }

    /** Tira um sigilo (teste e cenas do mestre). Devolve true se o jogador tinha. */
    public static boolean esquecer(ServerPlayer p, String id) {
        List<String> antes = DadosJogador.sigilos(p);
        List<String> depois = RegrasSigilos.sem(antes, id);
        if (depois == antes) return false;
        DadosJogador.definirSigilos(p, depois);
        return true;
    }

    // ---------- giz ----------

    /** Pacote que abre a tela: só os sigilos aprendidos, na ordem oficial. */
    public static AbrirTelaSigilo pacoteAbrir(ServerPlayer p, BlockPos pos) {
        return new AbrirTelaSigilo(pos, RegrasSigilos.naOrdemOficial(DadosJogador.sigilos(p)));
    }

    /**
     * Risca o sigilo no chão. O pedido vem do cliente, então nada é confiado: giz na mão, sigilo aprendido, giro
     * válido, 1 pedido por segundo, até 6 blocos, chão firme, espaço livre (só ar, ou outro sigilo nosso, que é
     * trocado) e permissão de construir ali. Devolve true se riscou.
     */
    public static boolean desenhar(ServerPlayer p, BlockPos pos, String id, int giro) {
        Simbolo simbolo = Simbolo.de(id);
        if (simbolo == null || !RegrasSigilos.giroValido(giro) || !conhece(p, id)) return false;
        long agora = p.server.getTickCount();
        Long ultimo = ULTIMO_RISCO.get(p.getUUID());
        if (!RegrasSigilos.livre(ultimo == null ? -1 : ultimo, agora)) return false;
        ULTIMO_RISCO.put(p.getUUID(), agora);

        InteractionHand mao = p.getMainHandItem().is(Registro.GIZ.get()) ? InteractionHand.MAIN_HAND
                : p.getOffhandItem().is(Registro.GIZ.get()) ? InteractionHand.OFF_HAND : null;
        if (mao == null) return false;
        ServerLevel nivel = p.serverLevel();
        if (!nivel.isLoaded(pos) || !RegrasSigilos.perto(p.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5))) {
            return false;
        }
        BlockState clicado = nivel.getBlockState(pos);
        // Clicou num sigilo: troca esse. Senão, risca em cima do bloco clicado.
        BlockPos alvo = clicado.is(Registro.SIGILO.get()) ? pos : pos.above();
        BlockPos chao = alvo.below();
        boolean livre = alvo.equals(pos) || nivel.getBlockState(alvo).isAir();
        if (!livre || !nivel.getBlockState(chao).isFaceSturdy(nivel, chao, Direction.UP)) return false;
        ItemStack giz = p.getItemInHand(mao);
        if (!p.mayUseItemAt(alvo, Direction.UP, giz)) return false;

        nivel.setBlock(alvo, Registro.SIGILO.get().estado(simbolo, giro), 3);
        nivel.playSound(null, alvo, SoundEvents.GRAVEL_PLACE, SoundSource.BLOCKS, 0.6f, 1.5f);
        giz.hurtAndBreak(1, p, mao == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        return true;
    }
}
