package com.nikolas.odteste.sigilos;

import java.util.Optional;

/**
 * Regras do efeito dos sigilos de elemento, sem nada do Minecraft (tem teste): quem o efeito alcança, limites e espera.
 * Os números aqui são só os padrões da config do servidor ({@link ConfigSigilos}); nada disso aparece na tela do jogador.
 */
public final class RegrasEfeitoSigilo {
    public static final int RAIO_PADRAO = 5;
    /** Teto do raio na config: a busca de criaturas é uma caixa, então raio grande custa caro. */
    public static final int RAIO_MAX = 12;
    public static final int DURACAO_PADRAO_S = 10;
    /** Custo padrão de Sanidade (mesma escala do {@code Abalo}: uma aparição tira 10). */
    public static final int CUSTO_PADRAO = 8;
    public static final int ESPERA_PADRAO_S = 30;
    public static final int MAX_ALVOS_PADRAO = 48;
    /** Efeito de atordoamento da Energia (Lentidão forte + Fraqueza), em ticks: 3 s. */
    public static final int ATORDOAR_TICKS = 3 * 20;
    /** Visão Noturna abaixo de 10 s pisca; por isso a duração dela nunca fica abaixo disto. */
    public static final int VISAO_NOTURNA_MIN_TICKS = 221;

    /** Os cinco efeitos. O nome do sigilo é {@code sigilo_de_<id>}. */
    public enum Efeito {
        SANGUE("sangue"), MORTE("morte"), CONHECIMENTO("conhecimento"), ENERGIA("energia"), MEDO("medo");

        public final String id;

        Efeito(String id) {
            this.id = id;
        }

        /** O efeito de um desenho do bloco; vazio para letras, dígitos e tipos de ritual (que não fazem nada sozinhos). */
        public static Optional<Efeito> deSimbolo(String simbolo) {
            if (simbolo == null) return Optional.empty();
            for (Efeito e : values()) {
                if (simbolo.equals("sigilo_de_" + e.id)) return Optional.of(e);
            }
            return Optional.empty();
        }
    }

    /** Que tipo de ser está na área, do ponto de vista do efeito. */
    public enum Alvo {
        /** Quem clicou no sigilo. */
        QUEM_ATIVOU,
        /** Outro jogador (nunca espectador). */
        JOGADOR,
        /** Criatura hostil (monstro). */
        CRIATURA_HOSTIL,
        /** Criatura que não é hostil nem tem dono (animal, aldeão). */
        CRIATURA,
        /** O resto: bichos de estimação com dono, suporte de armadura... */
        OUTRO
    }

    private RegrasEfeitoSigilo() {}

    public static int raio(int pedido) {
        return Math.max(1, Math.min(RAIO_MAX, pedido));
    }

    /**
     * O efeito do sigilo cai nesse alvo? Em jogador, só em quem não é do mesmo grupo do que ativou (o Medo não
     * escolhe lado e pega até em quem ativou). Os bônus de quem ativou (Força, Velocidade, Visão Noturna) não passam por aqui.
     */
    public static boolean afeta(Efeito efeito, Alvo alvo, boolean mesmoGrupo) {
        if (efeito == null || alvo == null) return false;
        if (efeito == Efeito.MEDO) return alvo != Alvo.OUTRO;
        return switch (alvo) {
            case QUEM_ATIVOU -> false;
            case JOGADOR -> efeito != Efeito.SANGUE && !mesmoGrupo;
            case CRIATURA_HOSTIL -> true;
            case CRIATURA -> efeito != Efeito.SANGUE;
            case OUTRO -> efeito == Efeito.CONHECIMENTO;
        };
    }

    /** Já passou a espera desde a última ativação? ({@code ultimo} negativo = nunca ativou.) */
    public static boolean livre(long ultimo, long agora, long espera) {
        return ultimo < 0 || agora < ultimo || agora - ultimo >= espera;
    }

    /** Ainda cabe mais um alvo? */
    public static boolean cabeMais(int jaAfetados, int teto) {
        return jaAfetados < Math.max(1, teto);
    }

    /** Duração da Visão Noturna de quem ativou. */
    public static int duracaoVisaoNoturna(int duracaoTicks) {
        return Math.max(duracaoTicks, VISAO_NOTURNA_MIN_TICKS);
    }
}
