package com.nikolas.odteste.sigilos;

import java.util.List;

/**
 * IDs do giz e dos sigilos (todos em docs/ids.md). Java puro, para o teste conferir. **Nenhum nome daqui some nem
 * muda**: o mundo guarda sigilos riscados pelo nome. A ordem também não muda (novos entram no fim).
 */
public final class IdsSigilos {
    /** Item Giz (um só, sem cor por elemento). */
    public static final String ITEM_GIZ = "giz";
    /** Bloco fino riscado no chão; o desenho está na propriedade {@link #PROPRIEDADE_SIMBOLO}. */
    public static final String BLOCO_SIGILO = "sigilo";
    public static final String PROPRIEDADE_SIMBOLO = "simbolo";
    public static final String PROPRIEDADE_GIRO = "giro";
    /** Pacote servidor → jogador: abre a tela do giz (posição e sigilos aprendidos). */
    public static final String PACOTE_ABRIR = "abrir_tela_sigilo";
    /** Pacote jogador → servidor: pede para riscar um sigilo. O servidor confere tudo de novo. */
    public static final String PACOTE_DESENHAR = "desenhar_sigilo";

    /** Os 42 desenhos da arte v1 (Modelador): letras, dígitos, elementos e o do poder. */
    public static final List<String> SIMBOLOS = List.of(
            "letra_a", "letra_b", "letra_c", "letra_d", "letra_e", "letra_f", "letra_g", "letra_h", "letra_i",
            "letra_j", "letra_k", "letra_l", "letra_m", "letra_n", "letra_o", "letra_p", "letra_q", "letra_r",
            "letra_s", "letra_t", "letra_u", "letra_v", "letra_w", "letra_x", "letra_y", "letra_z",
            "digito_0", "digito_1", "digito_2", "digito_3", "digito_4", "digito_5", "digito_6", "digito_7",
            "digito_8", "digito_9",
            "elemento_sangue", "elemento_morte", "elemento_conhecimento", "elemento_energia", "elemento_medo",
            "ritual_poder");

    /** IDs com {@code odteste:} na frente (o dado do jogador está em IdsDados). */
    public static final List<String> OUTROS = List.of(ITEM_GIZ, BLOCO_SIGILO, PACOTE_ABRIR, PACOTE_DESENHAR);

    private IdsSigilos() {}
}
