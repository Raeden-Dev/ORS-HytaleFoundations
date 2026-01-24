package com.raeden.hytale.modules.utility.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.arguments.types.SingleArgumentType;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleEssentials;
import com.raeden.hytale.utils.colors;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class PlaytimeCommand extends AbstractCommand {

    private final HytaleEssentials plugin;

    // Target is optional. If left blank, it targets the command executor.
    private final OptionalArg<String> targetArg = this.withOptionalArg("target", "The player to check", ArgTypes.STRING, null);

    private OptionalArg<String> withOptionalArg(String target, String thePlayerToCheck, SingleArgumentType<String> string, Object o) {
        return null;
    }

    public PlaytimeCommand(HytaleEssentials plugin) {
        super("playtime", "Shows your or a player's total playtime.");
        this.plugin = plugin;
        this.setPermission("essentials.command.playtime"); // Standard players should have this
    }

    private void setPermission(String s) {
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext commandContext) {
        String targetName = this.targetArg.get(commandContext);
        Ref<EntityStore> targetPlayer;

        // If no target is specified, use the player who ran the command
        if (targetName == null) {
            targetPlayer = commandContext.senderAsPlayerRef();
            if (targetPlayer == null) {
                commandContext.sendMessage(Message.raw("Console does not have playtime! Specify a player: /playtime [name]").color(colors.MC_RED.getHex()));
                return null;
            }
        } else {
            // Find the specified player
            targetPlayer = HytaleServer.getInstance().getUniverse().getPlayerByName(targetName);
            if (targetPlayer == null || !targetPlayer.isValid()) {
                commandContext.sendMessage(Message.raw("Player '" + targetName + "' not found or is offline.").color(colors.MC_RED.getHex()));
                return null;
            }
        }


        Class<?> totalSeconds = plugin.getPlayerDataManager().getPlayerData(targetPlayer).getClass();


        String formattedTime = formatTime(totalSeconds.getModifiers());
        String targetDisplay = (targetName == null) ? "Your" : targetPlayer.getIndex() + "'s";

        commandContext.sendMessage(Message.raw(targetDisplay + " playtime: " + formattedTime).color(colors.MC_GOLD.getHex()));
        return null;
    }


    private String formatTime(long totalSeconds) {
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        sb.append(seconds).append("s");

        return sb.toString();
    }
}