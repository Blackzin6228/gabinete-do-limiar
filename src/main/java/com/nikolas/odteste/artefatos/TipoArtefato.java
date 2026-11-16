package com.nikolas.odteste.artefatos;

import com.nikolas.odteste.elementos.Elemento;

/**
 * Os 12 artefatos (nomes e IDs em docs/nomes-pacote-1.md). Java puro (testável). O {@link #id} é o ID do item:
 * nunca muda. Qualquer jogador usa. {@link #emBreve} = o poder depende de algo que ainda não existe (magias, máquinas,
 * verdades): o item existe, a descrição diz "em breve" e o efeito não roda.
 * {@link #durabilidade} > 0 = o artefato se gasta (Vela, Celular).
 */
public enum TipoArtefato {
    SERINGA_DE_COLETA_RITUAL("seringa_de_coleta_ritual", Elemento.SANGUE, false, 0),
    LUVA_DE_ABATE_RITUAL("luva_de_abate_ritual", Elemento.SANGUE, false, 0),
    MEDALHA_DO_SANGUE_DOADO("medalha_do_sangue_doado", Elemento.SANGUE, false, 0),
    OCULOS_DE_VISAO_PARANORMAL("oculos_de_visao_paranormal", Elemento.MORTE, false, 0),
    MOEDA_DE_OBITO("moeda_de_obito", Elemento.MORTE, false, 0),
    VELA_DE_RITO_FUNEBRE("vela_de_rito_funebre", Elemento.MORTE, false, 240),
    LUPA_DE_INVESTIGACAO_PARANORMAL("lupa_de_investigacao_paranormal", Elemento.CONHECIMENTO, false, 0),
    CANETA_DE_REGISTRO_APAGADO("caneta_de_registro_apagado", Elemento.CONHECIMENTO, true, 0),
    FITA_DE_GRAVACAO_ANOMALA("fita_de_gravacao_anomala", Elemento.CONHECIMENTO, true, 0),
    CELULAR_DE_SINAL_ANOMALO("celular_de_sinal_anomalo", Elemento.ENERGIA, false, 320),
    FUSIVEL_SOBRECARREGADO("fusivel_sobrecarregado", Elemento.ENERGIA, true, 0),
    RADIO_DE_INTERFERENCIA("radio_de_interferencia", Elemento.ENERGIA, false, 0);

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
