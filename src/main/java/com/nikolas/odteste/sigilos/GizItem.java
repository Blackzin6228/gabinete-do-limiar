package com.nikolas.odteste.sigilos;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Giz. Botão direito no chão abre a tela dos sigilos que o jogador aprendeu; escolher um risca o desenho no chão
 * (o servidor confere tudo, ver {@link Sigilos#desenhar}). Gasta um pouco do giz a cada risco. Não faz item nem magia.
 */
public class GizItem extends Item {
    public GizItem(Properties propriedades) {
        super(propriedades);
    }

    @Override
    public void appendHoverText(ItemStack item, TooltipContext contexto, List<Component> linhas, TooltipFlag flag) {
        linhas.add(Component.translatable("item.odteste.giz.desc").withStyle(ChatFormatting.GRAY));
        linhas.add(Component.translatable("item.odteste.giz.preco").withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public InteractionResult useOn(UseOnContext contexto) {
        if (contexto.getClickedFace() != Direction.UP) return InteractionResult.PASS; // só o chão
        if (contexto.getPlayer() instanceof ServerPlayer jogador) {
            PacketDistributor.sendToPlayer(jogador, Sigilos.pacoteAbrir(jogador, contexto.getClickedPos()));
        }
        return InteractionResult.sidedSuccess(contexto.getLevel().isClientSide);
    }
}
