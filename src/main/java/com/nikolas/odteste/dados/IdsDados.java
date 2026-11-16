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

    /** Sigilos que o jogador já aprendeu (lista de nomes; ver sigilos/IdsSigilos). */
    public static final String SIGILOS_CONHECIDOS = "sigilos_conhecidos";

    /** Dados em uso. */
    public static final List<String> TODOS = List.of(
            EXPOSICAO, LUCIDEZ, LUCIDEZ_MAX, EXPOSICAO_RESTO, CONTATO_ELEMENTO, EXPOSICAO_REPETICAO,
            ELEMENTO, ELEMENTO_REVELADO, RECEBEU_KIT, SIGILOS_CONHECIDOS);

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

    /** Todos os IDs que não são dado do jogador, mas também precisam estar em docs/ids.md. */
    public static final List<String> OUTROS = List.of(
            MODIFICADOR_DEDAL, MODIFICADOR_LENTIDAO, TAG_PARANORMAL, TAG_COMIDA_QUENTE, ARQUIVO_HISTORIA,
            ITEM_CRACHA, COMPONENTE_CRACHA, LIVRO_DOSSIE);

    private IdsDados() {}
}
