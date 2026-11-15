package com.nikolas.odteste.cliente;

import com.nikolas.odteste.OdMod;
import com.nikolas.odteste.cracha.CrachaDados;
import com.nikolas.odteste.cracha.RegrasCracha;
import com.nikolas.odteste.elementos.Elemento;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

/**
 * Tela do cartão do crachá: textura 160x100 centralizada, na maior escala inteira que couber, fundo escurecido.
 * ESC fecha. O texto usa a fonte do Minecraft e escala junto com o cartão.
 */
public final class TelaCracha extends Screen {
    private final CrachaDados dados;
    private final RegrasCracha.Cartao cartao;

    public TelaCracha(CrachaDados dados) {
        super(Component.translatable("item.odteste.cracha"));
        this.dados = dados;
        this.cartao = RegrasCracha.escolher(dados.mestre(), dados.revelado());
    }

    @Override
    protected void init() {
        // Som de página de livro, baixo.
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0f, 0.4f));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float parcial) {
        super.render(g, mouseX, mouseY, parcial); // fundo escurecido
        int e = RegrasCracha.escala(width, height);
        int x = (width - RegrasCracha.LARGURA * e) / 2;
        int y = (height - RegrasCracha.ALTURA * e) / 2;
        ResourceLocation textura = ResourceLocation.fromNamespaceAndPath(
                OdMod.MODID, "textures/gui/" + cartao.textura() + ".png");
        g.blit(textura, x, y, RegrasCracha.LARGURA * e, RegrasCracha.ALTURA * e, 0, 0,
                RegrasCracha.LARGURA, RegrasCracha.ALTURA, RegrasCracha.LARGURA, RegrasCracha.ALTURA);

        g.pose().pushPose();
        g.pose().translate(x, y, 0);
        g.pose().scale(e, e, 1);
        g.drawString(font, dados.nome(), RegrasCracha.NOME_X, RegrasCracha.NOME_Y,
                0xFF000000 | RegrasCracha.COR_NOME, false);
        if (cartao == RegrasCracha.Cartao.LIMPO) {
            Elemento el = Elemento.deIdOuNenhum(dados.elemento());
            if (el != Elemento.NENHUM) {
                g.drawString(font, Component.translatable(el.chaveTraducao()), RegrasCracha.ELEMENTO_X,
                        RegrasCracha.ELEMENTO_Y, 0xFF000000 | RegrasCracha.COR_ELEMENTO, false);
            }
        }
        g.pose().popPose();
    }
}
