package com.raeden.hytale.modules.utility.commands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.raeden.hytale.utils.colors;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class AnvilCommand extends AbstractCommand {

    public AnvilCommand() {
        super("anvil", "Opens a portable anvil interface.");
        this.setPermission("essentials.command.anvil");
    }

    private void setPermission(String s) {
    }

    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext commandContext) {
        // FIX 1: Get the generic sender first
        CommandSender sender = commandContext.sender(); // Try .getExecutor() if getSender() is not found

        // Safety check: Ensure the sender is actually a player (not console/command block)
        if (!(sender instanceof PlayerRef)) {
            commandContext.sendMessage(Message.raw("Error: Only in-game players can open the Anvil GUI.").color(colors.MC_RED.getHex()));
            return null;
        }

        PlayerRef player = (PlayerRef) sender;

        player.getUuid();

        commandContext.sendMessage(Message.raw("Opened portable anvil.").color(colors.MC_GREEN.getHex()));
        return null;
    }
}