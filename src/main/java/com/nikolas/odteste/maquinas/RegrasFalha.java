package com.nikolas.odteste.maquinas;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Contas das falhas de máquina, sem nada do Minecraft (por isso tem teste). O tempo é em ticks do servidor.
 */
public final class RegrasFalha {
    /** Teto de itens lembrados por relógio: máquina que ninguém mais visita sai da lista sozinha. */
    public static final int MAX_LEMBRADOS = 4096;

    private RegrasFalha() {}

    /** Chance (0 a 1) de uma checagem falhar: base em porcentagem, vezes o fator de proteção (1 = sem proteção). */
    public static double chance(double basePercentual, double fator) {
        double c = basePercentual / 100.0 * Math.max(0.0, Math.min(1.0, fator));
        return Math.max(0.0, Math.min(1.0, c));
    }

    /** Sorteio: {@code sorteio} vem de 0 (inclusive) a 1 (exclusive). */
    public static boolean falhou(double chance, double sorteio) {
        return sorteio < chance;
    }

    /**
     * Relógio de espera por chave (uma máquina ou um jogador). Guarda o tick da última marca.
     * Pequeno e com teto: quando enche, esquece a marca mais antiga.
     */
    public static final class Relogio<K> {
        private final Map<K, Long> marcas = new LinkedHashMap<>();
        private final int max;

        public Relogio() {
            this(MAX_LEMBRADOS);
        }

        public Relogio(int max) {
            this.max = Math.max(1, max);
        }

        /** Já passou o tempo de espera desde a última marca? Sem marca = pode. Não marca. */
        public boolean pode(K chave, long agora, long espera) {
            Long m = marcas.get(chave);
            return m == null || agora - m >= espera || agora < m;
        }

        public void marcar(K chave, long agora) {
            marcas.remove(chave);
            marcas.put(chave, agora);
            while (marcas.size() > max) {
                var it = marcas.keySet().iterator();
                it.next();
                it.remove();
            }
        }

        /** {@link #pode} e, se puder, já marca. */
        public boolean tentar(K chave, long agora, long espera) {
            if (!pode(chave, agora, espera)) return false;
            marcar(chave, agora);
            return true;
        }

        public int tamanho() {
            return marcas.size();
        }

        public void limpar() {
            marcas.clear();
        }
    }
}
