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
import com.raeden.hytale.core.data.PlayerProfile;
import com.raeden.hytale.core.utils.Permissions;
import com.raeden.hytale.lang.LangKey;

import javax.annotation.Nonnull;

import static com.raeden.hytale.HytaleFoundations.langManager;

public class NicknameClearCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;
    //private final RequiredArg<String> targetPlayer;
    public NicknameClearCommand(HytaleFoundations hytaleFoundations) {
        super("clear", "Set nickname of other players");
        this.hytaleFoundations = hytaleFoundations;
        this.requirePermission(Permissions.HFPermissions.ADMIN.getPermission());
        this.setAllowsExtraArguments(true);
        //targetPlayer = withRequiredArg("Player", "Player to execute command on.", ArgTypes.STRING);
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        String[] rawMessage = commandContext.getInputString().split("\\s+", 3);
        String targetPlayer = rawMessage[2];
        String senderUsername = commandContext.sender().getDisplayName();
        if(targetPlayer == null || targetPlayer.isEmpty()) {
            PlayerProfile senderProfile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(senderUsername);
            if(senderProfile != null) {
                senderProfile.setNickname("");
            }
        } else {
            PlayerProfile targetProfile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(targetPlayer);
            if(targetProfile == null) {
                commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.PLAYER_NOT_FOUND_MSG,false, targetPlayer));
                return;
            }
            targetProfile.setNickname("");

        }
    }
}
