package com.nikolas.odteste.cracha;

import com.nikolas.odteste.registro.Registro;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Crachá do Gabinete: guarda o dono e, ao usar, abre o cartão na tela (só no cliente). */
public class CrachaItem extends Item {
    /** Gancho da tela. Fica vazio no servidor; o {@code OdModCliente} troca por quem abre a tela. */
    public static volatile Consumer<CrachaDados> abrirTela = dados -> {};

    public CrachaItem(Properties propriedades) {
        super(propriedades);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level mundo, Player jogador, InteractionHand mao) {
        ItemStack item = jogador.getItemInHand(mao);
        CrachaDados dados = item.get(Registro.CRACHA_DADOS);
        if (dados == null) return InteractionResultHolder.pass(item);
        // Código comum não conhece cliente/: quem abre a tela é o gancho que o cliente registra ao iniciar.
        if (mundo.isClientSide) abrirTela.accept(dados);
        return InteractionResultHolder.sidedSuccess(item, mundo.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack item, TooltipContext contexto, List<Component> linhas, TooltipFlag flag) {
        CrachaDados dados = item.get(Registro.CRACHA_DADOS);
        if (dados != null) {
            linhas.add(Component.translatable("odteste.cracha.agente", dados.nome()).withStyle(ChatFormatting.GRAY));
        }
    }
}
