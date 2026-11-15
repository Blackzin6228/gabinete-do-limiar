package com.nikolas.odteste.comandos;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.nikolas.odteste.cracha.Kit;
import com.nikolas.odteste.dados.DadosJogador;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * Kit do agente (crachá e dossiê), só de admin (dentro de /odteste, permissão 2):
 *
 * /odteste kit dar <jogadores> (dá de novo, sem mexer na marca) · esquecer <jogadores> (o kit sai de novo no próximo login)
 *
 * Mensagens só para o admin, em texto direto.
 */
public final class ComandosKit {
    private static final String JOGADORES = "jogadores";

    private ComandosKit() {}

    public static LiteralArgumentBuilder<CommandSourceStack> kit() {
        return Commands.literal("kit").requires(s -> s.hasPermission(2))
                .then(Commands.literal("dar").then(Commands.argument(JOGADORES, EntityArgument.players())
                        .executes(ComandosKit::dar)))
                .then(Commands.literal("esquecer").then(Commands.argument(JOGADORES, EntityArgument.players())
                        .executes(ComandosKit::esquecer)));
    }

    private static int dar(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        int n = 0;
        for (ServerPlayer p : EntityArgument.getPlayers(c, JOGADORES)) {
            Kit.dar(p);
            n++;
        }
        int total = n;
        c.getSource().sendSuccess(() -> Component.literal("Kit entregue a " + total + " jogador(es)."), false);
        return n;
    }

    private static int esquecer(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        int n = 0;
        for (ServerPlayer p : EntityArgument.getPlayers(c, JOGADORES)) {
            DadosJogador.marcarKit(p, false);
            n++;
        }
        int total = n;
        c.getSource().sendSuccess(() -> Component.literal(
                "Kit esquecido por " + total + " jogador(es): sai de novo no próximo login."), false);
        return n;
    }
}
