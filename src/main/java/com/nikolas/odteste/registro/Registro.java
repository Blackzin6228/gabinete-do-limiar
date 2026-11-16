package com.nikolas.odteste.registro;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.nikolas.odteste.OdMod;
import com.nikolas.odteste.artefatos.ArtefatoItem;
import com.nikolas.odteste.artefatos.ArtefatoNoBau;
import com.nikolas.odteste.artefatos.Sangramento;
import com.nikolas.odteste.artefatos.TipoArtefato;
import com.nikolas.odteste.reliquias.EspadaReliquia;
import com.nikolas.odteste.reliquias.TipoEspada;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
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

    // ---------- pacote 1: espadas e artefatos ----------

    /** Número da noite em que o poder "1x por noite" foi usado (Moeda de Defunto). Persistente. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> NOITE_USADA =
            COMPONENTES.register(IdsDados.COMPONENTE_NOITE_USADA, () -> DataComponentType.<Long>builder()
                    .persistent(Codec.LONG)
                    .networkSynchronized(ByteBufCodecs.VAR_LONG)
                    .build());

    public static final Map<TipoEspada, DeferredHolder<Item, EspadaReliquia>> ESPADAS = new EnumMap<>(TipoEspada.class);
    public static final Map<TipoArtefato, DeferredHolder<Item, ArtefatoItem>> ARTEFATOS = new EnumMap<>(TipoArtefato.class);

    static {
        for (TipoEspada t : TipoEspada.values()) ESPADAS.put(t, ITEMS.register(t.id, () -> new EspadaReliquia(t)));
        for (TipoArtefato t : TipoArtefato.values()) ARTEFATOS.put(t, ITEMS.register(t.id, () -> new ArtefatoItem(t)));
    }

    public static final DeferredRegister<MobEffect> EFEITOS = DeferredRegister.create(Registries.MOB_EFFECT, OdMod.MODID);
    public static final DeferredHolder<MobEffect, Sangramento> SANGRAMENTO =
            EFEITOS.register(IdsDados.EFEITO_SANGRAMENTO, Sangramento::new);

    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, OdMod.MODID);
    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<ArtefatoNoBau>> ARTEFATO_NO_BAU =
            LOOT.register(IdsDados.LOOT_ARTEFATO_NO_BAU, () -> ArtefatoNoBau.CODEC);

    /** Aba do criativo: as 4 espadas e os 12 artefatos (com o /give, o único jeito de pegar as espadas). */
    public static final DeferredRegister<CreativeModeTab> ABAS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, OdMod.MODID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ABA = ABAS.register(IdsDados.ABA_CRIATIVA,
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.odteste." + IdsDados.ABA_CRIATIVA))
                    .icon(() -> new ItemStack(ESPADAS.get(TipoEspada.SENTENCA_SEM_RECURSO).get()))
                    .displayItems((params, saida) -> {
                        ESPADAS.values().forEach(h -> saida.accept(h.get()));
                        ARTEFATOS.values().forEach(h -> saida.accept(h.get()));
                    })
                    .build());

    private Registro() {}

    public static void registrar(IEventBus barramento) {
        DADOS.register(barramento);
        COMPONENTES.register(barramento);
        ITEMS.register(barramento);
        EFEITOS.register(barramento);
        LOOT.register(barramento);
        ABAS.register(barramento);
    }
}
