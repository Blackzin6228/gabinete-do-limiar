package com.nikolas.odteste.artefatos;

/**
 * Números dos artefatos (docs/reliquias-v1.md). Java puro, testado em RegrasArtefatosTest.
 * São proposta para o teste no jogo.
 */
public final class RegrasArtefatos {
    /** Checagem do portador: a cada 5 s, só dele (o tick é espalhado pelo id do jogador). */
    public static final int INTERVALO_TICKS = 100;
    /** Teto de criaturas tocadas por uma checagem ou um pulso. */
    public static final int TETO_CRIATURAS = 16;

    /** Chance padrão de um artefato aparecer num baú que combina (config do servidor). */
    public static final double CHANCE_BAU_PADRAO = 0.02;

    // Seringa de Coleta Ritual
    public static final float SERINGA_VIDA = 2f; // 1 coração
    public static final int SERINGA_FORCA_TICKS = 600; // 30 s
    public static final int SERINGA_RECARGA_TICKS = 600;
    public static final int SERINGA_EXPOSICAO = 1;

    // Luva de Abate Ritual
    public static final int LUVA_SANGRAR_TICKS = 60;
    public static final float LUVA_FOME = 1.5f; // cansaço (exaustão) por acerto

    // Medalha do Sangue Doado
    public static final float MEDALHA_VIDA = 6f; // abaixo de 3 corações
    public static final int MEDALHA_LUCIDEZ = 1;

    // Óculos de Visão Paranormal
    public static final double OCULOS_RAIO = 16;
    public static final int OCULOS_CONTORNO_TICKS = 120; // cobre o intervalo da checagem
    public static final float OCULOS_EXPOSICAO = 0.2f; // intensidade do contato a cada checagem
    /** Uma visão (som) a cada N checagens, em média. */
    public static final int OCULOS_VISAO_UMA_EM = 8;

    // Moeda de Óbito
    public static final float MOEDA_VIDA_QUE_FICA = 1f; // meio coração
    public static final int MOEDA_ESQUECER_TICKS = 100; // 5 s
    public static final double MOEDA_RAIO = 24;
    public static final int MOEDA_LUCIDEZ = 5;

    // Vela de Rito Fúnebre
    public static final double VELA_RAIO = 8;
    public static final int VELA_FRAQUEZA_TICKS = 120;
    public static final int VELA_GASTO = 1;
    public static final int VELA_LUCIDEZ = 1;

    // Lupa de Investigação Paranormal (e a Espada de Conhecimento com livro): conta como um degrau a mais de Exposição ao ler
    public static final int LEITURA_BONUS = 20;
    public static final int LUPA_LUCIDEZ = 2;

    // Celular de Sinal Anômalo
    public static final int SINAL_ENERGIA = 100;
    public static final int SINAL_ITENS_MAX = 4;
    public static final double SINAL_RAIO_PULSO = 5;
    public static final int SINAL_TONTEIRA_TICKS = 60;
    public static final int SINAL_GASTO_PULSO = 8;
    public static final int SINAL_RECARGA_TICKS = 200;
    public static final int SINAL_TOCA_UMA_EM = 12;

    // Rádio de Interferência
    public static final double RADIO_RAIO = 16;
    public static final int RADIO_SILENCIO_UMA_EM = 6;
    public static final int RADIO_LUCIDEZ = 1;

    private RegrasArtefatos() {}

    /** Este é o tick de checar este jogador? Espalha os jogadores pelos 5 s (nunca todos no mesmo tick). */
    public static boolean vezDeChecar(int tick, int idJogador) {
        return Math.floorMod(tick + idJogador, INTERVALO_TICKS) == 0;
    }

    /** O poder "1x por noite" já foi usado nesta noite? ({@code usada} &lt; 0 = nunca usado) */
    public static boolean usadoNestaNoite(long usada, long noiteAtual) {
        return usada >= 0 && usada == noiteAtual;
    }

    /** Chance da config presa entre 0 e 1 (arquivo editado à mão não quebra o loot). */
    public static double chance(double daConfig) {
        if (Double.isNaN(daConfig)) return CHANCE_BAU_PADRAO;
        return Math.max(0, Math.min(1, daConfig));
    }

    /** Volume do chiado do Rádio: mais perto do perigo, mais alto (0,15 a 1). */
    public static float volumeChiado(double distancia) {
        double d = Math.max(0, Math.min(RADIO_RAIO, distancia));
        return (float) (0.15 + 0.85 * (1 - d / RADIO_RAIO));
    }
}
