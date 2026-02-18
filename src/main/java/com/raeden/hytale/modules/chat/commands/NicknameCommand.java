package com.raeden.hytale.modules.chat.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.data.PlayerProfile;
import com.raeden.hytale.core.utils.Permissions;

import javax.annotation.Nonnull;

import static com.raeden.hytale.core.utils.Permissions.isPlayerAdmin;
import static com.raeden.hytale.utils.GeneralUtils.findPlayerByName;

public class NicknameCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;
    public NicknameCommand(HytaleFoundations hytaleFoundations) {
        super("nick", "Argument to access rest of /nick functions");
        this.hytaleFoundations = hytaleFoundations;
        this.requirePermission(Permissions.HFPermissions.NICK.getPermission());
        this.setAllowsExtraArguments(true);
        this.addSubCommand(new NicknameSetCommand(hytaleFoundations));
        this.addSubCommand(new NicknameClearCommand(hytaleFoundations));
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        String senderUsername = commandContext.sender().getDisplayName();
        String[] rawMessage = commandContext.getInputString().split("\\s+", 2);
        String nicknameContent = rawMessage[1];

        PlayerProfile senderProfile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(senderUsername);
        if(senderProfile != null) {
            senderProfile.setNickname(nicknameContent);
        }
    }
}
