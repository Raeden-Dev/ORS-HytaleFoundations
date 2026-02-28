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
import com.raeden.hytale.core.utils.Permissions;
import com.raeden.hytale.lang.LangKey;
import com.raeden.hytale.utils.TimeUtils;

import javax.annotation.Nonnull;

import static com.raeden.hytale.HytaleFoundations.LM;
import static com.raeden.hytale.core.utils.Permissions.isPlayerAdmin;
import static com.raeden.hytale.utils.GeneralUtils.findPlayerByName;

public class MutePlayerCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;
    private final RequiredArg<String> targetPlayer;
    private final RequiredArg<String> duration;

    public MutePlayerCommand(HytaleFoundations hytaleFoundations) {
        super("mute", "Mutes a player so they can't speak in chat.");
        this.hytaleFoundations = hytaleFoundations;
        this.setAllowsExtraArguments(true);
        this.requirePermission(Permissions.HFPermissions.MUTE_PLAYER.getPermission());
        targetPlayer = withRequiredArg("Player", "Player to execute command on.", ArgTypes.STRING);
        duration = withRequiredArg("Duration", "Duration of the mute. (d|h|m|s eg. 1d8h5m33s)", ArgTypes.STRING);
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        boolean isAdmin = isPlayerAdmin(commandContext.sender());
        String senderUsername = commandContext.sender().getDisplayName();
        String targetUsername = commandContext.get(this.targetPlayer);
        String[] rawMessage = commandContext.getInputString().split("\\s+", 3);
        String duration = rawMessage[2];

        long durationInMillis = TimeUtils.parseDuration(duration);
        if(durationInMillis == 0) {
            commandContext.sender().sendMessage(LM.getMessage(senderUsername, LangKey.INVALID_TIME_FORMAT, false));
            return;
        }

        PlayerRef target = findPlayerByName("Mute Player Command", targetUsername);
        boolean isTargetOffline = false;
        if(target == null) {
            if(!hytaleFoundations.getPlayerDataManager().doesPlayerDataExist(targetUsername)) {
                commandContext.sender().sendMessage(LM.getMessage(senderUsername, LangKey.PLAYER_NOT_FOUND,false, targetUsername));
                return;
            } else {
                isTargetOffline = true;
            }
        }
        PlayerDataManager dataManager = hytaleFoundations.getPlayerDataManager();
        PlayerProfile profile = dataManager.getPlayerProfile(targetUsername);
        long muteDuration = profile.getMuteDuration();
        long newMuteDuration = muteDuration + durationInMillis;
        profile.setMuteDuration(newMuteDuration);

        if(profile.isMuted()) {
            commandContext.sender().sendMessage(LM.getMessage(senderUsername, LangKey.MUTE_ACTION_INCREASED,false,
                    targetUsername, TimeUtils.formatDuration(muteDuration),
                                    TimeUtils.formatDuration(newMuteDuration)));
            if(target != null) {
               target.sendMessage(LM.getMessage(targetUsername, LangKey.MUTE_NOTIFY_INCREASED,false,
                        TimeUtils.formatDuration(newMuteDuration), senderUsername));
            }
        } else {
            profile.setMuted(true);
            commandContext.sender().sendMessage(LM.getMessage(senderUsername, LangKey.MUTE_ACTION_SUCCESS,false, targetUsername,
                    TimeUtils.formatDuration(newMuteDuration)));
            if(target != null) {
                target.sendMessage(LM.getMessage(targetUsername, LangKey.MUTE_NOTIFY_ACTIVE,false,
                        senderUsername, TimeUtils.formatDuration(newMuteDuration)));
            }
        }

        if(isTargetOffline) {
            dataManager.savePlayerData(targetUsername, dataManager.PROFILE_FILENAME, profile);
        }
    }
}
