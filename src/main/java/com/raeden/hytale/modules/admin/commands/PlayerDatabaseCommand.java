package com.raeden.hytale.modules.admin.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.modules.admin.pages.AdminPlayerDBPage;

import javax.annotation.Nonnull;

import static com.raeden.hytale.core.utils.Permissions.isPlayerAdmin;

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

        CommandSender sender = commandContext.sender();
        if (!isPlayerAdmin(sender)) {
            sender.sendMessage(Message.raw("You don't have permission."));
            return;
        }

        Player player = store.getComponent(ref, Player.getComponentType());
        if(player == null) {
            return;
        }
        AdminPlayerDBPage dbPage = new AdminPlayerDBPage(playerRef, CustomPageLifetime.CanDismiss);


        player.getPageManager().openCustomPage(ref, store, dbPage);
    }
}
