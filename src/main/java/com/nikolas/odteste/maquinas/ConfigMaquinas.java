package com.nikolas.odteste.maquinas;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Config do servidor para as falhas de máquina ({@code serverconfig/odteste-maquinas-server.toml} do mundo).
 * O NeoForge relê o arquivo com o servidor ligado. Antes de carregar, os getters devolvem o padrão.
 */
public final class ConfigMaquinas {
    public static final String ARQUIVO = "odteste-maquinas-server.toml";
    public static final String SECAO = "maquinas";

    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.BooleanValue LIGADO;
    private static final ModConfigSpec.IntValue CHANCE;
    private static final ModConfigSpec.IntValue INTERVALO_MAQUINA;
    private static final ModConfigSpec.IntValue ESPERA_JOGADOR;
    private static final ModConfigSpec.IntValue RAIO;
    private static final ModConfigSpec.IntValue CHANCE_SUSTO;
    private static final ModConfigSpec.IntValue FATOR_PROTECAO;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        b.push(SECAO);
        LIGADO = b.comment("Liga as falhas paranormais das máquinas do Mekanism e do Re:Avaritia.")
                .define("ligado", true);
        CHANCE = b.comment("Chance (em %) de falhar a cada checagem de uma máquina que está no meio de um ciclo.")
                .defineInRange("chance_percentual", 4, 0, 100);
        INTERVALO_MAQUINA = b.comment("Segundos entre duas checagens da mesma máquina (no mínimo).")
                .defineInRange("intervalo_maquina_s", 60, 10, 3600);
        ESPERA_JOGADOR = b.comment("Segundos que um jogador fica sem ver outra falha depois de ver uma.")
                .defineInRange("espera_jogador_s", 600, 30, 86400);
        RAIO = b.comment("Blocos de distância até o jogador: só máquina assim perto pode falhar (ninguém veria longe).")
                .defineInRange("raio", 12, 4, 24);
        CHANCE_SUSTO = b.comment("Chance (em %) de, na falha, um jogador perto levar também um susto leve (luz ou sussurro).")
                .defineInRange("chance_susto_percentual", 35, 0, 100);
        FATOR_PROTECAO = b.comment("Quanto da chance sobra (em %) para quem leva um item da tag odteste:reduz_falha_maquina",
                        "(ex.: o Fusível Queimado). 50 = metade da chance.")
                .defineInRange("fator_protecao_percentual", 50, 0, 100);
        b.pop();
        SPEC = b.build();
    }

    private ConfigMaquinas() {}

    public static void registrar(ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, SPEC, ARQUIVO);
    }

    public static boolean ligado() {
        try {
            return LIGADO.get();
        } catch (IllegalStateException carregando) {
            return true;
        }
    }

    public static int chancePercentual() {
        try {
            return CHANCE.get();
        } catch (IllegalStateException carregando) {
            return 4;
        }
    }

    /** Em ticks. */
    public static long intervaloMaquina() {
        try {
            return INTERVALO_MAQUINA.get() * 20L;
        } catch (IllegalStateException carregando) {
            return 60 * 20L;
        }
    }

    /** Em ticks. */
    public static long esperaJogador() {
        try {
            return ESPERA_JOGADOR.get() * 20L;
        } catch (IllegalStateException carregando) {
            return 600 * 20L;
        }
    }

    public static int raio() {
        try {
            return RAIO.get();
        } catch (IllegalStateException carregando) {
            return 12;
        }
    }

    public static int chanceSusto() {
        try {
            return CHANCE_SUSTO.get();
        } catch (IllegalStateException carregando) {
            return 35;
        }
    }

    /** De 0 a 1. */
    public static double fatorProtecao() {
        try {
            return FATOR_PROTECAO.get() / 100.0;
        } catch (IllegalStateException carregando) {
            return 0.5;
        }
    }
}
