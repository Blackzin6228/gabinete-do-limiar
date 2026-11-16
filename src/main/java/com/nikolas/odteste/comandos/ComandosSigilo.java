package com.nikolas.odteste.comandos;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.nikolas.odteste.sigilos.IdsSigilos;
import com.nikolas.odteste.sigilos.RegrasSigilos;
import com.nikolas.odteste.sigilos.Sigilos;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * Sigilos aprendidos, só de admin (dentro de /odteste, permissão 2):
 *
 * /odteste sigilo ensinar|esquecer &lt;jogador&gt; &lt;sigilo|todos&gt; · lista &lt;jogador&gt;
 *
 * O jogador não tem comando nem tela para isso. Mensagens só para o admin (aí pode ter contagem).
 */
public final class ComandosSigilo {
    private static final String JOGADOR = "jogador";
    private static final String SIGILO = "sigilo";
    private static final String TODOS = "todos";

    private static final DynamicCommandExceptionType SIGILO_INVALIDO = new DynamicCommandExceptionType(
            nome -> Component.translatable("odteste.comando.sigilo.erro", nome));

    private ComandosSigilo() {}

    public static LiteralArgumentBuilder<CommandSourceStack> sigilo() {
        return Commands.literal("sigilo").requires(s -> s.hasPermission(2))
                .then(Commands.literal("ensinar").then(Commands.argument(JOGADOR, EntityArgument.player())
                        .then(nome().executes(c -> mudar(c, true)))))
                .then(Commands.literal("esquecer").then(Commands.argument(JOGADOR, EntityArgument.player())
                        .then(nome().executes(c -> mudar(c, false)))))
                .then(Commands.literal("lista").then(Commands.argument(JOGADOR, EntityArgument.player())
                        .executes(ComandosSigilo::lista)));
    }

    private static com.mojang.brigadier.builder.RequiredArgumentBuilder<CommandSourceStack, String> nome() {
        return Commands.argument(SIGILO, StringArgumentType.word()).suggests((c, b) -> {
            List<String> opcoes = new ArrayList<>(IdsSigilos.SIMBOLOS);
            opcoes.add(TODOS);
            return SharedSuggestionProvider.suggest(opcoes, b);
        });
    }

    private static int mudar(CommandContext<CommandSourceStack> c, boolean ensinar) throws CommandSyntaxException {
        ServerPlayer p = EntityArgument.getPlayer(c, JOGADOR);
        String nome = StringArgumentType.getString(c, SIGILO);
        List<String> alvos = TODOS.equals(nome) ? IdsSigilos.SIMBOLOS : List.of(nome);
        if (!TODOS.equals(nome) && !RegrasSigilos.valido(nome)) throw SIGILO_INVALIDO.create(nome);
        int mudou = 0;
        for (String id : alvos) {
            if (ensinar ? Sigilos.aprender(p, id, alvos.size() == 1) : Sigilos.esquecer(p, id)) mudou++;
        }
        int total = mudou;
        c.getSource().sendSuccess(() -> Component.translatable(
                ensinar ? "odteste.comando.sigilo.ensinou" : "odteste.comando.sigilo.esqueceu",
                p.getDisplayName(), total), false);
        return mudou;
    }

    private static int lista(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
        ServerPlayer p = EntityArgument.getPlayer(c, JOGADOR);
        List<String> l = RegrasSigilos.naOrdemOficial(Sigilos.conhecidos(p));
        Component msg = l.isEmpty()
                ? Component.translatable("odteste.comando.sigilo.vazia", p.getDisplayName())
                : Component.translatable("odteste.comando.sigilo.lista", p.getDisplayName(), l.size(), String.join(", ", l));
        c.getSource().sendSuccess(() -> msg, false);
        return l.size();
    }
}
