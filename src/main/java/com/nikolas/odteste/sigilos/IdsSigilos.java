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
    /** Arquivo da config do servidor com raio, duração, custo e espera do efeito dos sigilos de elemento. */
    public static final String ARQUIVO_CONFIG = "odteste-sigilos-server.toml";
    /** Pacote servidor → jogador: abre a tela do giz (posição e sigilos aprendidos). */
    public static final String PACOTE_ABRIR = "abrir_tela_sigilo";
    /** Pacote jogador → servidor: pede para riscar um sigilo. O servidor confere tudo de novo. */
    public static final String PACOTE_DESENHAR = "desenhar_sigilo";

    /** Os desenhos do bloco (nomes da tabela docs/nomes-pacote-1.md): 26 letras, 10 dígitos, 5 elementos e 5 tipos de ritual. */
    public static final List<String> SIMBOLOS = List.of(
            "sigilo_a", "sigilo_b", "sigilo_c", "sigilo_d", "sigilo_e",
            "sigilo_f", "sigilo_g", "sigilo_h", "sigilo_i", "sigilo_j",
            "sigilo_k", "sigilo_l", "sigilo_m", "sigilo_n", "sigilo_o",
            "sigilo_p", "sigilo_q", "sigilo_r", "sigilo_s", "sigilo_t",
            "sigilo_u", "sigilo_v", "sigilo_w", "sigilo_x", "sigilo_y",
            "sigilo_z", "sigilo_0", "sigilo_1", "sigilo_2", "sigilo_3",
            "sigilo_4", "sigilo_5", "sigilo_6", "sigilo_7", "sigilo_8",
            "sigilo_9", "sigilo_de_sangue", "sigilo_de_morte", "sigilo_de_conhecimento", "sigilo_de_energia",
            "sigilo_de_medo", "sigilo_de_invocacao", "sigilo_de_protecao", "sigilo_de_banimento", "sigilo_de_passagem",
            "sigilo_de_revelacao");

    /** IDs com {@code odteste:} na frente (o dado do jogador está em IdsDados). */
    public static final List<String> OUTROS = List.of(ITEM_GIZ, BLOCO_SIGILO, PACOTE_ABRIR, PACOTE_DESENHAR);

    private IdsSigilos() {}
}
