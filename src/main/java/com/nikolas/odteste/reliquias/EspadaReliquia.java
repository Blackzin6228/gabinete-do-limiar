package com.nikolas.odteste.reliquias;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Unbreakable;

/**
 * Uma das 4 espadas. Sem receita: só pela aba do criativo do mod e por {@code /give}. Inquebrável (relíquia única
 * não pode sumir). O poder mora em {@link Espadas} (eventos) e só vale para quem é do elemento dela.
 */
public final class EspadaReliquia extends SwordItem {
    public final TipoEspada tipo;

    public EspadaReliquia(TipoEspada tipo) {
        super(Tiers.NETHERITE, new Item.Properties()
                .stacksTo(1)
                .fireResistant()
                .rarity(Rarity.EPIC)
                .component(DataComponents.UNBREAKABLE, new Unbreakable(false))
                .attributes(SwordItem.createAttributes(Tiers.NETHERITE, tipo.dano, tipo.velocidade)));
        this.tipo = tipo;
    }

    @Override
    public void appendHoverText(ItemStack item, TooltipContext contexto, List<Component> linhas, TooltipFlag flag) {
        linhas.add(Component.translatable("item.odteste." + tipo.id + ".desc").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        linhas.add(Component.translatable("odteste.espada.so_do_elemento",
                        Component.translatable(tipo.elemento.chaveTraducao()))
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}
