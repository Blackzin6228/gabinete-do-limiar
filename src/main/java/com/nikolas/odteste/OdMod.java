package com.nikolas.odteste;

import com.mojang.logging.LogUtils;
import com.nikolas.odteste.comandos.ComandosAdmin;
import com.nikolas.odteste.comandos.ComandosHistoria;
import com.nikolas.odteste.compat.Compat;
import com.nikolas.odteste.dados.DadosGuardados;
import com.nikolas.odteste.dados.DadosJogador;
import com.nikolas.odteste.historia.mestre.Mestre;
import com.nikolas.odteste.horror.CamadaHorror;
import com.nikolas.odteste.rede.Rede;
import com.nikolas.odteste.registro.Registro;
import com.nikolas.odteste.sistemas.Vigia;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

/**
 * Classe principal do Gabinete do Limiar. Só liga os sistemas: cada sistema mora na sua pasta e se registra sozinho.
 * O mod id continua {@code odteste} (o do mod antigo): os dados do mundo do servidor dependem dele.
 */
@Mod(OdMod.MODID)
public final class OdMod {
    /** Vem do gradle.properties. Nunca muda (o mundo do servidor já tem dados com ele). */
    public static final String MODID = "odteste";
    public static final Logger LOGGER = LogUtils.getLogger();

    public OdMod(IEventBus barramento, ModContainer container) {
        DadosJogador.iniciar();
        DadosGuardados.iniciar();
        Registro.registrar(barramento);
        Rede.iniciar(barramento);
        ComandosAdmin.iniciar();
        ComandosHistoria.iniciar();
        Mestre.iniciar();
        Vigia.iniciar();
        CamadaHorror.iniciar();
        Compat.iniciar(barramento);
        LOGGER.info("{} carregado. Integrações ligadas: {}", MODID, Compat.ligadas());
    }
}
