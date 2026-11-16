package com.nikolas.odteste.reliquias;

import com.nikolas.odteste.elementos.Elemento;

/**
 * As 4 espadas (nomes e IDs em docs/nomes-pacote-1.md). Java puro (testável). O {@link #id} é o ID do item: nunca muda.
 * Só funcionam para quem é do elemento delas; para os outros, o golpe vale como um soco e nada acontece.
 */
public enum TipoEspada {
    FACAO_DE_RITUAL_DE_SANGUE("facao_de_ritual_de_sangue", Elemento.SANGUE, 4, -2.0f),
    SABRE_DE_RITUAL_DE_MORTE("sabre_de_ritual_de_morte", Elemento.MORTE, 3, -2.4f),
    ESPADA_DE_RITUAL_DE_CONHECIMENTO("espada_de_ritual_de_conhecimento", Elemento.CONHECIMENTO, 3, -2.6f),
    ESPETO_DE_RITUAL_DE_ENERGIA("espeto_de_ritual_de_energia", Elemento.ENERGIA, 2, -2.2f);

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
