package com.nikolas.odteste.sistemas;

import net.minecraft.server.level.ServerPlayer;

/**
 * O que acontece quando um valor do jogador muda, venha de onde vier (ganho automático ou comando de admin).
 * Chamado só no servidor, por DadosJogador.
 */
public final class Reacoes {
    private Reacoes() {}

    public static void lucidezMudou(ServerPlayer p, int antes, int depois) {
        int max = com.nikolas.odteste.dados.DadosJogador.lucidezMax(p);
        if (FaixaLucidez.de(antes, max) != FaixaLucidez.de(depois, max)) Lucidez.atualizarLentidao(p);
        if (depois == 0 && antes > 0) Lucidez.surto(p);
    }
}
