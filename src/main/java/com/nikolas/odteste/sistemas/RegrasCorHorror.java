package com.nikolas.odteste.sistemas;

import com.nikolas.odteste.horror.RegrasCamadaHorror;

/**
 * Números do efeito de tela {@code cor_horror} (decisão de 14/11/2026, camada de horror: forte no mundo inteiro,
 * de dia também). Java puro, testado em RegrasCorHorrorTest; o cliente (cliente/FxCorHorror) usa as mesmas contas.
 *
 * <ul>
 * <li>Vale no overworld inteiro, de dia (forte) e de noite (mais pesado). Nether, End e outras: nada.</li>
 * <li>Um efeito de cor mais forte por cima (no mod antigo, o carmesim da Vila Calada) tira o cor_horror na mesma
 * medida. Hoje não há nenhum: o cliente passa 0.</li>
 * <li>{@code efeitos_fortes} diminui (metade no DESLIGADO), não desliga.</li>
 * </ul>
 */
public final class RegrasCorHorror {
    /** Entrar e sair da noite leva uns 5 s (fração por tick), igual à camada de horror. */
    public static final float PASSO = 1f / 100f;

    private RegrasCorHorror() {}

    /** Alvo da parte "noite" pela hora do mundo (0 ou 1; o cliente suaviza). */
    public static float noiteAlvo(boolean overworld, long horaDoDia) {
        return overworld && RegrasCamadaHorror.noite(horaDoDia) ? 1f : 0f;
    }

    /** Força-padrão de dia (sem a camada de horror do cliente): 1 no overworld, 0 fora. */
    public static float diaPadrao(boolean overworld) {
        return overworld ? 1f : 0f;
    }

    /**
     * Força que vai para o shader (uniform {@code Forca}).
     *
     * @param dia      0..1, quanto a camada vale aqui (1 no overworld)
     * @param porCima  0..1, quanto um efeito de cor mais forte está ligado (o cor_horror sai na mesma medida)
     * @param tremor   multiplicador do {@code efeitos_fortes} (1 / 0,5 / 0,25)
     */
    public static float forca(float dia, float porCima, float tremor) {
        float d = Math.max(0f, Math.min(1f, dia));
        float c = Math.max(0f, Math.min(1f, porCima));
        return d * (1f - c) * forcaVisual(tremor);
    }

    /** {@code efeitos_fortes} nos efeitos de tela: diminui até a metade, nunca desliga. */
    public static float forcaVisual(float tremor) {
        return 0.5f + 0.5f * Math.max(0f, Math.min(1f, tremor));
    }

    public static float aproximar(float atual, float alvo, float passo) {
        if (atual < alvo) return Math.min(alvo, atual + passo);
        return Math.max(alvo, atual - passo);
    }
}
