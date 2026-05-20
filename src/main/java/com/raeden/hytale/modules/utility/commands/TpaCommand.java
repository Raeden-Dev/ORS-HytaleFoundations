package com.raeden.hytale.modules.utility.commands;

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
import com.raeden.hytale.core.lang.LangKey;
import com.raeden.hytale.core.permission.Permissions;
import com.raeden.hytale.modules.utility.TeleportManager;

import javax.annotation.Nonnull;

import static com.raeden.hytale.HytaleFoundations.LM;

public class TpaCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;
    private final RequiredArg<PlayerRef> targetArg;

    public TpaCommand(HytaleFoundations hytaleFoundations) {
        super("tpa", "Request to teleport to another player.");
        this.requirePermission(Permissions.TPA.getPermission());
        this.hytaleFoundations = hytaleFoundations;
        this.targetArg = withRequiredArg("target", "Player you want to teleport to.", ArgTypes.PLAYER_REF);
    }

    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store,
                           @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        PlayerRef target = commandContext.get(this.targetArg);
        if (target == null) {
            playerRef.sendMessage(LM.getPlayerMessage(playerRef.getUsername(),
                    LangKey.PLAYER_NOT_FOUND_MSG, "target"));
            return;
        }
        TeleportManager tm = hytaleFoundations.getUtilityManager().getTeleportManager();
        tm.requestTpa(playerRef, target, false);
    }
}
