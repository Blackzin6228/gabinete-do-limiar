package com.nikolas.odteste.reliquias;

import com.nikolas.odteste.elementos.Elemento;

/**
 * As 4 espadas de docs/reliquias-v1.md. Java puro (testável). O {@link #id} é o ID do item: nunca muda.
 * Só funcionam para quem é do elemento delas; para os outros, o golpe vale como um soco e nada acontece.
 */
public enum TipoEspada {
    SEDE_DE_DOMINGO("sede_de_domingo", Elemento.SANGUE, 4, -2.0f),
    SABRE_DO_ULTIMO_ADEUS("sabre_do_ultimo_adeus", Elemento.MORTE, 3, -2.4f),
    SENTENCA_SEM_RECURSO("sentenca_sem_recurso", Elemento.CONHECIMENTO, 3, -2.6f),
    ESPETO_DA_CASA_CHEIA("espeto_da_casa_cheia", Elemento.ENERGIA, 2, -2.2f);

    public final String id;
    public final Elemento elemento;
    /** Dano e velocidade somados ao material (netherite), como numa espada vanilla. */
    public final int dano;
    public final float velocidade;

    TipoEspada(String id, Elemento elemento, int dano, float velocidade) {
        this.id = id;
        this.elemento = elemento;
        this.dano = dano;
        this.velocidade = velocidade;
    }
}
