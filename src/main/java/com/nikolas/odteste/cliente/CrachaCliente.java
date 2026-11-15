package com.nikolas.odteste.cliente;

import com.nikolas.odteste.cracha.CrachaDados;
import net.minecraft.client.Minecraft;

/** Ponte do crachá para o cliente. Só é chamada no lado do cliente (cliente/ nunca é carregado no servidor). */
public final class CrachaCliente {
    private CrachaCliente() {}

    public static void abrir(CrachaDados dados) {
        Minecraft.getInstance().setScreen(new TelaCracha(dados));
    }
}
