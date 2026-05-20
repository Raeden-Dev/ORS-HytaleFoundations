package com.raeden.hytale.modules.utility.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.permission.Permissions;

import javax.annotation.Nonnull;

public class TpdenyCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;

    public TpdenyCommand(HytaleFoundations hytaleFoundations) {
        super("tpdeny", "Deny a pending teleport request.");
        this.requirePermission(Permissions.ACCESS.getPermission());
        this.hytaleFoundations = hytaleFoundations;
    }

    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store,
                           @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        hytaleFoundations.getUtilityManager().getTeleportManager().denyTpa(playerRef);
    }
}
