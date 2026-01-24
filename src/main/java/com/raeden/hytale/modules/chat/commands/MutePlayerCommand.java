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
import com.raeden.hytale.HytaleEssentials;
import com.raeden.hytale.core.data.PlayerData;
import com.raeden.hytale.lang.LangKey;
import com.raeden.hytale.utils.TimeUtils;

import javax.annotation.Nonnull;

import static com.raeden.hytale.HytaleEssentials.langManager;
import static com.raeden.hytale.core.utils.Permissions.isPlayerAdmin;
import static com.raeden.hytale.utils.GeneralUtils.findPlayerByName;

public class MutePlayerCommand extends AbstractPlayerCommand {
    private final HytaleEssentials hytaleEssentials;
    private final RequiredArg<String> targetPlayer;
    private final OptionalArg<String> duration;

    public MutePlayerCommand(HytaleEssentials hytaleEssentials) {
        super("mute", "Mutes a player so they can't speak in chat.");
        this.hytaleEssentials = hytaleEssentials;
        targetPlayer = withRequiredArg("Player", "Player to execute command on.", ArgTypes.STRING);
        duration = withOptionalArg("Duration", "Duration of the mute. (d|h|m|s eg. 1d8h5m33s)", ArgTypes.STRING);
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        boolean isAdmin = isPlayerAdmin(commandContext.sender());
        String senderUsername = commandContext.sender().getDisplayName();
        String targetUsername = commandContext.get(this.targetPlayer);

        if(!commandContext.sender().hasPermission("ors.essentials.mute") && !isAdmin) {
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
            if(!hytaleEssentials.getPlayerDataManager().doesPlayerDataExist(targetUsername)) {
                commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.PLAYER_NEVER_JOINED, targetUsername));
                return;
            } else {
                isTargetOffline = true;
            }
        }

        PlayerData targetData;
        if(isTargetOffline) {
            targetData = hytaleEssentials.getPlayerDataManager().getPlayerDataFromFile(targetUsername);
        } else {
            targetData = hytaleEssentials.getPlayerDataManager().getPlayerData(targetUsername);
        }

        long muteDuration = targetData.getMuteDuration();
        long newMuteDuration = muteDuration + durationInMillis;

        if(targetData.isMuted()) {
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.MUTE_DURATION_INCREASE,
                    targetUsername, TimeUtils.formatDuration(muteDuration),
                                    TimeUtils.formatDuration(newMuteDuration)));
            if(target != null) {
               target.sendMessage(langManager.getMessage(targetUsername, LangKey.PLAYER_MSG_MUTE_DURATION_INCREASE,
                        TimeUtils.formatDuration(newMuteDuration), senderUsername));
            }
            targetData.setMuteDuration(newMuteDuration);
        } else {
            targetData.setMuted(true);
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.MUTE_PLAYER, targetUsername,
                    TimeUtils.formatDuration(newMuteDuration)));
            if(target != null) {
                target.sendMessage(langManager.getMessage(targetUsername, LangKey.PLAYER_MUTE_MSG,
                        senderUsername, TimeUtils.formatDuration(newMuteDuration)));
            }
        }

        if(isTargetOffline) {
            hytaleEssentials.getPlayerDataManager().savePlayerData(targetUsername, targetData);
        }
    }
}
