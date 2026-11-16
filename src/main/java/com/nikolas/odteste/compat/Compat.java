package com.nikolas.odteste.compat;

import com.nikolas.odteste.compat.mekanism.MekanismFalhas;
import com.nikolas.odteste.compat.reavaritia.ReAvaritiaFalhas;
import com.nikolas.odteste.maquinas.FalhaMaquinas;
import java.util.ArrayList;
import java.util.List;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;

/**
 * Liga as integrações com os mods do pack. Regra de ouro: nenhuma classe de outro mod é tocada
 * fora de um pacote compat/<mod>, e esse pacote só é carregado quando o mod existe.
 * Assim, se um mod sair do pack, o nosso continua abrindo sem crash.
 */
public final class Compat {
    public enum Integracao {
        /** Shader pack. Só cliente: só perguntamos (por reflexão, em compat/iris/) se há um pack ligado. */
        IRIS("iris"),
        /** Máquinas que falham (compat/mekanism/). Só por reflexão: não há dependência de compilação. */
        MEKANISM("mekanism"),
        /** Máquinas que falham (compat/reavaritia/). Mod id do Re:Avaritia: avaritia. Só por reflexão. */
        REAVARITIA("avaritia"),
        /** Slot de acessório dos artefatos (compat/curios/). Sem ele, o artefato vale em qualquer lugar do inventário. */
        CURIOS("curios");

        public final String modid;

        Integracao(String modid) {
            this.modid = modid;
        }

        public boolean ligada() {
            return ModList.get().isLoaded(modid);
        }
    }

    private Compat() {}

    /** Cada integração de servidor entra aqui quando tiver conteúdo, sempre atrás do if (a classe compat só é lida se o mod existir). */
    public static void iniciar(IEventBus barramento) {
        // O Iris é só do cliente (cliente/ chama compat/iris/ atrás de IRIS.ligada()).
        FalhaMaquinas.iniciar();
        if (Integracao.MEKANISM.ligada()) MekanismFalhas.iniciar();
        if (Integracao.REAVARITIA.ligada()) ReAvaritiaFalhas.iniciar();
    }

    public static List<String> ligadas() {
        List<String> lista = new ArrayList<>();
        for (Integracao i : Integracao.values()) {
            if (i.ligada()) lista.add(i.modid);
        }
        return lista;
    }
}
