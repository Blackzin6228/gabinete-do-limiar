package com.nikolas.odteste.artefatos;

import com.nikolas.odteste.elementos.Elemento;

/**
 * Os 12 artefatos de docs/reliquias-v1.md. Java puro (testável). O {@link #id} é o ID do item: nunca muda.
 * Qualquer jogador usa. {@link #emBreve} = o poder depende de algo que ainda não existe (magias, máquinas,
 * verdades): o item existe, a descrição diz "em breve" e o efeito não roda.
 * {@link #durabilidade} > 0 = o artefato se gasta (Vela, Barra de Sinal).
 */
public enum TipoArtefato {
    SERINGA_VELHA("seringa_velha", Elemento.SANGUE, false, 0),
    LUVA_DE_ACOUGUEIRO("luva_de_acougueiro", Elemento.SANGUE, false, 0),
    MEDALHA_DE_DOADOR("medalha_de_doador", Elemento.SANGUE, false, 0),
    OCULOS_DE_VELORIO("oculos_de_velorio", Elemento.MORTE, false, 0),
    MOEDA_DE_DEFUNTO("moeda_de_defunto", Elemento.MORTE, false, 0),
    VELA_DE_SETIMO_DIA("vela_de_setimo_dia", Elemento.MORTE, false, 240),
    LUPA_DO_ARQUIVO_MORTO("lupa_do_arquivo_morto", Elemento.CONHECIMENTO, false, 0),
    CANETA_SEM_TINTA("caneta_sem_tinta", Elemento.CONHECIMENTO, true, 0),
    FITA_SEM_ETIQUETA("fita_sem_etiqueta", Elemento.CONHECIMENTO, true, 0),
    UMA_BARRA_DE_SINAL("uma_barra_de_sinal", Elemento.ENERGIA, false, 320),
    FUSIVEL_QUEIMADO("fusivel_queimado", Elemento.ENERGIA, true, 0),
    RADINHO_DE_PILHA("radinho_de_pilha", Elemento.ENERGIA, false, 0);

    public final String id;
    public final Elemento elemento;
    public final boolean emBreve;
    public final int durabilidade;

    TipoArtefato(String id, Elemento elemento, boolean emBreve, int durabilidade) {
        this.id = id;
        this.elemento = elemento;
        this.emBreve = emBreve;
        this.durabilidade = durabilidade;
    }
}
