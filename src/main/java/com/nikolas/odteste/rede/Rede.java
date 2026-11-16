package com.nikolas.odteste.rede;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/**
 * Pacotes do mod. Só manda em momentos raros (mudou de área, começou um susto): nada por tick.
 * Nenhum valor do jogador vai para o cliente hoje (nenhum efeito de cliente usa Exposição ou Lucidez).
 */
public final class Rede {
    /** Versão do protocolo. Mudou o formato de algum pacote? Suba o número. 9 = Gabinete do Limiar; 10 = giz, sigilos e contorno. */
    private static final String VERSAO = "10";

    private Rede() {}

    public static void iniciar(IEventBus barramentoDoMod) {
        barramentoDoMod.addListener(Rede::registrarPacotes);
    }

    private static void registrarPacotes(RegisterPayloadHandlersEvent evento) {
        evento.registrar(VERSAO)
                .playToClient(AreaHorror.TIPO, AreaHorror.CODEC, AreaHorror::receber)
                .playToClient(Susto.TIPO, Susto.CODEC, Susto::receber)
                .playToClient(AbrirTelaSigilo.TIPO, AbrirTelaSigilo.CODEC, AbrirTelaSigilo::receber)
                .playToServer(DesenharSigilo.TIPO, DesenharSigilo.CODEC, DesenharSigilo::receber)
                .playToClient(Contorno.TIPO, Contorno.CODEC, Contorno::receber);
    }
}
