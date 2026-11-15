package com.nikolas.odteste.cliente;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ViewportEvent;

/**
 * Efeitos de tela dos sustos (só cliente, só para o próprio jogador): vinheta escura, escurecer e tremor.
 * Sem shader: usa a textura de vinheta do próprio Minecraft. Tudo tem duração; desenhar custa no máximo uma
 * textura e um retângulo por quadro. {@code efeitos_fortes} diminui o tremor (nunca desliga).
 */
final class Tela {
    private static final ResourceLocation VINHETA = ResourceLocation.withDefaultNamespace("textures/misc/vignette.png");

    /** Relógio do cliente, em ticks. */
    static long tick;

    /** Envelope: sobe em {@code entrada}, segura, desce em {@code saida}. */
    private record Envelope(long inicio, int entrada, int segura, int saida, float forca) {
        float valor(float t) {
            float d = t - inicio;
            if (d < 0) return 0;
            if (d < entrada) return forca * d / Math.max(1, entrada);
            d -= entrada;
            if (d < segura) return forca;
            d -= segura;
            if (d < saida) return forca * (1 - d / Math.max(1, saida));
            return 0;
        }

        boolean acabou(float t) {
            return t - inicio > entrada + segura + saida;
        }
    }

    /** Escurecer pela vinheta (bordas para dentro). */
    private static Envelope vinhetaMomento;
    /** Escurecer a tela inteira. */
    private static Envelope escuro;
    private static Envelope tremor;

    private Tela() {}

    // ---------- pedidos (chamados pelo SustoCliente) ----------

    static void vinheta(int entrada, int segura, int saida, float forca) {
        vinhetaMomento = new Envelope(tick, entrada, segura, saida, forca);
    }

    /** Tela inteira escura. Com "Efeitos fortes" em zero (hoje nunca), vira só a vinheta, no mesmo tempo. */
    static void escurecer(int entrada, int segura, int saida, float forca) {
        if (ConfigCliente.soVinhetaESom()) {
            vinheta(entrada, segura, saida, forca);
            return;
        }
        escuro = new Envelope(tick, entrada, segura, saida, forca);
    }

    /** Tremor em graus. Obedece à opção de acessibilidade. */
    static void tremer(int duracao, float graus) {
        if (ConfigCliente.tremor() <= 0) return;
        tremor = new Envelope(tick, 2, Math.max(0, duracao - 6), 4, graus);
    }

    static void limpar() {
        vinhetaMomento = escuro = tremor = null;
    }

    /** Uma vez por tick: joga fora o que acabou. */
    static void aoTick() {
        tick++;
        float t = tick;
        if (vinhetaMomento != null && vinhetaMomento.acabou(t)) vinhetaMomento = null;
        if (escuro != null && escuro.acabou(t)) escuro = null;
        if (tremor != null && tremor.acabou(t)) tremor = null;
    }

    // ---------- desenho ----------

    static void desenhar(GuiGraphics g, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        float t = tick + delta.getGameTimeDeltaPartialTick(false);
        int w = g.guiWidth();
        int h = g.guiHeight();

        float borda = vinhetaMomento == null ? 0 : vinhetaMomento.valor(t);
        float tudo = escuro == null ? 0 : escuro.valor(t);
        if (borda < 0.01f && tudo < 0.01f) return;

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();

        // Bordas escuras: a vinheta do jogo multiplicada (mesmo truque da vinheta vanilla).
        if (borda > 0.01f) {
            float k = Math.min(1f, borda);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
            g.setColor(k, k, k, 1f);
            g.blit(VINHETA, 0, 0, -90, 0f, 0f, w, h, w, h);
            g.setColor(1f, 1f, 1f, 1f);
            RenderSystem.defaultBlendFunc();
        }

        // Tela inteira escura.
        if (tudo > 0.01f) {
            int a = Math.min(255, (int) (tudo * 255));
            g.fill(0, 0, w, h, a << 24);
        }

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    // ---------- câmera ----------

    static void aoCalcularCamera(ViewportEvent.ComputeCameraAngles e) {
        float forca = ConfigCliente.tremor();
        if (forca <= 0 || tremor == null) return;
        float t = (float) (tick + e.getPartialTick());
        float amp = tremor.valor(t) * forca;
        if (amp > 0.001f) {
            e.setYaw(e.getYaw() + (float) Math.sin(t * 2.7) * amp);
            e.setPitch(e.getPitch() + (float) Math.cos(t * 3.3) * amp * 0.7f);
        }
    }
}
