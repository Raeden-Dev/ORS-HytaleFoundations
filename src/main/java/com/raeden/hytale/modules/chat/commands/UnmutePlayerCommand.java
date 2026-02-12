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
import com.raeden.hytale.core.data.PlayerDataManager;
import com.raeden.hytale.core.data.PlayerProfile;
import com.raeden.hytale.core.utils.Permissions;
import com.raeden.hytale.lang.LangKey;

import javax.annotation.Nonnull;

import static com.raeden.hytale.HytaleFoundations.langManager;
import static com.raeden.hytale.core.utils.Permissions.isPlayerAdmin;
import static com.raeden.hytale.utils.GeneralUtils.findPlayerByName;

public class UnmutePlayerCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;
    private final RequiredArg<String> targetPlayer;

    public UnmutePlayerCommand(HytaleFoundations hytaleFoundations) {
        super("unmute", "Unmutes a player so they can speak again in chat.");
        this.hytaleFoundations = hytaleFoundations;
        targetPlayer = withRequiredArg("Player", "Player to execute command on.", ArgTypes.STRING);
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        boolean isAdmin = isPlayerAdmin(commandContext.sender());
        String senderUsername = commandContext.sender().getDisplayName();
        String targetUsername = commandContext.get(this.targetPlayer);

        if(!Permissions.hasPermission(commandContext.sender(), Permissions.HFPermissions.MUTE_PLAYER.getPermission()) && !isAdmin) {
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.NO_PERMISSION));
            return;
        }

        PlayerRef target = findPlayerByName("Mute Player Command", targetUsername);
        boolean isTargetOffline = false;
        if(target == null) {
            if(!hytaleFoundations.getPlayerDataManager().doesPlayerDataExist(targetUsername)) {
                commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.PLAYER_NEVER_JOINED, targetUsername));
                return;
            } else {
                isTargetOffline = true;
            }
        }

        PlayerDataManager dataManager = hytaleFoundations.getPlayerDataManager();
        PlayerProfile profile;
        if(isTargetOffline) {
            profile = dataManager.getPlayerProfileFromFile(targetUsername);
        } else {
            profile = dataManager.getPlayerProfile(targetUsername);
        }

        if(profile.isMuted()) {
            profile.setMuted(false);
            profile.setMuteDuration(0);
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.UNMUTED_PLAYER, targetUsername));
            if(target != null) target.sendMessage(langManager.getMessage(targetUsername, LangKey.PLAYER_UNMUTE_MSG, senderUsername));
        } else {
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.NOT_MUTED, targetUsername));
        }

        if(isTargetOffline) {
            dataManager.savePlayerData(targetUsername, dataManager.PROFILE_JSON, profile);
        }
    }
}
