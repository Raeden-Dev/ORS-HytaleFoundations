package com.raeden.hytale.modules.chat.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;

import javax.annotation.Nonnull;

import static com.raeden.hytale.core.utils.PermissionManager.isPlayerAdmin;

public class UnmuteAllPlayerCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;
    public UnmuteAllPlayerCommand(HytaleFoundations hytaleFoundations) {
        super("unmuteall", "Unmutes all players in server so they can speak again in chat.");
        this.hytaleFoundations = hytaleFoundations;
        this.setAllowsExtraArguments(true);
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        boolean isAdmin = isPlayerAdmin(commandContext.sender());
        String senderUsername = commandContext.sender().getDisplayName();
    }
}
