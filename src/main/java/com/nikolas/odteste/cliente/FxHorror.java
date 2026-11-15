package com.nikolas.odteste.cliente;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Liga os efeitos de tela com shader (base {@link FxTela}, com e sem Iris). Hoje só um: o {@link FxCorHorror}
 * (tom do mundo da camada de horror). Efeito novo entra na lista do {@link #iniciar}, com prévia aprovada.
 * Sem lag: por tick, só o relógio e as contas do cor_horror.
 */
final class FxHorror {
    /** Relógio dos efeitos (ticks, só anda sem pausa). */
    private static long relogio;

    private FxHorror() {}

    static void iniciar(IEventBus barramentoDoMod) {
        FxTela.iniciar(barramentoDoMod, List.of(new FxTela.Passe() {
            @Override
            public String shader() {
                return FxCorHorror.SHADER;
            }

            @Override
            public boolean ativo() {
                return FxCorHorror.ativo();
            }

            @Override
            public void uniforms(ShaderInstance sh, FxTela.Quadro q) {
                sh.safeGetUniform("Tempo").set(((relogio + q.parcial()) / 20f) % 3600f);
                FxCorHorror.uniforms(sh, q);
            }
        }));
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post e) -> aoTick());
        NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut e) -> limpar());
    }

    private static void aoTick() {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel nivel = mc.level;
        if (mc.player == null || nivel == null) {
            if (relogio != 0) limpar();
            return;
        }
        if (mc.isPaused()) return;
        relogio++;
        FxCorHorror.aoTick();
    }

    private static void limpar() {
        relogio = 0;
        FxCorHorror.limpar();
        FxTela.limpar();
    }
}
