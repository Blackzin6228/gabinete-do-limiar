package com.nikolas.odteste.registro;

import com.nikolas.odteste.OdMod;
import com.nikolas.odteste.cracha.CrachaDados;
import com.nikolas.odteste.cracha.CrachaItem;
import com.nikolas.odteste.dados.IdsDados;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import com.nikolas.odteste.sigilos.GizItem;
import com.nikolas.odteste.sigilos.IdsSigilos;
import com.nikolas.odteste.sigilos.SigiloBloco;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
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

    public static final DeferredRegister.Blocks BLOCOS = DeferredRegister.createBlocks(OdMod.MODID);

    /** Giz único (sem cor por elemento). Sem aba criativa: vem da história, do kit ou de {@code /give}. */
    public static final DeferredHolder<Item, GizItem> GIZ =
            ITEMS.register(IdsSigilos.ITEM_GIZ, () -> new GizItem(new Item.Properties().durability(64)));

    /** Sigilo riscado no chão. Não tem item e não solta nada. */
    public static final DeferredHolder<Block, SigiloBloco> SIGILO = BLOCOS.register(IdsSigilos.BLOCO_SIGILO,
            () -> new SigiloBloco(BlockBehaviour.Properties.of()
                    .noCollission()
                    .noOcclusion()
                    .instabreak()
                    .noLootTable()
                    .lightLevel(estado -> 3)
                    .pushReaction(PushReaction.DESTROY)
                    .sound(SoundType.GRAVEL)));

    private Registro() {}

    public static void registrar(IEventBus barramento) {
        DADOS.register(barramento);
        COMPONENTES.register(barramento);
        BLOCOS.register(barramento);
        ITEMS.register(barramento);
    }
}
