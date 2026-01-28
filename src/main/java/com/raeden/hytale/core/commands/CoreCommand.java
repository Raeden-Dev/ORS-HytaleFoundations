package com.raeden.hytale.core.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;

import javax.annotation.Nonnull;

public class CoreCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;

    public CoreCommand(HytaleFoundations hytaleFoundations) {
        super("foundation", "Argument for all Hytale Foundations Command.");
        this.hytaleFoundations = hytaleFoundations;
        this.addAliases("hf", "fd");
        this.addSubCommand(new PluginMenuCommand());
    }

    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

    }

}
