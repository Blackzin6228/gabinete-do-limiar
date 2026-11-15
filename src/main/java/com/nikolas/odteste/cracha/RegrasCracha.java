package com.nikolas.odteste.cracha;

import java.util.List;
import java.util.Locale;

/** Regras do crachá (Java puro, testado em RegrasCrachaTest). */
public final class RegrasCracha {
    /** Tamanho da textura do cartão, em pixels. */
    public static final int LARGURA = 160;
    public static final int ALTURA = 100;

    /** Posições do texto dentro do cartão (na escala 1). */
    public static final int NOME_X = 56;
    public static final int NOME_Y = 45;
    public static final int ELEMENTO_X = 56;
    public static final int ELEMENTO_Y = 73;

    public static final int COR_NOME = 0x282624;
    public static final int COR_ELEMENTO = 0x701214;

    /** Qual textura do cartão usar. */
    public enum Cartao {
        MESTRE("cracha_cartao_mestre"),
        LIMPO("cracha_cartao_limpo"),
        LOTACAO("cracha_cartao");

        private final String textura;

        Cartao(String textura) {
            this.textura = textura;
        }

        /** Nome do arquivo em textures/gui (sem .png). */
        public String textura() {
            return textura;
        }
    }

    private RegrasCracha() {}

    /** Mestre vence; senão, elemento revelado mostra o cartão limpo; senão, a lotação borrada. */
    public static Cartao escolher(boolean mestre, boolean elementoRevelado) {
        if (mestre) return Cartao.MESTRE;
        return elementoRevelado ? Cartao.LIMPO : Cartao.LOTACAO;
    }

    /** Nome está na lista de mestres? Sem diferença de maiúscula; lista vazia = ninguém. */
    public static boolean ehMestre(String nome, List<String> mestres) {
        if (nome == null || mestres == null) return false;
        String n = nome.toLowerCase(Locale.ROOT);
        for (String m : mestres) {
            if (m != null && m.toLowerCase(Locale.ROOT).equals(n)) return true;
        }
        return false;
    }

    /** Maior inteiro que faz o cartão caber na tela (mínimo 1). */
    public static int escala(int larguraTela, int alturaTela) {
        return Math.max(1, Math.min(larguraTela / LARGURA, alturaTela / ALTURA));
    }

    /** Só guarda o elemento se foi revelado e existe (senão vazaria para o cliente). Vazio = não revelado. */
    public static String elementoParaGuardar(String idElemento, boolean revelado) {
        if (!revelado || idElemento == null || idElemento.isEmpty() || idElemento.equals("nenhum")) return "";
        return idElemento;
    }
}
