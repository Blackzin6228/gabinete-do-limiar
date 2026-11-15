package com.nikolas.odteste.cliente;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.TranslatableEnum;

/**
 * Opções do cliente (arquivo config/odteste-client.toml e tela de configuração do mod).
 * "Efeitos fortes de tela" é acessibilidade (tontura): dá para diminuir, não para desligar (decisão de 14/11/2026).
 * O valor {@code DESLIGADO} fica com o mesmo nome no arquivo (quem já escolheu não perde a escolha), mas hoje
 * quer dizer "Mínimo": tremor, piscada e distorção a um quarto, alucinações como no SUAVE.
 */
public final class ConfigCliente {
    public enum Forca implements TranslatableEnum {
        LIGADO(1f),
        SUAVE(0.5f),
        /** Nome antigo (fica no arquivo de quem já escolheu). No jogo aparece como "Mínimo". */
        DESLIGADO(0.25f);

        public final float tremor;

        Forca(float tremor) {
            this.tremor = tremor;
        }

        @Override
        public Component getTranslatedName() {
            return Component.translatable("odteste.configuration.efeitos_fortes." + name().toLowerCase(java.util.Locale.ROOT));
        }
    }

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.EnumValue<Forca> EFEITOS_FORTES;
    public static final ModConfigSpec.IntValue INTENSIDADE;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
        EFEITOS_FORTES = b.comment("Efeitos fortes de tela (tremor, piscada, distorção). LIGADO, SUAVE ou DESLIGADO (= mínimo: diminui, não tira).")
                .translation("odteste.configuration.efeitos_fortes")
                .defineEnum("efeitos_fortes", Forca.LIGADO);
        INTENSIDADE = b.comment("Intensidade da camada de horror (céu cinza, névoa, escuro), em %. De 50 a 100: diminui, não desliga. A música cortada não muda.")
                .translation("odteste.configuration.intensidade")
                .defineInRange("intensidade", 100, 50, 100);
        SPEC = b.build();
    }

    private ConfigCliente() {}

    /** Multiplicador do tremor (0,25 a 1). Seguro de chamar antes da config carregar. */
    public static float tremor() {
        try {
            return EFEITOS_FORTES.get().tremor;
        } catch (IllegalStateException carregando) {
            return Forca.LIGADO.tremor;
        }
    }

    /** "Suave" ou "mínimo": porta só com som, vulto e criatura somem em 1 s, tela e corpo menores (docs/horror.md 1.4/2.4). */
    public static boolean suave() {
        float t = tremor();
        return t > 0 && t < 1;
    }

    /**
     * Antes: "desligado" deixava só vinheta e som. Desde 14/11/2026 nada desliga (o mínimo é 0,25), então isto só
     * fica verdadeiro se algum dia voltar uma força 0.
     */
    public static boolean soVinhetaESom() {
        return tremor() <= 0;
    }

    /** Intensidade da camada de horror em % (50 a 100). Seguro de chamar antes da config carregar. */
    public static int intensidade() {
        try {
            return INTENSIDADE.get();
        } catch (IllegalStateException carregando) {
            return 100;
        }
    }
}
