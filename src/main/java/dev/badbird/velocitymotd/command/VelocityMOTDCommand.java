package dev.badbird.velocitymotd.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.badbird.velocitymotd.BuildConstants;
import dev.badbird.velocitymotd.VelocityMOTD;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.concurrent.CompletableFuture;

public class VelocityMOTDCommand {
    private static final Component ROOT_MESSAGE =
            Component.text("----------------------------------------").color(NamedTextColor.GRAY).decorate(TextDecoration.STRIKETHROUGH)
                    .append(Component.newline().decoration(TextDecoration.STRIKETHROUGH, false)
                            .append(Component.text("VelocityMOTD").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                            .append(Component.newline())
                            .append(Component.text("Version: ").color(NamedTextColor.GRAY).append(Component.text(BuildConstants.VERSION).color(NamedTextColor.GOLD)))
                            .append(Component.newline())
                            .append(Component.text("Author: ").color(NamedTextColor.GRAY).append(Component.text("Badbird5907").color(NamedTextColor.GOLD)))
                            .append(Component.newline())
                            .append(Component.text("Github: ").color(NamedTextColor.GRAY).append(Component.text("https://github.com/Badbird5907/VelocityMOTD")
                                    .clickEvent(ClickEvent.openUrl("https://github.com/Badbird5907/VelocityMOTD")).color(NamedTextColor.GOLD)))
                    ).append(Component.newline())
                    .append(Component.text("----------------------------------------").color(NamedTextColor.GRAY).decorate(TextDecoration.STRIKETHROUGH));

    public static BrigadierCommand createCommand(ProxyServer proxyServer, VelocityMOTD plugin) {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder.<CommandSource>literal("motd")
                .executes(context -> {
                    CommandSource source = context.getSource();
                    source.sendMessage(ROOT_MESSAGE);
                    return Command.SINGLE_SUCCESS;
                })
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("argument", StringArgumentType.word())
                        .requires(source -> source.hasPermission("velocitymotd.command.reload"))
                        .suggests((context, builder) -> {
                            CompletableFuture<Suggestions> future = new CompletableFuture<>();
                            future.complete(builder.suggest("reload").build());
                            return future;
                        })
                        .executes(context -> {
                                    String subCommand = context.getArgument("argument", String.class);
                                    if (subCommand.equalsIgnoreCase("reload")) {
                                        CommandSource source = context.getSource();
                                        long start = System.currentTimeMillis();
                                        plugin.loadConfig();
                                        long end = System.currentTimeMillis();
                                        source.sendMessage(Component.text("Reloaded config in " + (end - start) + "ms").color(NamedTextColor.GREEN));
                                    } else {
                                        CommandSource source = context.getSource();
                                        source.sendMessage(ROOT_MESSAGE);
                                    }
                                    return Command.SINGLE_SUCCESS;
                                }
                        )
                ).build();
        return new BrigadierCommand(node);
    }
}
