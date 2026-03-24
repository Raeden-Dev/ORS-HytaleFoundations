package com.raeden.hytale.modules.home.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.modules.home.pages.HomesPage;

import javax.annotation.Nonnull;

public class HomesCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;
    public HomesCommand(HytaleFoundations hytaleFoundations) {
        super("homes", "Open UI to check out your available homes.");
        this.hytaleFoundations = hytaleFoundations;
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        Player player = store.getComponent(ref, Player.getComponentType());
        HomesPage page = new HomesPage(hytaleFoundations, playerRef);
        if(player == null) return;
        player.getPageManager().openCustomPage(ref, store, page);
    }
}
