package com.nikolas.odteste.horror;

import java.util.List;

/** IDs da camada de horror (todos em docs/ids.md). Java puro. */
public final class IdsHorror {
    /** Pacote servidor → cliente: em que área da história o jogador está ("" = nenhuma). */
    public static final String PACOTE_AREA = "area_horror";
    /** Pacote servidor → cliente: começa um susto grande (tipo pelo id de {@link TipoSusto}). */
    public static final String PACOTE_SUSTO = "susto";
    /** Camada da HUD: véu escuro de reserva (quando a luz da noite não é nossa). */
    public static final String CAMADA_ESCURO = "noite_escura";
    /** Camada da HUD com vinheta, escurecer e tremor dos sustos. */
    public static final String CAMADA_TELA = "tela_paranormal";
    /** Tag vanilla do jogador: quem tem nunca leva susto sozinho ({@code /tag <nome> add odteste_mestre}). */
    public static final String TAG_MESTRE = "odteste_mestre";

    /** Os que levam {@code odteste:} na frente (a tag do mestre não leva). */
    public static final List<String> TODOS = List.of(PACOTE_AREA, PACOTE_SUSTO, CAMADA_ESCURO, CAMADA_TELA);

    private IdsHorror() {}
}
