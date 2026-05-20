package com.raeden.hytale.modules.utility.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.lang.LangKey;
import com.raeden.hytale.core.permission.Permissions;
import com.raeden.hytale.modules.utility.TeleportManager;

import javax.annotation.Nonnull;

import static com.raeden.hytale.HytaleFoundations.LM;

public class RtpCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;

    public RtpCommand(HytaleFoundations hytaleFoundations) {
        super("rtp", "Teleport to a random location nearby.");
        this.requirePermission(Permissions.RTP.getPermission());
        this.addAliases("wild");
        this.hytaleFoundations = hytaleFoundations;
    }

    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store,
                           @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        TeleportManager tm = hytaleFoundations.getUtilityManager().getTeleportManager();
        Vector3d destination = tm.randomTeleport(playerRef);
        if (destination == null) {
            playerRef.sendMessage(LM.getPlayerMessage(playerRef.getUsername(), LangKey.RTP_FAILURE));
            return;
        }
        playerRef.sendMessage(LM.getPlayerMessage(playerRef.getUsername(), LangKey.RTP_SUCCESS,
                String.valueOf((long) destination.getX()),
                String.valueOf((long) destination.getY()),
                String.valueOf((long) destination.getZ())));
    }
}
