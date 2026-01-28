package com.raeden.hytale.modules.utility.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.data.PlayerData;
import com.raeden.hytale.core.data.PlayerDataManager;
import com.raeden.hytale.lang.LangKey;
import com.raeden.hytale.utils.TimeUtils;

import javax.annotation.Nonnull;

import static com.raeden.hytale.HytaleFoundations.langManager;
import static com.raeden.hytale.utils.GeneralUtils.findPlayerByName;

public class PlaytimeCommand extends AbstractPlayerCommand {

    private final HytaleFoundations hytaleFoundations;
    private final OptionalArg<String> targetPlayer;

    public PlaytimeCommand(HytaleFoundations plugin) {
        super("playtime", "Shows your or a player's total playtime.");
        this.hytaleFoundations = plugin;
        targetPlayer = withOptionalArg("Player", "Target player to check their playtime", ArgTypes.STRING);
    }


    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        String senderUsername = commandContext.sender().getDisplayName();
        String targetUsername = commandContext.get(this.targetPlayer);

        if(!commandContext.sender().hasPermission("ors.foundations.playtime")) {
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.NO_PERMISSION));
            return;
        }

        PlayerDataManager dataManager = hytaleFoundations.getPlayerDataManager();

        if(targetUsername == null) {
            PlayerData data = dataManager.getPlayerData(senderUsername);
            dataManager.savePlayTime(data);
            String playtime = TimeUtils.formatDuration(data.getPlayTimeMillis());
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.PLAYER_PLAYTIME, senderUsername, playtime));
            return;
        }

        PlayerRef target = findPlayerByName(targetUsername);
        if(target == null) {
            if(dataManager.doesPlayerDataExist(targetUsername)) {
                PlayerData data = dataManager.getPlayerDataFromFile(targetUsername);
                String playtime = TimeUtils.formatDuration(data.getPlayTimeMillis());
                commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.PLAYER_PLAYTIME, targetUsername, playtime));
            } else {
              commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.PLAYER_NEVER_JOINED, targetUsername));
            }
        } else {
            PlayerData data = dataManager.getPlayerData(target.getUsername());
            dataManager.savePlayTime(data);
            String playtime = TimeUtils.formatDuration(data.getPlayTimeMillis());
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.PLAYER_PLAYTIME, targetUsername, playtime));
        }
    }
}