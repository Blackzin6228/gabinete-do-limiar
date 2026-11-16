package com.nikolas.odteste.dados;

import java.util.List;

/**
 * Nomes dos dados salvos no jogador e dos outros IDs do mod. Ficam aqui (Java puro) para o teste conferir
 * que todos estão em docs/ids.md. Vieram do mod antigo (Od Teste) e nunca mudam.
 */
public final class IdsDados {
    public static final String EXPOSICAO = "exposicao";
    public static final String LUCIDEZ = "lucidez";
    public static final String LUCIDEZ_MAX = "lucidez_max";
    /** Centésimos de Exposição guardados até completar 1 ponto (0–99). */
    public static final String EXPOSICAO_RESTO = "exposicao_resto";
    /** Contato secreto com cada elemento. */
    public static final String CONTATO_ELEMENTO = "contato_elemento";
    /** Freio de repetição da Exposição (fonte → início da janela e vezes). Salvo para relogar não zerar. */
    public static final String EXPOSICAO_REPETICAO = "exposicao_repeticao";
    /** Elemento do jogador, definido pelo admin (nenhum = sem elemento). Nunca vai para o cliente. */
    public static final String ELEMENTO = "elemento";
    /** Se a história já revelou o elemento ao jogador. Nunca vai para o cliente. */
    public static final String ELEMENTO_REVELADO = "elemento_revelado";

    /** O jogador já recebeu o kit (crachá e dossiê) na primeira entrada? */
    public static final String RECEBEU_KIT = "recebeu_kit";

    /** Dados em uso. */
    public static final List<String> TODOS = List.of(
            EXPOSICAO, LUCIDEZ, LUCIDEZ_MAX, EXPOSICAO_RESTO, CONTATO_ELEMENTO, EXPOSICAO_REPETICAO,
            ELEMENTO, ELEMENTO_REVELADO, RECEBEU_KIT);

    // ---------- guardados do mod antigo (DadosGuardados: registrados só para não apagar) ----------

    public static final String MARCA_ELEMENTO = "marca_elemento";
    public static final String MARCA_ESTAGIO = "marca_estagio";
    public static final String TRANSCENDER_NIVEL = "transcender_nivel";
    public static final String ARTEFATO_RECARGA = "artefato_recarga";
    public static final String CRIATURA_NOITE = "criatura_noite";
    public static final String CANSACO_ELEMENTO = "cansaco_elemento";
    public static final String CENAS_VISTAS = "cenas_vistas";

    public static final List<String> GUARDADOS = List.of(
            MARCA_ELEMENTO, MARCA_ESTAGIO, TRANSCENDER_NIVEL, ARTEFATO_RECARGA, CRIATURA_NOITE, CANSACO_ELEMENTO,
            CENAS_VISTAS);

    /** Modificador de vida do Dedal Dentado (mod antigo): o mod novo só tira, ao entrar. */
    public static final String MODIFICADOR_DEDAL = "dedal_coracao";

    // ---------- outros IDs ----------

    /** Modificador de velocidade da Lucidez baixa (temporário: não fica salvo no jogador). */
    public static final String MODIFICADOR_LENTIDAO = "lucidez_lentidao";
    /** Tag de dano e tag de criatura "paranormal" (pesam mais na Lucidez). */
    public static final String TAG_PARANORMAL = "paranormal";
    /** Tag de item: comida quente que devolve Lucidez (data/odteste/tags/item/comida_quente.json). */
    public static final String TAG_COMIDA_QUENTE = "comida_quente";
    /** Arquivo de dados do mundo com os grupos (data/odteste_historia.dat). Sem "odteste:". */
    public static final String ARQUIVO_HISTORIA = "odteste_historia";

    /** Item Crachá do Gabinete e o componente de dados que guarda o dono dele. */
    public static final String ITEM_CRACHA = "cracha";
    public static final String COMPONENTE_CRACHA = "cracha_dados";
    /** Livro de história entregue no kit (data/odteste/odteste_livros/dossie.json). */
    public static final String LIVRO_DOSSIE = "dossie";

    // ---------- pacote 1: espadas e artefatos (os IDs dos itens vêm de reliquias/TipoEspada e artefatos/TipoArtefato) ----------

    /** Aba do criativo do mod (espadas e artefatos). */
    public static final String ABA_CRIATIVA = "gabinete";
    /** Efeito de sangramento (só em criaturas, nunca em jogador). O ID do mod antigo volta. */
    public static final String EFEITO_SANGRAMENTO = "sangramento";
    /** Componente do item: número da noite em que o poder "1x por noite" foi usado (Moeda, Espada de Conhecimento). */
    public static final String COMPONENTE_NOITE_USADA = "noite_usada";
    /** Tipo de loot modifier que põe um artefato em baús (data/odteste/loot_modifiers/<artefato>.json). */
    public static final String LOOT_ARTEFATO_NO_BAU = "artefato_no_bau";
    /** Pacote servidor → jogador: contorno de criaturas só na tela dele. */
    public static final String PACOTE_CONTORNO = "contorno";
    /** Arquivo de config do servidor com a chance dos artefatos nos baús. */
    public static final String ARQUIVO_CONFIG_ARTEFATOS = "odteste-artefatos-server.toml";
    public static final String CONFIG_CHANCE_BAU = "chance_bau";

    /** Todos os IDs que não são dado do jogador, mas também precisam estar em docs/ids.md. */
    public static final List<String> OUTROS = List.of(
            MODIFICADOR_DEDAL, MODIFICADOR_LENTIDAO, TAG_PARANORMAL, TAG_COMIDA_QUENTE, ARQUIVO_HISTORIA,
            ITEM_CRACHA, COMPONENTE_CRACHA, LIVRO_DOSSIE,
            ABA_CRIATIVA, EFEITO_SANGRAMENTO, COMPONENTE_NOITE_USADA, LOOT_ARTEFATO_NO_BAU, PACOTE_CONTORNO,
            ARQUIVO_CONFIG_ARTEFATOS, CONFIG_CHANCE_BAU);

    private IdsDados() {}
}
