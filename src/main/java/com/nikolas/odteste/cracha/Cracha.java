package com.nikolas.odteste.cracha;

import com.nikolas.odteste.dados.DadosJogador;
import com.nikolas.odteste.elementos.Elemento;
import com.nikolas.odteste.historia.mestre.ConfigMestre;
import com.nikolas.odteste.registro.Registro;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/** Montagem e atualização dos crachás (só servidor). */
public final class Cracha {
    private Cracha() {}

    /** O que o crachá deste jogador deve guardar agora. */
    public static CrachaDados dadosDe(ServerPlayer p) {
        String nome = p.getGameProfile().getName();
        Elemento e = DadosJogador.elemento(p);
        String elemento = RegrasCracha.elementoParaGuardar(e.id(), DadosJogador.elementoRevelado(p));
        return new CrachaDados(p.getUUID(), nome, RegrasCracha.ehMestre(nome, ConfigMestre.mestres()), elemento);
    }

    public static ItemStack criar(ServerPlayer p) {
        ItemStack item = new ItemStack(Registro.CRACHA.get());
        item.set(Registro.CRACHA_DADOS, dadosDe(p));
        return item;
    }

    /**
     * Atualiza os crachás deste jogador que estão no inventário dele (no máximo 41 espaços). Os que estão com
     * outra pessoa guardam o dado no próprio item e só atualizam quando o dono recebe, loga ou muda de elemento.
     */
    public static void atualizarDoDono(ServerPlayer p) {
        CrachaDados novo = dadosDe(p);
        Inventory inv = p.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.isEmpty() || !s.is(Registro.CRACHA.get())) continue;
            CrachaDados atual = s.get(Registro.CRACHA_DADOS);
            if (atual != null && atual.dono().equals(p.getUUID()) && !atual.equals(novo)) {
                s.set(Registro.CRACHA_DADOS, novo);
            }
        }
    }
}
