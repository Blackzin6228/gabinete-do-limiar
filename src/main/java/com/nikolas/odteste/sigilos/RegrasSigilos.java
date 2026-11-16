package com.nikolas.odteste.sigilos;

import java.util.ArrayList;
import java.util.List;

/** Regras dos sigilos aprendidos e do risco no chão, sem nada do Minecraft (tem teste). */
public final class RegrasSigilos {
    /** Até onde o jogador pode estar do bloco que riscou (o servidor confere; o cliente não manda nada confiável). */
    public static final double DISTANCIA_MAX = 6.0;
    /** Um pedido de risco por segundo. */
    public static final int ESPERA_TICKS = 20;
    public static final int GIROS = 4;

    private RegrasSigilos() {}

    public static boolean valido(String id) {
        return id != null && IdsSigilos.SIMBOLOS.contains(id);
    }

    public static boolean giroValido(int giro) {
        return giro >= 0 && giro < GIROS;
    }

    public static boolean perto(double distanciaAoQuadrado) {
        return distanciaAoQuadrado <= DISTANCIA_MAX * DISTANCIA_MAX;
    }

    /** Já passou a espera desde o último pedido? ({@code ultimo} negativo = nunca pediu.) */
    public static boolean livre(long ultimo, long agora) {
        return ultimo < 0 || agora < ultimo || agora - ultimo >= ESPERA_TICKS;
    }

    /** Tira nome desconhecido (de arquivo velho ou editado à mão) e repetido, mantendo a ordem. */
    public static List<String> limpar(List<String> lido) {
        List<String> r = new ArrayList<>();
        for (String s : lido) {
            if (valido(s) && !r.contains(s)) r.add(s);
        }
        return List.copyOf(r);
    }

    /** Lista com o sigilo a mais (a mesma lista se já tinha ou se o nome não existe). */
    public static List<String> com(List<String> atual, String id) {
        if (!valido(id) || atual.contains(id)) return atual;
        List<String> r = new ArrayList<>(atual);
        r.add(id);
        return List.copyOf(r);
    }

    /** Lista sem o sigilo (a mesma lista se não tinha). */
    public static List<String> sem(List<String> atual, String id) {
        if (!atual.contains(id)) return atual;
        List<String> r = new ArrayList<>(atual);
        r.remove(id);
        return List.copyOf(r);
    }

    /** Só os conhecidos que ainda existem, na ordem oficial dos desenhos (a tela mostra sempre igual). */
    public static List<String> naOrdemOficial(List<String> conhecidos) {
        List<String> r = new ArrayList<>();
        for (String s : IdsSigilos.SIMBOLOS) {
            if (conhecidos.contains(s)) r.add(s);
        }
        return r;
    }
}
