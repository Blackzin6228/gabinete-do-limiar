package com.nikolas.odteste.sigilos;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Config do servidor para o efeito dos sigilos de elemento ({@code serverconfig/odteste-sigilos-server.toml} do mundo).
 * Relida com o servidor ligado. Antes de carregar, os getters devolvem o padrão.
 */
public final class ConfigSigilos {
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.IntValue RAIO;
    private static final ModConfigSpec.IntValue DURACAO;
    private static final ModConfigSpec.IntValue CUSTO;
    private static final ModConfigSpec.IntValue ESPERA;
    private static final ModConfigSpec.IntValue MAX_ALVOS;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        b.push("sigilos");
        RAIO = b.comment("Raio (em blocos) da área do efeito de um sigilo de elemento.")
                .defineInRange("raio", RegrasEfeitoSigilo.RAIO_PADRAO, 1, RegrasEfeitoSigilo.RAIO_MAX);
        DURACAO = b.comment("Quantos segundos duram os efeitos sobre quem está na área.")
                .defineInRange("duracao_s", RegrasEfeitoSigilo.DURACAO_PADRAO_S, 1, 120);
        CUSTO = b.comment("Pontos de Sanidade (Lucidez) que cada ativação cobra de quem ativou. 0 = não cobra.")
                .defineInRange("custo_sanidade", RegrasEfeitoSigilo.CUSTO_PADRAO, 0, 100);
        ESPERA = b.comment("Segundos que quem ativou um sigilo espera para ativar outro.")
                .defineInRange("espera_s", RegrasEfeitoSigilo.ESPERA_PADRAO_S, 0, 3600);
        MAX_ALVOS = b.comment("Teto de criaturas afetadas por ativação (evita lag em lugar lotado).")
                .defineInRange("max_alvos", RegrasEfeitoSigilo.MAX_ALVOS_PADRAO, 1, 128);
        b.pop();
        SPEC = b.build();
    }

    private ConfigSigilos() {}

    public static void registrar(ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, SPEC, IdsSigilos.ARQUIVO_CONFIG);
    }

    public static int raio() {
        try {
            return RegrasEfeitoSigilo.raio(RAIO.get());
        } catch (IllegalStateException carregando) {
            return RegrasEfeitoSigilo.RAIO_PADRAO;
        }
    }

    /** Em ticks. */
    public static int duracaoTicks() {
        try {
            return DURACAO.get() * 20;
        } catch (IllegalStateException carregando) {
            return RegrasEfeitoSigilo.DURACAO_PADRAO_S * 20;
        }
    }

    public static int custoSanidade() {
        try {
            return CUSTO.get();
        } catch (IllegalStateException carregando) {
            return RegrasEfeitoSigilo.CUSTO_PADRAO;
        }
    }

    /** Em ticks. */
    public static long esperaTicks() {
        try {
            return ESPERA.get() * 20L;
        } catch (IllegalStateException carregando) {
            return RegrasEfeitoSigilo.ESPERA_PADRAO_S * 20L;
        }
    }

    public static int maxAlvos() {
        try {
            return MAX_ALVOS.get();
        } catch (IllegalStateException carregando) {
            return RegrasEfeitoSigilo.MAX_ALVOS_PADRAO;
        }
    }
}
