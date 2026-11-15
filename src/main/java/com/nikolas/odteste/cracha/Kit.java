package com.nikolas.odteste.cracha;

import com.nikolas.odteste.dados.DadosJogador;
import com.nikolas.odteste.dados.IdsDados;
import com.nikolas.odteste.historia.livros.LivrosHistoria;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/** Kit do agente: crachá e dossiê, entregues na primeira entrada. Só servidor. */
public final class Kit {
    private Kit() {}

    public static void iniciar() {
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent e) -> {
            if (!(e.getEntity() instanceof ServerPlayer p)) return;
            Cracha.atualizarDoDono(p);
            if (RegrasKit.deveDar(DadosJogador.recebeuKit(p))) {
                DadosJogador.marcarKit(p, true);
                dar(p);
            }
        });
    }

    /** Dá o crachá e o dossiê. Não mexe na marca de "já recebeu". Inventário cheio: solta no pé. */
    public static void dar(ServerPlayer p) {
        entregar(p, Cracha.criar(p));
        LivrosHistoria.criar(IdsDados.LIVRO_DOSSIE, p).ifPresent(l -> entregar(p, l));
    }

    private static void entregar(ServerPlayer p, ItemStack item) {
        if (p.getInventory().add(item)) return;
        // Cheio: cai no pé do jogador, parado (nada de jogar para a frente na altura dos olhos).
        ItemEntity solto = new ItemEntity(p.level(), p.getX(), p.getY(), p.getZ(), item);
        solto.setDeltaMovement(Vec3.ZERO);
        solto.setDefaultPickUpDelay();
        p.level().addFreshEntity(solto);
    }
}
