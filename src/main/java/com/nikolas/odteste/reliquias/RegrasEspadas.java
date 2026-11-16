package com.nikolas.odteste.reliquias;

import com.nikolas.odteste.elementos.Elemento;

/**
 * Números das 4 espadas (docs/reliquias-v1.md). Java puro, testado em RegrasEspadasTest.
 * São proposta para o teste no jogo: mudar aqui muda a espada.
 */
public final class RegrasEspadas {
    /** Golpe de quem não é do elemento: vale como um soco (sem dano extra e sem efeito). */
    public static final float DANO_FORA_DO_ELEMENTO = 1f;

    // Sede de Domingo
    public static final int SEDE_ACERTOS_PARA_CURAR = 4;
    public static final float SEDE_CURA = 2f;            // 1 coração
    public static final float SEDE_CURA_MAX_JANELA = 4f; // 2 corações...
    public static final long SEDE_JANELA_TICKS = 600;    // ...a cada 30 s
    public static final int SANGRAR_TICKS = 80;          // 4 s de dano leve
    /** Acertos seguidos no mesmo alvo para começar a sangrar. */
    public static final int SEDE_SEGUIDOS_PARA_SANGRAR = 2;

    // Sabre do Último Adeus
    /** "Pouca vida": até 25% da vida máxima. */
    public static final float SABRE_POUCA_VIDA = 0.25f;
    public static final int SABRE_RECARGA_TICKS = 1200; // 60 s

    // Sentença Sem Recurso
    public static final float SENTENCA_MULTIPLICADOR = 1.5f;
    public static final int CONTORNO_TICKS = 200; // o contorno dura 10 s e é renovado a cada golpe no julgado

    // Espeto da Casa Cheia
    public static final int ESPETO_CARGA_CHEIA = 6;
    public static final int ESPETO_ALVOS = 3;
    public static final double ESPETO_RAIO = 4.0;
    public static final float ESPETO_DANO_DESCARGA = 4f;
    public static final int ESPETO_TONTEIRA_TICKS = 60; // 3 s
    /** Energia dada a cada item com bateria, a cada 5 s, no máximo em 4 itens. */
    public static final int ESPETO_ENERGIA = 200;
    public static final int ESPETO_ITENS_MAX = 4;
    public static final double ALIADO_RAIO = 8.0;

    private RegrasEspadas() {}

    /** A espada funciona para este portador? Só para quem é do elemento dela (nenhum nunca é). */
    public static boolean funciona(Elemento portador, TipoEspada espada) {
        return portador != null && portador != Elemento.NENHUM && portador == espada.elemento;
    }

    /** Sabre: o alvo está com pouca vida para "o adeus"? */
    public static boolean adeus(float vida, float vidaMax) {
        return vidaMax > 0 && vida > 0 && vida <= vidaMax * SABRE_POUCA_VIDA;
    }

    /** Espeto: carga depois de um acerto. Com aliado do grupo perto, enche o dobro. */
    public static int carregar(int carga, boolean aliadoPerto) {
        return Math.min(ESPETO_CARGA_CHEIA, Math.max(0, carga) + (aliadoPerto ? 2 : 1));
    }

    public static boolean cheia(int carga) {
        return carga >= ESPETO_CARGA_CHEIA;
    }

    /**
     * Freio da cura da Sede: no máximo {@link #SEDE_CURA_MAX_JANELA} por {@link #SEDE_JANELA_TICKS}.
     * Só memória (reiniciar zera: no pior caso, um coração a mais).
     */
    public static final class FreioCura {
        private long inicio = Long.MIN_VALUE / 2; // metade: "agora - inicio" não estoura
        private float curado;

        /** Quanto pode curar agora (0 = já curou o máximo da janela) e já conta como curado. */
        public float curar(long agora, float quanto) {
            if (agora < inicio || agora - inicio >= SEDE_JANELA_TICKS) {
                inicio = agora;
                curado = 0;
            }
            float pode = Math.max(0, Math.min(quanto, SEDE_CURA_MAX_JANELA - curado));
            curado += pode;
            return pode;
        }
    }
}
