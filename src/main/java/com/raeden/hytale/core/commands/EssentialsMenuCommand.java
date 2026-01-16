package com.raeden.hytale.core.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.core.pages.EssentialsMainMenu;

import javax.annotation.Nonnull;

public class EssentialsMenuCommand extends AbstractPlayerCommand {
    public EssentialsMenuCommand() {
        super("menu", "Opens Hytale Essentials Main Menu.", false);
        this.addAliases("m", "mn");

    }

    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        Player player = store.getComponent(ref, Player.getComponentType());

        if(player == null) {
            return;
        }

        EssentialsMainMenu menu = new EssentialsMainMenu(playerRef);
        player.getPageManager().openCustomPage(ref, store, menu);
    }
}
