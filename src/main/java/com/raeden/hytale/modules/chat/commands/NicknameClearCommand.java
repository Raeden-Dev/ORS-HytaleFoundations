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

import javax.annotation.Nonnull;

public class NicknameClearCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;
    //private final RequiredArg<String> targetPlayer;
    public NicknameClearCommand(HytaleFoundations hytaleFoundations) {
        super("clear", "Set nickname of other players");
        this.hytaleFoundations = hytaleFoundations;
        this.requirePermission(Permissions.HFPermissions.ADMIN.getPermission());
        //targetPlayer = withRequiredArg("Player", "Player to execute command on.", ArgTypes.STRING);
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        String senderUsername = commandContext.sender().getDisplayName();
        PlayerProfile senderProfile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(senderUsername);
        if(senderProfile != null) {
            senderProfile.setNickname("");
        }
    }
}
