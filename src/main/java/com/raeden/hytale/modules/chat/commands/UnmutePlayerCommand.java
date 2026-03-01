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
import com.raeden.hytale.core.player.PlayerDataManager;
import com.raeden.hytale.core.player.PlayerProfile;
import com.raeden.hytale.lang.LangKey;

import javax.annotation.Nonnull;

import static com.raeden.hytale.HytaleFoundations.LM;
import static com.raeden.hytale.core.utils.PermissionManager.isPlayerAdmin;
import static com.raeden.hytale.utils.GeneralUtils.findPlayerByName;

public class UnmutePlayerCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;
    private final RequiredArg<String> targetPlayer;

    public UnmutePlayerCommand(HytaleFoundations hytaleFoundations) {
        super("unmute", "Unmutes a player so they can speak again in chat.");
        this.hytaleFoundations = hytaleFoundations;
        this.requirePermission(PermissionNodes.MUTE_PLAYER.getPermission());
        targetPlayer = withRequiredArg("Player", "Player to execute command on.", ArgTypes.STRING);
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        boolean isAdmin = isPlayerAdmin(commandContext.sender());
        String senderUsername = commandContext.sender().getDisplayName();
        String targetUsername = commandContext.get(this.targetPlayer);

        PlayerRef target = findPlayerByName("Mute Player Command", targetUsername);
        boolean isTargetOffline = false;
        if(target == null) {
            if(!hytaleFoundations.getPlayerDataManager().doesPlayerDataExist(targetUsername)) {
                commandContext.sender().sendMessage(LM.getMessage(senderUsername, LangKey.PLAYER_NOT_FOUND, false, targetUsername));
                return;
            } else {
                isTargetOffline = true;
            }
        }
        PlayerDataManager dataManager = hytaleFoundations.getPlayerDataManager();
        PlayerProfile profile = dataManager.getPlayerProfile(targetUsername);
        if(profile.isMuted()) {
            profile.setMuted(false);
            profile.setMuteDuration(0);
            commandContext.sender().sendMessage(LM.getMessage(senderUsername, LangKey.UNMUTE_ACTION_SUCCESS,false, targetUsername));
            if(target != null) target.sendMessage(LM.getMessage(targetUsername, LangKey.UNMUTE_NOTIFY_ACTIVE,false, senderUsername));
        } else {
            commandContext.sender().sendMessage(LM.getMessage(senderUsername, LangKey.UNMUTE_ERROR_NOT_MUTED,false, targetUsername));
        }

        if(isTargetOffline) {
            dataManager.savePlayerData(targetUsername, dataManager.PROFILE_FILENAME, profile);
        }
    }
}
