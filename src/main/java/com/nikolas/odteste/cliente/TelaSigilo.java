package com.nikolas.odteste.cliente;

import com.nikolas.odteste.OdMod;
import com.nikolas.odteste.rede.DesenharSigilo;
import com.nikolas.odteste.sigilos.RegrasSigilos;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Tela do giz: uma grade só com os sigilos que o jogador aprendeu (o servidor mandou a lista; nada mais chega aqui).
 * Clicar num sigilo pede ao servidor para riscá-lo no chão e fecha a tela. O botão de baixo gira o desenho
 * (quatro lados). Nenhum número na tela. Sem efeito de tela, então {@code efeitos_fortes} não muda nada aqui.
 */
public final class TelaSigilo extends Screen {
    private static final int CELULA = 26;
    private static final int ICONE = 20;
    private static final int COLUNAS_MAX = 8;

    private final BlockPos pos;
    private final List<String> sigilos;
    private int giro;
    private int colunas;
    private int gradeX;
    private int gradeY;

    public TelaSigilo(BlockPos pos, List<String> sigilos) {
        super(Component.translatable("item.odteste.giz"));
        this.pos = pos;
        this.sigilos = sigilos;
    }

    @Override
    protected void init() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.2f, 0.4f));
        colunas = Math.max(1, Math.min(COLUNAS_MAX, sigilos.size()));
        int linhas = (sigilos.size() + colunas - 1) / colunas;
        gradeX = (width - colunas * CELULA) / 2;
        gradeY = Math.max(40, (height - linhas * CELULA) / 2);
        if (!sigilos.isEmpty()) {
            addRenderableWidget(Button.builder(textoGiro(), b -> {
                giro = (giro + 1) % RegrasSigilos.GIROS;
                b.setMessage(textoGiro());
            }).bounds(width / 2 - 50, gradeY + linhas * CELULA + 10, 100, 20).build());
        }
    }

    private Component textoGiro() {
        return Component.translatable("odteste.sigilo.giro." + giro);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float parcial) {
        super.render(g, mouseX, mouseY, parcial); // fundo escurecido e botões
        g.drawCenteredString(font, title, width / 2, 20, 0xFFE0D8C8);
        if (sigilos.isEmpty()) {
            g.drawCenteredString(font, Component.translatable("odteste.sigilo.nenhum"), width / 2, height / 2, 0xFF9A9A9A);
            return;
        }
        String sobre = null;
        for (int i = 0; i < sigilos.size(); i++) {
            int x = gradeX + (i % colunas) * CELULA;
            int y = gradeY + (i / colunas) * CELULA;
            boolean dentro = mouseX >= x && mouseX < x + CELULA && mouseY >= y && mouseY < y + CELULA;
            g.fill(x + 1, y + 1, x + CELULA - 1, y + CELULA - 1, dentro ? 0x80806040 : 0x60000000);
            ResourceLocation tex = ResourceLocation.fromNamespaceAndPath(OdMod.MODID,
                    "textures/block/sigilo/" + sigilos.get(i) + ".png");
            g.blit(tex, x + (CELULA - ICONE) / 2, y + (CELULA - ICONE) / 2, ICONE, ICONE, 0, 0, 16, 16, 16, 16);
            if (dentro) sobre = sigilos.get(i);
        }
        if (sobre != null) {
            g.renderTooltip(font, Component.translatable("odteste.sigilo." + sobre), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int botao) {
        if (super.mouseClicked(mx, my, botao)) return true;
        if (botao != 0) return false;
        for (int i = 0; i < sigilos.size(); i++) {
            int x = gradeX + (i % colunas) * CELULA;
            int y = gradeY + (i / colunas) * CELULA;
            if (mx >= x && mx < x + CELULA && my >= y && my < y + CELULA) {
                PacketDistributor.sendToServer(new DesenharSigilo(pos, sigilos.get(i), giro));
                onClose();
                return true;
            }
        }
        return false;
    }
}
