package com.nikolas.odteste.cracha;

/** Regras da entrega do kit (Java puro, testado em RegrasKitTest). */
public final class RegrasKit {
    private RegrasKit() {}

    /** O kit sai na primeira entrada: só quem ainda não recebeu. */
    public static boolean deveDar(boolean jaRecebeu) {
        return !jaRecebeu;
    }
}
