package com.nikolas.odteste.sigilos;

import java.util.Locale;
import net.minecraft.util.StringRepresentable;

/**
 * Os desenhos do bloco {@code odteste:sigilo} (propriedade {@code simbolo}). O nome em minúsculas é o ID e é para
 * sempre; tem de bater com {@link IdsSigilos#SIMBOLOS} (conferido ao carregar a classe) e com os arquivos em
 * {@code assets/odteste/textures/block/sigilo/}.
 */
public enum Simbolo implements StringRepresentable {
    LETRA_A, LETRA_B, LETRA_C, LETRA_D, LETRA_E, LETRA_F, LETRA_G, LETRA_H, LETRA_I, LETRA_J, LETRA_K, LETRA_L,
    LETRA_M, LETRA_N, LETRA_O, LETRA_P, LETRA_Q, LETRA_R, LETRA_S, LETRA_T, LETRA_U, LETRA_V, LETRA_W, LETRA_X,
    LETRA_Y, LETRA_Z,
    DIGITO_0, DIGITO_1, DIGITO_2, DIGITO_3, DIGITO_4, DIGITO_5, DIGITO_6, DIGITO_7, DIGITO_8, DIGITO_9,
    ELEMENTO_SANGUE, ELEMENTO_MORTE, ELEMENTO_CONHECIMENTO, ELEMENTO_ENERGIA, ELEMENTO_MEDO,
    RITUAL_PODER;

    static {
        Simbolo[] todos = values();
        for (int i = 0; i < todos.length; i++) {
            if (!todos[i].getSerializedName().equals(IdsSigilos.SIMBOLOS.get(i)) || todos.length != IdsSigilos.SIMBOLOS.size()) {
                throw new IllegalStateException("Simbolo e IdsSigilos.SIMBOLOS divergem em " + i);
            }
        }
    }

    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }

    @Override
    public String getSerializedName() {
        return id();
    }

    /** Pelo ID; null se não existe. */
    public static Simbolo de(String id) {
        if (id == null) return null;
        for (Simbolo s : values()) {
            if (s.id().equals(id)) return s;
        }
        return null;
    }
}
