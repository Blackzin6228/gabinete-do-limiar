package com.nikolas.odteste.artefatos;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

/**
 * Põe um artefato, raro, nos baús que combinam com ele ({@code data/odteste/loot_modifiers/<artefato>.json}).
 * A chance vem da config do servidor ({@link ConfigArtefatos}). Só baús vanilla (que as estruturas dos mods do pack
 * também costumam usar). Barato: compara o id da tabela com uma lista curta.
 */
public final class ArtefatoNoBau extends LootModifier {
    public static final MapCodec<ArtefatoNoBau> CODEC = RecordCodecBuilder.mapCodec(i -> codecStart(i).and(i.group(
                    BuiltInRegistries.ITEM.byNameCodec().fieldOf("artefato").forGetter(m -> m.artefato),
                    ResourceLocation.CODEC.listOf().fieldOf("baus").forGetter(m -> List.copyOf(m.baus))))
            .apply(i, ArtefatoNoBau::new));

    private final Item artefato;
    private final Set<ResourceLocation> baus;

    public ArtefatoNoBau(LootItemCondition[] condicoes, Item artefato, List<ResourceLocation> baus) {
        super(condicoes);
        this.artefato = artefato;
        this.baus = Set.copyOf(baus);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> itens, LootContext contexto) {
        if (!baus.contains(contexto.getQueriedLootTableId())) return itens;
        if (contexto.getRandom().nextDouble() < ConfigArtefatos.chanceBau()) {
            itens.add(new ItemStack(artefato));
        }
        return itens;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
