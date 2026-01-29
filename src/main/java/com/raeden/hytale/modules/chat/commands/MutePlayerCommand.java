package com.raeden.hytale.modules.chat.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.data.PlayerDataManager;
import com.raeden.hytale.core.data.PlayerProfile;
import com.raeden.hytale.lang.LangKey;
import com.raeden.hytale.utils.TimeUtils;

import javax.annotation.Nonnull;

import static com.raeden.hytale.HytaleFoundations.langManager;
import static com.raeden.hytale.core.utils.Permissions.isPlayerAdmin;
import static com.raeden.hytale.utils.GeneralUtils.findPlayerByName;

public class MutePlayerCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;
    private final RequiredArg<String> targetPlayer;
    private final OptionalArg<String> duration;

    public MutePlayerCommand(HytaleFoundations hytaleFoundations) {
        super("mute", "Mutes a player so they can't speak in chat.");
        this.hytaleFoundations = hytaleFoundations;
        targetPlayer = withRequiredArg("Player", "Player to execute command on.", ArgTypes.STRING);
        duration = withOptionalArg("Duration", "Duration of the mute. (d|h|m|s eg. 1d8h5m33s)", ArgTypes.STRING);
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        boolean isAdmin = isPlayerAdmin(commandContext.sender());
        String senderUsername = commandContext.sender().getDisplayName();
        String targetUsername = commandContext.get(this.targetPlayer);

        if(!commandContext.sender().hasPermission("ors.foundations.mute") && !isAdmin) {
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.NO_PERMISSION));
            return;
        }

        long durationInMillis = TimeUtils.parseDuration(commandContext.get(this.duration));
        if(durationInMillis == 0) {
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.INCORRECT_TIME_FORMAT));
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

        long muteDuration = profile.getMuteDuration();
        long newMuteDuration = muteDuration + durationInMillis;

        if(profile.isMuted()) {
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.MUTE_DURATION_INCREASE,
                    targetUsername, TimeUtils.formatDuration(muteDuration),
                                    TimeUtils.formatDuration(newMuteDuration)));
            if(target != null) {
               target.sendMessage(langManager.getMessage(targetUsername, LangKey.PLAYER_MSG_MUTE_DURATION_INCREASE,
                        TimeUtils.formatDuration(newMuteDuration), senderUsername));
            }
            profile.setMuteDuration(newMuteDuration);
        } else {
            profile.setMuted(true);
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.MUTE_PLAYER, targetUsername,
                    TimeUtils.formatDuration(newMuteDuration)));
            if(target != null) {
                target.sendMessage(langManager.getMessage(targetUsername, LangKey.PLAYER_MUTE_MSG,
                        senderUsername, TimeUtils.formatDuration(newMuteDuration)));
            }
        }

        if(isTargetOffline) {
            dataManager.savePlayerData(targetUsername, dataManager.PROFILE_JSON, profile);
        }
    }
}
