package com.nikolas.odteste.registro;

import com.nikolas.odteste.OdMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Todas as listas do mod. Cada ID registrado aqui entra em docs/ids.md: depois que um ID vai para o servidor, ele
 * não pode sumir (os dados do mundo dependem dele). Hoje só há dados presos ao jogador; itens, blocos e criaturas
 * entram aqui quando tiverem prévia aprovada.
 */
public final class Registro {
    /** Dados presos ao jogador (Exposição, Lucidez, elemento e os guardados do mod antigo). */
    public static final DeferredRegister<AttachmentType<?>> DADOS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, OdMod.MODID);

    private Registro() {}

    public static void registrar(IEventBus barramento) {
        DADOS.register(barramento);
    }
}
