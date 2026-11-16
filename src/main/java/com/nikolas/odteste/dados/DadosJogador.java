package com.nikolas.odteste.dados;

import com.mojang.serialization.Codec;
import com.nikolas.odteste.elementos.Elemento;
import com.nikolas.odteste.registro.Registro;
import com.nikolas.odteste.sigilos.RegrasSigilos;
import com.nikolas.odteste.sistemas.Reacoes;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;

/**
 * Dados dos sistemas do jogador (Exposição, Lucidez, elemento), salvos no próprio jogador.
 * Todos usam copyOnDeath: morrer não apaga nada. Os IDs são os mesmos do mod antigo (o mundo já tem esses dados).
 *
 * Regra: ninguém mexe nos attachments direto. Use os métodos daqui, que prendem o valor na faixa (Limites).
 * Nada daqui vai para o cliente.
 */
public final class DadosJogador {

    public static final Supplier<AttachmentType<Integer>> EXPOSICAO =
            inteiro(IdsDados.EXPOSICAO, 0, Limites::exposicao);
    /** A faixa da Lucidez depende da máxima; ela é presa na leitura e na escrita. */
    public static final Supplier<AttachmentType<Integer>> LUCIDEZ =
            inteiro(IdsDados.LUCIDEZ, Limites.LUCIDEZ_MAX_PADRAO, v -> Math.max(0, v));
    public static final Supplier<AttachmentType<Integer>> LUCIDEZ_MAX =
            inteiro(IdsDados.LUCIDEZ_MAX, Limites.LUCIDEZ_MAX_PADRAO, Limites::lucidezMax);
    /** Centésimos de Exposição guardados até completar um ponto. */
    public static final Supplier<AttachmentType<Integer>> EXPOSICAO_RESTO =
            inteiro(IdsDados.EXPOSICAO_RESTO, 0, v -> Limites.prender(v, 0, 99));
    /**
     * Contato secreto com cada elemento (id do elemento → centésimos).
     * Elemento desconhecido no arquivo é ignorado ao ler; valor negativo vira 0.
     */
    public static final Supplier<AttachmentType<Map<String, Integer>>> CONTATO_ELEMENTO = Registro.DADOS.register(
            IdsDados.CONTATO_ELEMENTO,
            () -> AttachmentType.<Map<String, Integer>>builder(() -> Map.of())
                    .serialize(Codec.unboundedMap(Codec.STRING, Codec.INT).xmap(DadosJogador::limparContato, m -> m))
                    .copyOnDeath()
                    .build());
    /**
     * Freio de repetição da Exposição: fonte → [início da janela (tempo do mundo), vezes].
     * Só guarda janelas abertas (10 min), então fica pequeno.
     */
    public static final Supplier<AttachmentType<Map<String, List<Long>>>> EXPOSICAO_REPETICAO = Registro.DADOS.register(
            IdsDados.EXPOSICAO_REPETICAO,
            () -> AttachmentType.<Map<String, List<Long>>>builder(() -> Map.of())
                    .serialize(Codec.unboundedMap(Codec.STRING, Codec.LONG.listOf()))
                    .copyOnDeath()
                    .build());

    /**
     * Elemento do jogador, definido pelo admin. **Nunca vai para o cliente** (nem do dono): antes de revelado,
     * o jogador não pode ver o elemento em lugar nenhum. Texto desconhecido vira nenhum.
     */
    public static final Supplier<AttachmentType<Elemento>> ELEMENTO = Registro.DADOS.register(
            IdsDados.ELEMENTO,
            () -> AttachmentType.builder(() -> Elemento.NENHUM)
                    .serialize(Codec.STRING.xmap(Elemento::deIdOuNenhum, Elemento::id))
                    .copyOnDeath()
                    .build());
    /** A história já revelou o elemento? Nunca vai para o cliente. */
    public static final Supplier<AttachmentType<Boolean>> ELEMENTO_REVELADO = Registro.DADOS.register(
            IdsDados.ELEMENTO_REVELADO,
            () -> AttachmentType.builder(() -> false)
                    .serialize(Codec.BOOL)
                    .copyOnDeath()
                    .build());

    /** Já recebeu o kit (crachá e dossiê)? Fica salvo; morrer não devolve nem tira. */
    public static final Supplier<AttachmentType<Boolean>> RECEBEU_KIT = Registro.DADOS.register(
            IdsDados.RECEBEU_KIT,
            () -> AttachmentType.builder(() -> false)
                    .serialize(Codec.BOOL)
                    .copyOnDeath()
                    .build());

    /**
     * Sigilos que o jogador já aprendeu (nomes). Nome desconhecido ou repetido some ao ler. Morrer não apaga.
     * Quem mexe é {@code sigilos.Sigilos}. O cliente só recebe a lista ao abrir a tela do giz.
     */
    public static final Supplier<AttachmentType<List<String>>> SIGILOS_CONHECIDOS = Registro.DADOS.register(
            IdsDados.SIGILOS_CONHECIDOS,
            () -> AttachmentType.<List<String>>builder(() -> List.of())
                    .serialize(Codec.STRING.listOf().xmap(RegrasSigilos::limpar, l -> l))
                    .copyOnDeath()
                    .build());

    private DadosJogador() {}

    public static List<String> sigilos(Player p) {
        return p.getData(SIGILOS_CONHECIDOS);
    }

    public static void definirSigilos(Player p, List<String> lista) {
        p.setData(SIGILOS_CONHECIDOS, RegrasSigilos.limpar(lista));
    }

    public static boolean recebeuKit(Player p) {
        return p.getData(RECEBEU_KIT);
    }

    public static void marcarKit(Player p, boolean recebeu) {
        p.setData(RECEBEU_KIT, recebeu);
    }

    /** Força o carregamento da classe, para os registros entrarem antes do evento de registro. */
    public static void iniciar() {}

    private static Supplier<AttachmentType<Integer>> inteiro(String id, int padrao, UnaryOperator<Integer> prender) {
        // O codec prende o valor ao ler do disco: um arquivo editado à mão não quebra o jogador.
        Codec<Integer> codec = Codec.INT.xmap(prender, v -> v);
        return Registro.DADOS.register(id, () -> AttachmentType.builder(() -> padrao)
                .serialize(codec)
                .copyOnDeath()
                .build());
    }

    // ---------- elemento ----------

    /** Elemento do jogador (nenhum se o admin não definiu). Use sistemas.ElementoJogador para mudar. */
    public static Elemento elemento(Player p) {
        return p.getData(ELEMENTO);
    }

    public static boolean elementoRevelado(Player p) {
        return elemento(p) != Elemento.NENHUM && p.getData(ELEMENTO_REVELADO);
    }

    /** Grava sem avisar ninguém (o cliente nunca recebe). Quem chama é ElementoJogador. */
    public static void gravarElemento(Player p, Elemento e, boolean revelado) {
        Elemento v = e == null ? Elemento.NENHUM : e;
        p.setData(ELEMENTO, v);
        p.setData(ELEMENTO_REVELADO, v != Elemento.NENHUM && revelado);
    }

    // ---------- leitura ----------

    public static int exposicao(Player p) {
        return Limites.exposicao(p.getData(EXPOSICAO));
    }

    public static int lucidezMax(Player p) {
        return Limites.lucidezMax(p.getData(LUCIDEZ_MAX));
    }

    public static int lucidez(Player p) {
        return Limites.lucidez(p.getData(LUCIDEZ), lucidezMax(p));
    }

    public static int exposicaoResto(Player p) {
        return Limites.prender(p.getData(EXPOSICAO_RESTO), 0, 99);
    }

    /** Contato com cada elemento (sem NENHUM). */
    public static Map<Elemento, Integer> contato(Player p) {
        Map<Elemento, Integer> m = new EnumMap<>(Elemento.class);
        p.getData(CONTATO_ELEMENTO).forEach((id, v) -> Elemento.deId(id)
                .filter(e -> e != Elemento.NENHUM)
                .ifPresent(e -> m.put(e, Math.max(0, v))));
        return m;
    }

    /** Tira elementos desconhecidos e valores negativos (arquivo velho ou editado à mão). */
    static Map<String, Integer> limparContato(Map<String, Integer> lido) {
        Map<String, Integer> m = new HashMap<>();
        lido.forEach((id, v) -> Elemento.deId(id)
                .filter(e -> e != Elemento.NENHUM)
                .ifPresent(e -> m.put(e.id(), Math.max(0, v == null ? 0 : v))));
        return Map.copyOf(m);
    }

    // ---------- escrita (sempre presa na faixa) ----------

    public static void definirExposicao(Player p, int valor) {
        p.setData(EXPOSICAO, Limites.exposicao(valor));
    }

    /** Usado pelo ganho automático: grava a Exposição e o resto juntos. */
    public static void definirExposicaoComResto(Player p, int valor, int resto) {
        p.setData(EXPOSICAO_RESTO, Limites.prender(resto, 0, 99));
        if (Limites.exposicao(valor) != exposicao(p)) definirExposicao(p, valor);
    }

    /** Soma contato com um elemento (nunca desce, para em 1 milhão para não estourar). */
    public static void somarContato(Player p, Elemento e, int centesimos) {
        if (e == null || e == Elemento.NENHUM || centesimos <= 0) return;
        Map<String, Integer> m = new HashMap<>(p.getData(CONTATO_ELEMENTO));
        m.merge(e.id(), centesimos, (a, b) -> (int) Math.min(1_000_000L, (long) a + b));
        p.setData(CONTATO_ELEMENTO, Map.copyOf(m));
    }

    public static void definirLucidez(Player p, int valor) {
        int antes = lucidez(p);
        p.setData(LUCIDEZ, Limites.lucidez(valor, lucidezMax(p)));
        if (p instanceof ServerPlayer sp) Reacoes.lucidezMudou(sp, antes, lucidez(p));
    }

    /** Baixar a máxima também baixa a Lucidez atual, se ela passar do novo teto. */
    public static void definirLucidezMax(Player p, int valor) {
        int max = Limites.lucidezMax(valor);
        p.setData(LUCIDEZ_MAX, max);
        p.setData(LUCIDEZ, Limites.lucidez(p.getData(LUCIDEZ), max));
        if (p instanceof ServerPlayer sp) com.nikolas.odteste.sistemas.Lucidez.atualizarLentidao(sp);
    }
}
