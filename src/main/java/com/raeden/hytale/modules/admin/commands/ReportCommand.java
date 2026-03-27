package com.raeden.hytale.modules.admin.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.modules.admin.pages.PlayerReportPage;

import javax.annotation.Nonnull;

public class ReportCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;
    public ReportCommand(HytaleFoundations hytaleFoundations) {
        super("report", "Report a player who broke any server rule.");
        this.hytaleFoundations = hytaleFoundations;
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        Player player = store.getComponent(ref, Player.getComponentType());
        PlayerReportPage page = new PlayerReportPage(hytaleFoundations, playerRef);
        if(player == null) return;
        player.getPageManager().openCustomPage(ref, store, page);
    }
}

