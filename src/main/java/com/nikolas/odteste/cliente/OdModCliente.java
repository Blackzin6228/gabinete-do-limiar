package com.nikolas.odteste.cliente;

import com.nikolas.odteste.OdMod;
import com.nikolas.odteste.cracha.CrachaItem;
import com.nikolas.odteste.horror.IdsHorror;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Entrada do lado do cliente. Com {@code dist = Dist.CLIENT}, o NeoForge nem carrega esta classe
 * num servidor dedicado: nada de cliente/ chega lá.
 */
@Mod(value = OdMod.MODID, dist = Dist.CLIENT)
public final class OdModCliente {

    public OdModCliente(IEventBus barramento, ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, ConfigCliente.SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        // Vinheta, escurecer e tremor dos sustos (por cima de tudo).
        barramento.addListener((RegisterGuiLayersEvent e) -> e.registerAboveAll(
                ResourceLocation.fromNamespaceAndPath(OdMod.MODID, IdsHorror.CAMADA_TELA), Tela::desenhar));
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post e) -> Tela.aoTick());
        NeoForge.EVENT_BUS.addListener(Tela::aoCalcularCamera);
        NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut e) -> Tela.limpar());

        // Crachá: o item (código comum) abre a tela por este gancho.
        CrachaItem.abrirTela = CrachaCliente::abrir;

        // Contorno do paranormal (Óculos de Velório, Sentença): só nesta tela.
        ContornoCliente.iniciar();
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post e) -> ContornoCliente.aoTick());
        NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut e) -> ContornoCliente.limpar());

        // Camada de horror: céu cinza e névoa de dia, noite escura, música cortada e sustos grandes.
        CamadaHorrorCliente.iniciar(barramento);
        // Tom do mundo (shader cor_horror; funciona com shader pack do Iris).
        FxHorror.iniciar(barramento);
        OdMod.LOGGER.info("{}: efeitos do cliente ligados.", OdMod.MODID);
    }
}
