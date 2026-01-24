package com.raeden.hytale.modules.utility.commands;

import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.arguments.types.SingleArgumentType;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.raeden.hytale.HytaleEssentials;
import com.raeden.hytale.utils.colors;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class TitleCommand extends AbstractCommand {

    private final RequiredArg<String> targetArg;
    private final RequiredArg<String> titleArg;
    private final RequiredArg<String> messageArg;


    private final OptionalArg<Integer> fadeInArg;
    private final OptionalArg<Integer> stayArg;
    private final OptionalArg<Integer> fadeOutArg;


    public TitleCommand(HytaleEssentials plugin) {
        super("title", "Sends a customized title to a player or all players.");


        this.setPermission("essentials.command.title");

        // Fetch the defaults directly from your JSON config object
        int defaultFadeIn = plugin.getConfigManager().getDefaultConfig().getTitleDefaultFadeIn();
        int defaultStay = plugin.getConfigManager().getDefaultConfig().getTitleDefaultStay();
        int defaultFadeOut = plugin.getConfigManager().getDefaultConfig().getTitleDefaultFadeOut();


        this.targetArg = this.withRequiredArg("target", "Target player or 'all'", ArgTypes.STRING);
        this.titleArg = this.withRequiredArg("title", "The main title text (supports & color codes)", ArgTypes.STRING);
        this.messageArg = this.withRequiredArg("message", "The subtitle message (supports & color codes)", ArgTypes.STRING);

        // Optional arguments use the config defaults if the user doesn't specify them
        this.fadeInArg = this.withOptionalArg("fadeIn", "Fade in duration (ticks)", ArgTypes.INTEGER, defaultFadeIn);
        this.stayArg = this.withOptionalArg("stay", "Display duration (ticks)", ArgTypes.INTEGER, defaultStay);
        this.fadeOutArg = this.withOptionalArg("fadeOut", "Fade out duration (ticks)", ArgTypes.INTEGER, defaultFadeOut);
    }

    private void setPermission(String s) {
    }

    private OptionalArg<Integer> withOptionalArg(String stay, String s, SingleArgumentType<Integer> integer, int defaultStay) {
        return null;
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext commandContext) {
        String target = this.targetArg.get(commandContext);
        String mainTitle = this.titleArg.get(commandContext);
        String subTitle = this.messageArg.get(commandContext);

        int fadeIn = this.fadeInArg.get(commandContext);
        int stay = this.stayArg.get(commandContext);
        int fadeOut = this.fadeOutArg.get(commandContext);


        Message titleMsg = Message.raw(colorize(mainTitle));
        Message subtitleMsg = Message.raw(colorize(subTitle));


        if (target.equalsIgnoreCase("all")) {
            HytaleServer.get().getUniverse().getPlayers().forEach(playerRef -> {
                sendTitleToPlayer(playerRef, titleMsg, subtitleMsg, fadeIn, stay, fadeOut);
            });
            commandContext.sendMessage(Message.raw("Title sent to all players.").color(colors.MC_GREEN.getHex()));
            return null;
        }


        PlayerRef targetPlayerRef = HytaleServer.get().getUniverse().getPlayerByName(target);
        if (targetPlayerRef != null && targetPlayerRef.isValid()) {
            sendTitleToPlayer(targetPlayerRef, titleMsg, subtitleMsg, fadeIn, stay, fadeOut);
            commandContext.sendMessage(Message.raw("Title sent to " + target).color(colors.MC_GREEN.getHex()));
        } else {
            commandContext.sendMessage(Message.raw("Failed to send: Player '" + target + "' not found!").color(colors.MC_RED.getHex()));
        }
        return null;
    }

    private void sendTitleToPlayer(PlayerRef player, Message title, Message subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }


    private String colorize(String text) {
        return text.replace("&", "§");
    }
}