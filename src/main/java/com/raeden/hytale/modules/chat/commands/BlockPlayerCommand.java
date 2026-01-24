package com.raeden.hytale.modules.chat.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleEssentials;
import com.raeden.hytale.core.data.PlayerData;
import com.raeden.hytale.lang.LangKey;

import javax.annotation.Nonnull;

import java.util.List;

import static com.raeden.hytale.HytaleEssentials.langManager;
import static com.raeden.hytale.core.utils.Permissions.isPlayerAdmin;
import static com.raeden.hytale.utils.GeneralUtils.findPlayerByName;

public class BlockPlayerCommand extends AbstractPlayerCommand {
    private final HytaleEssentials hytaleEssentials;
    private final RequiredArg<String> targetPlayer;

    public BlockPlayerCommand(HytaleEssentials hytaleEssentials) {
        super("block", "Blocks a player so they can't interact with you anymore.");
        this.hytaleEssentials = hytaleEssentials;
        targetPlayer = withRequiredArg("Player", "Player to execute command on.", ArgTypes.STRING);
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        boolean isAdmin = isPlayerAdmin(commandContext.sender());
        String senderUsername = commandContext.sender().getDisplayName();
        String targetUsername = commandContext.get(this.targetPlayer);

        if(!commandContext.sender().hasPermission("ors.essentials.block") && !isAdmin) {
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.NO_PERMISSION));
            return;
        }

        PlayerRef receiver = findPlayerByName("Block Player Command", targetUsername);
        if(receiver == null) {
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.RECEIVER_NOT_ONLINE, targetUsername));
            return;
        }

        PlayerData senderData = hytaleEssentials.getPlayerDataManager().getPlayerData(senderUsername);

        List<String> blockedPlayers = senderData.getBlockedPlayers();
        if(blockedPlayers.contains(targetUsername)) {
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.BLOCKED_PLAYER_ALREADY, targetUsername));
        } else {
            senderData.addNewBlockedPlayer(targetUsername);
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.BLOCKED_PLAYER, targetUsername));
        }
    }
}
