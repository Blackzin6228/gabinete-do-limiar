package com.nikolas.odteste.cliente;

import com.nikolas.odteste.rede.Contorno;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

/**
 * Contorno do paranormal só nesta tela (Óculos de Velório, Sentença Sem Recurso). Liga a flag de brilho da
 * entidade aqui no cliente; o servidor não sabe e ninguém mais vê. A flag é reaplicada a cada 10 ticks (o servidor
 * reescreve as flags quando a criatura muda de estado) e apagada quando o tempo acaba. Teto de 32 criaturas.
 */
final class ContornoCliente {
    private static final int TETO = 32;
    private static final int FLAG_BRILHO = 6;
    /** id da entidade → ticks que faltam. */
    private static final Int2IntOpenHashMap ATIVOS = new Int2IntOpenHashMap();
    private static int relogio;

    private ContornoCliente() {}

    static void iniciar() {
        Contorno.definirReceptor(ContornoCliente::receber);
    }

    private static void receber(Contorno m) {
        for (int id : m.ids()) {
            if (ATIVOS.size() >= TETO && !ATIVOS.containsKey(id)) break;
            ATIVOS.put(id, Math.max(ATIVOS.get(id), m.ticks()));
        }
        aplicar(10);
    }

    static void aoTick() {
        if (ATIVOS.isEmpty() || ++relogio % 10 != 0) return;
        aplicar(10);
    }

    private static void aplicar(int passou) {
        ClientLevel mundo = Minecraft.getInstance().level;
        if (mundo == null) {
            ATIVOS.clear();
            return;
        }
        IntIterator it = ATIVOS.keySet().iterator();
        while (it.hasNext()) {
            int id = it.nextInt();
            int resta = ATIVOS.get(id) - passou;
            Entity e = mundo.getEntity(id);
            if (e == null || e.isRemoved() || resta <= 0) {
                if (e != null) e.setSharedFlag(FLAG_BRILHO, e.hasGlowingTag());
                it.remove();
                continue;
            }
            ATIVOS.put(id, resta);
            e.setSharedFlag(FLAG_BRILHO, true);
        }
    }

    static void limpar() {
        ATIVOS.clear();
    }
}
