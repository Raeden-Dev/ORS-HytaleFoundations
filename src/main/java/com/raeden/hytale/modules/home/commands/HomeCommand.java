package com.raeden.hytale.modules.home.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;

import javax.annotation.Nonnull;

public class HomeCommand extends AbstractCommandCollection {
    public HomeCommand(HytaleFoundations hytaleFoundations) {
        super("home", "Argument for all home related commands.");
    }

    public static class HomeSetCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        public HomeSetCommand(HytaleFoundations hytaleFoundations) {
            super("set", "Set home at your current location.");
            this.hytaleFoundations = hytaleFoundations;
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

        }
    }
    public static class HomeDeleteCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        public HomeDeleteCommand(HytaleFoundations hytaleFoundations) {
            super("delete", "Delete a specific home that you've previously set.");
            this.hytaleFoundations = hytaleFoundations;
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

        }
    }

}
