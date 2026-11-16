package com.nikolas.odteste.sigilos;

import java.util.Locale;
import net.minecraft.util.StringRepresentable;

/**
 * Os desenhos do bloco {@code odteste:sigilo} (propriedade {@code simbolo}). O nome em minúsculas é o ID e é para
 * sempre; tem de bater com {@link IdsSigilos#SIMBOLOS} (conferido ao carregar a classe) e com os arquivos em
 * {@code assets/odteste/textures/block/sigilo/}.
 */
public enum Simbolo implements StringRepresentable {
    SIGILO_A, SIGILO_B, SIGILO_C, SIGILO_D, SIGILO_E, SIGILO_F,
    SIGILO_G, SIGILO_H, SIGILO_I, SIGILO_J, SIGILO_K, SIGILO_L,
    SIGILO_M, SIGILO_N, SIGILO_O, SIGILO_P, SIGILO_Q, SIGILO_R,
    SIGILO_S, SIGILO_T, SIGILO_U, SIGILO_V, SIGILO_W, SIGILO_X,
    SIGILO_Y, SIGILO_Z, SIGILO_0, SIGILO_1, SIGILO_2, SIGILO_3,
    SIGILO_4, SIGILO_5, SIGILO_6, SIGILO_7, SIGILO_8, SIGILO_9,
    SIGILO_DE_SANGUE, SIGILO_DE_MORTE, SIGILO_DE_CONHECIMENTO, SIGILO_DE_ENERGIA, SIGILO_DE_MEDO, SIGILO_DE_INVOCACAO,
    SIGILO_DE_PROTECAO, SIGILO_DE_BANIMENTO, SIGILO_DE_PASSAGEM, SIGILO_DE_REVELACAO;

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
