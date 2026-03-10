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
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.player.PlayerProfile;
import com.raeden.hytale.core.utils.Permissions;
import com.raeden.hytale.core.lang.LangKey;
import javax.annotation.Nonnull;
import java.util.List;
import static com.raeden.hytale.HytaleFoundations.LM;

public class UnblockPlayerCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;
    private final RequiredArg<String> targetPlayer;

    public UnblockPlayerCommand(HytaleFoundations hytaleFoundations) {
        super("unblock", "Unblocks a player so they can interact with you again.");
        this.hytaleFoundations = hytaleFoundations;
        this.requirePermission(Permissions.BLOCK_PLAYER.getPermission());
        targetPlayer = withRequiredArg("Player", "Player to execute command on.", ArgTypes.STRING);
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        boolean isAdmin = hytaleFoundations.getPermissionManager().isPlayerAdmin(commandContext.sender());
        String senderUsername = commandContext.sender().getDisplayName();
        String targetUsername = commandContext.get(this.targetPlayer);
        PlayerProfile profile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(senderUsername);
        if(profile == null) {
            commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PLAYER_NOT_FOUND_MSG, targetUsername));
            return;
        }
        List<String> blockedPlayers = profile.getBlockedPlayers();
        if(blockedPlayers.contains(targetUsername)) {
            profile.removeBlockedPlayer(targetUsername);
            commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.UNBLOCK_SUCCESS,targetUsername));
        } else {
            commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.UNBLOCK_NOT_FOUND,targetUsername));
        }
    }
}
