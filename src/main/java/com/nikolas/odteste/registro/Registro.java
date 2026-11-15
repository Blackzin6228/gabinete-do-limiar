package com.nikolas.odteste.registro;

import com.nikolas.odteste.OdMod;
import com.nikolas.odteste.cracha.CrachaDados;
import com.nikolas.odteste.cracha.CrachaItem;
import com.nikolas.odteste.dados.IdsDados;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Todas as listas do mod. Cada ID registrado aqui entra em docs/ids.md: depois que um ID vai para o servidor, ele
 * não pode sumir (os dados do mundo dependem dele). Blocos e criaturas entram aqui quando tiverem prévia aprovada.
 */
public final class Registro {
    /** Dados presos ao jogador (Exposição, Lucidez, elemento e os guardados do mod antigo). */
    public static final DeferredRegister<AttachmentType<?>> DADOS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, OdMod.MODID);

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(OdMod.MODID);

    public static final DeferredRegister<DataComponentType<?>> COMPONENTES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, OdMod.MODID);

    /** Dono do crachá (nome, mestre, elemento revelado). Sincroniza com o cliente. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CrachaDados>> CRACHA_DADOS =
            COMPONENTES.register(IdsDados.COMPONENTE_CRACHA, () -> DataComponentType.<CrachaDados>builder()
                    .persistent(CrachaDados.CODEC)
                    .networkSynchronized(CrachaDados.STREAM_CODEC)
                    .build());

    public static final DeferredHolder<Item, CrachaItem> CRACHA =
            ITEMS.register(IdsDados.ITEM_CRACHA, () -> new CrachaItem(new Item.Properties().stacksTo(1)));

    private Registro() {}

    public static void registrar(IEventBus barramento) {
        DADOS.register(barramento);
        COMPONENTES.register(barramento);
        ITEMS.register(barramento);
    }
}
