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
import com.raeden.hytale.core.permission.Permissions;

import javax.annotation.Nonnull;

public class HomeCommand extends AbstractCommandCollection {
    public HomeCommand(HytaleFoundations hytaleFoundations) {
        super("home", "Argument for all home related commands.");
        this.requirePermission(Permissions.HOME.getPermission());
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
    public static class HomeListCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        public HomeListCommand(HytaleFoundations hytaleFoundations) {
            super("list", "Opens a UI that shows list of all your available homes.");
            this.hytaleFoundations = hytaleFoundations;
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

        }
    }
    public static class HomeListAdminCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        public HomeListAdminCommand(HytaleFoundations hytaleFoundations) {
            super("list", "Admin command to check all player's available homes.");
            this.requirePermission(Permissions.HOME_ADMIN.getPermission());
            this.hytaleFoundations = hytaleFoundations;
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

        }
    }
}
