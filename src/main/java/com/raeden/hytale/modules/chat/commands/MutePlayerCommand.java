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
import com.raeden.hytale.core.permission.Permissions;
import com.raeden.hytale.core.lang.LangKey;
import com.raeden.hytale.utils.TimeUtils;
import javax.annotation.Nonnull;
import static com.raeden.hytale.HytaleFoundations.LM;
import static com.raeden.hytale.utils.PlayerUtils.findPlayerByName;

public class MutePlayerCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;
    private final RequiredArg<String> targetPlayer;
    private final RequiredArg<String> duration;

    public MutePlayerCommand(HytaleFoundations hytaleFoundations) {
        super("mute", "Mutes a player so they can't speak in chat.");
        this.hytaleFoundations = hytaleFoundations;
        this.requirePermission(Permissions.MUTE_PLAYER.getPermission());
        targetPlayer = withRequiredArg("Player", "Player to execute command on.", ArgTypes.STRING);
        duration = withRequiredArg("Duration", "Duration of the mute. (d|h|m|s eg. 1d8h5m33s)", ArgTypes.STRING);
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        String senderUsername = commandContext.sender().getDisplayName();
        String targetUsername = commandContext.get(this.targetPlayer);
        String duration = commandContext.get(this.duration);

        long durationInMillis = TimeUtils.parseDuration(duration);
        if(durationInMillis == 0) {
            commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.INVALID_TIME_FORMAT));
            return;
        }

        PlayerRef target = findPlayerByName(targetUsername);
        PlayerDataManager dataManager = hytaleFoundations.getPlayerDataManager();
        PlayerProfile profile = dataManager.getPlayerProfile(targetUsername);
        if(profile == null) {
            commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PLAYER_NOT_FOUND, targetUsername));
            return;
        }
        long muteDuration = profile.getMuteDuration();
        long newMuteDuration = muteDuration + durationInMillis;
        profile.setMuteDuration(newMuteDuration);

        if(profile.isMuted()) {
            commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.MUTE_ACTION_INCREASED,
                    targetUsername, TimeUtils.formatDuration(muteDuration),
                                    TimeUtils.formatDuration(newMuteDuration)));
            if(target != null) {
               target.sendMessage(LM.getPlayerMessage(targetUsername, LangKey.MUTE_NOTIFY_INCREASED,
                        TimeUtils.formatDuration(newMuteDuration), senderUsername));
            }
        } else {
            profile.setMuted(true);
            commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.MUTE_ACTION_SUCCESS, targetUsername,
                    TimeUtils.formatDuration(newMuteDuration)));
            if(target != null) {
                target.sendMessage(LM.getPlayerMessage(targetUsername, LangKey.MUTE_NOTIFY_ACTIVE,
                        senderUsername, TimeUtils.formatDuration(newMuteDuration)));
            }
        }

        if(target == null) dataManager.savePlayerData(targetUsername, dataManager.PROFILE_FILENAME, profile);
    }
}
