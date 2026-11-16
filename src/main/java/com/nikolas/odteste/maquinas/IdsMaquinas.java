package com.nikolas.odteste.maquinas;

import java.util.List;

/** IDs das falhas de máquina (todos em docs/ids.md). Java puro. */
public final class IdsMaquinas {
    /** Tag de item (vazia): quem leva um item dela no inventário tem menos chance de falha (Fusível Queimado). */
    public static final String TAG_REDUZ_FALHA = "reduz_falha_maquina";
    /** Chaves da config do servidor ({@link ConfigMaquinas#ARQUIVO}, seção {@link ConfigMaquinas#SECAO}). */
    public static final List<String> CHAVES_CONFIG = List.of(
            "ligado", "chance_percentual", "intervalo_maquina_s", "espera_jogador_s", "raio",
            "chance_susto_percentual", "fator_protecao_percentual");

    private IdsMaquinas() {}
}
