package com.nikolas.odteste.artefatos;

import com.nikolas.odteste.dados.IdsDados;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Config dos artefatos ({@code serverconfig/odteste-artefatos-server.toml} do mundo). Relida com o servidor ligado. */
public final class ConfigArtefatos {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.DoubleValue CHANCE_BAU;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        b.push("artefatos");
        CHANCE_BAU = b.comment("Chance de um artefato aparecer num baú que combina com ele (0 = nunca, 1 = sempre).",
                        "Cada artefato tem a sua lista de baús em data/odteste/loot_modifiers/<artefato>.json.",
                        "Padrão 0.02 (2%): raro.")
                .defineInRange(IdsDados.CONFIG_CHANCE_BAU, RegrasArtefatos.CHANCE_BAU_PADRAO, 0.0, 1.0);
        b.pop();
        SPEC = b.build();
    }

    private ConfigArtefatos() {}

    public static void registrar(ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, SPEC, IdsDados.ARQUIVO_CONFIG_ARTEFATOS);
    }

    /** Seguro antes da config carregar: devolve o padrão. */
    public static double chanceBau() {
        try {
            return RegrasArtefatos.chance(CHANCE_BAU.get());
        } catch (IllegalStateException carregando) {
            return RegrasArtefatos.CHANCE_BAU_PADRAO;
        }
    }
}
