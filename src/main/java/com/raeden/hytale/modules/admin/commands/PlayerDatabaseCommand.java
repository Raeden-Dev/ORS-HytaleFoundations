package com.raeden.hytale.modules.admin.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.utils.Permissions;
import com.raeden.hytale.modules.admin.pages.AdminPlayerDatabasePage;

import javax.annotation.Nonnull;

/**
 * Admin command to open the Player Database UI.
 * Usage: /playerdb
 */
public class PlayerDatabaseCommand extends AbstractPlayerCommand {

    private final HytaleFoundations plugin;

    public PlayerDatabaseCommand(HytaleFoundations plugin) {
        super("playerdb", "Open the admin Player Database UI.", false);
        this.addAliases("pdb", "playersdb");
        this.plugin = plugin;
    }

    @Override
    protected void execute(
            @Nonnull CommandContext commandContext,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world
    ) {

        /*
         * STEP 1 — Get Player component
         * (Permissions works with Player, not PlayerRef)
         */
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;


        /*
         * STEP 2 — Permission check
         */
        if (!Permissions.isPlayerAdmin(player)) {
            playerRef.sendMessage(Message.raw("You don't have permission."));
            return;
        }


        /*
         * STEP 3 — Open UI page
         */
        AdminPlayerDatabasePage page =
                new AdminPlayerDatabasePage(
                        plugin,
                        playerRef,
                        com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime.CanDismiss
                );

        player.getPageManager().openCustomPage(ref, store, page);
    }
}
