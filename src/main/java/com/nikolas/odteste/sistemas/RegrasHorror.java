package com.nikolas.odteste.sistemas;

/**
 * Faixas do horror (docs do mod antigo, horror.md) usadas pelas frases de /exposicao e /lucidez e pelos sustos.
 * Java puro, testado em RegrasHorrorTest. As alucinações do mod antigo ficaram de fora do Gabinete do Limiar.
 */
public final class RegrasHorror {
    /** "Grupo por perto": outro jogador a menos de 8 blocos. */
    public static final double GRUPO_RAIO_SQ = 8 * 8;

    /** Gerador de sorteio: devolve de 0 até {@code limite - 1}. No jogo é o RandomSource; no teste, java.util.Random. */
    @FunctionalInterface
    public interface Sorteio {
        int ate(int limite);
    }

    private RegrasHorror() {}

    /** Índice da faixa de Exposição: 0 Comum, 1 Baixa, 2 Média, 3 Alta, 4 Crítica. */
    public static int faixa(Degrau d) {
        if (d == null) return 0;
        return switch (d) {
            case COMUM -> 0;
            case TOCADO -> 1;
            case MARCADO -> 2;
            case FUNDO -> 3;
            case A_BEIRA, ATRAVESSADO -> 4;
        };
    }

    /** Índice da faixa de Lucidez: 0 Firme, 1 Alta, 2 Média, 3 Baixa (o Surto conta como Baixa). */
    public static int faixa(FaixaLucidez f) {
        if (f == null) return 0;
        return Math.min(3, f.gravidade());
    }
}
