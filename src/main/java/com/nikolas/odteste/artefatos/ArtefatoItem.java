package com.nikolas.odteste.artefatos;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * Um dos 12 artefatos. Qualquer jogador usa. Vale no slot de acessório do Curios (ou na mão); sem o Curios, em
 * qualquer lugar do inventário. A descrição mostra o que dá e o que cobra, sem número.
 */
public final class ArtefatoItem extends Item {
    public final TipoArtefato tipo;

    public ArtefatoItem(TipoArtefato tipo) {
        super(propriedades(tipo));
        this.tipo = tipo;
    }

    private static Item.Properties propriedades(TipoArtefato tipo) {
        Item.Properties p = new Item.Properties().rarity(Rarity.UNCOMMON);
        return tipo.durabilidade > 0 ? p.durability(tipo.durabilidade) : p.stacksTo(1);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level mundo, Player jogador, InteractionHand mao) {
        ItemStack item = jogador.getItemInHand(mao);
        if (jogador instanceof ServerPlayer p && Artefatos.usar(p, item, tipo)) {
            return InteractionResultHolder.consume(item);
        }
        return InteractionResultHolder.pass(item);
    }

    @Override
    public void appendHoverText(ItemStack item, TooltipContext contexto, List<Component> linhas, TooltipFlag flag) {
        String base = "item.odteste." + tipo.id;
        if (tipo.emBreve) {
            linhas.add(Component.translatable(base + ".desc").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            linhas.add(Component.translatable("odteste.artefato.em_breve").withStyle(ChatFormatting.DARK_GRAY));
        } else {
            linhas.add(Component.translatable(base + ".desc").withStyle(ChatFormatting.GRAY));
            linhas.add(Component.translatable(base + ".preco").withStyle(ChatFormatting.DARK_RED));
        }
        // TODO(magias): afinidade = força das magias do elemento (Iron's Spells). Volta quando as magias existirem.
        linhas.add(Component.translatable("odteste.artefato.afinidade",
                        Component.translatable(tipo.elemento.chaveTraducao()))
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }
}
