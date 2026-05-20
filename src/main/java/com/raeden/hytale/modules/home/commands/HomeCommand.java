package com.raeden.hytale.modules.home.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Location;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.lang.LangKey;
import com.raeden.hytale.core.permission.Permissions;
import com.raeden.hytale.core.player.PlayerProfile;
import com.raeden.hytale.modules.utility.TeleportManager;

import javax.annotation.Nonnull;
import java.util.Map;

import static com.raeden.hytale.HytaleFoundations.LM;

public class HomeCommand extends AbstractCommandCollection {
    public HomeCommand(HytaleFoundations hytaleFoundations) {
        super("home", "Argument for all home related commands.");
        this.requirePermission(Permissions.HOME.getPermission());
        this.addSubCommand(new HomeSetCommand(hytaleFoundations));
        this.addSubCommand(new HomeDeleteCommand(hytaleFoundations));
        this.addSubCommand(new HomeListCommand(hytaleFoundations));
        this.addSubCommand(new HomeTeleportCommand(hytaleFoundations));
    }

    public static class HomeSetCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> homeName;
        public HomeSetCommand(HytaleFoundations hytaleFoundations) {
            super("set", "Set home at your current location.");
            this.requirePermission(Permissions.HOME.getPermission());
            this.hytaleFoundations = hytaleFoundations;
            this.homeName = withRequiredArg("name", "Identifier for this home.", ArgTypes.STRING);
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store,
                               @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String name = commandContext.get(this.homeName);
            PlayerProfile profile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(playerRef.getUsername());
            if (profile == null) {
                playerRef.sendMessage(LM.getPlayerMessage(playerRef.getUsername(),
                        LangKey.HOME_SET_FAILURE, name));
                return;
            }
            TeleportManager tm = hytaleFoundations.getUtilityManager().getTeleportManager();
            Vector3d position = tm.getPosition(playerRef);
            if (position == null) {
                playerRef.sendMessage(LM.getPlayerMessage(playerRef.getUsername(),
                        LangKey.HOME_SET_FAILURE, name));
                return;
            }
            Vector3f rotation = playerRef.getTransform() != null ? new Vector3f(playerRef.getTransform().getRotation()) : Vector3f.ZERO;
            Location location = new Location(world.getName(), position, rotation);
            profile.addHome(name, location);
            playerRef.sendMessage(LM.getPlayerMessage(playerRef.getUsername(),
                    LangKey.HOME_SET_SUCCESS, name));
        }
    }

    public static class HomeDeleteCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> homeName;
        public HomeDeleteCommand(HytaleFoundations hytaleFoundations) {
            super("delete", "Delete a specific home that you've previously set.");
            this.requirePermission(Permissions.HOME.getPermission());
            this.hytaleFoundations = hytaleFoundations;
            this.homeName = withRequiredArg("name", "Identifier of the home to remove.", ArgTypes.STRING);
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store,
                               @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String name = commandContext.get(this.homeName);
            PlayerProfile profile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(playerRef.getUsername());
            if (profile == null || !profile.getHomes().containsKey(name)) {
                playerRef.sendMessage(LM.getPlayerMessage(playerRef.getUsername(),
                        LangKey.HOME_DELETE_FAILURE, name));
                return;
            }
            profile.removeHome(name);
            playerRef.sendMessage(LM.getPlayerMessage(playerRef.getUsername(),
                    LangKey.HOME_DELETE_SUCCESS, name));
        }
    }

    public static class HomeListCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        public HomeListCommand(HytaleFoundations hytaleFoundations) {
            super("list", "List all of your saved homes.");
            this.requirePermission(Permissions.HOME.getPermission());
            this.hytaleFoundations = hytaleFoundations;
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store,
                               @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            PlayerProfile profile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(playerRef.getUsername());
            if (profile == null) return;
            String username = playerRef.getUsername();
            playerRef.sendMessage(LM.getPlayerMessage(username, LangKey.LIST_CONTEXT, "home(s)"));
            for (Map.Entry<String, Location> entry : profile.getHomes().entrySet()) {
                Vector3d position = entry.getValue().getPosition();
                String coords = "&r&7&l[" + (long) position.getX() + ", " + (long) position.getY() + ", " + (long) position.getZ() + "]";
                playerRef.sendMessage(LM.getPlayerMessage(username, LangKey.LIST_ITEM,
                        "&e&l" + entry.getKey() + " " + coords));
            }
        }
    }

    public static class HomeTeleportCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> homeName;
        public HomeTeleportCommand(HytaleFoundations hytaleFoundations) {
            super("tp", "Teleport to a saved home.");
            this.requirePermission(Permissions.HOME.getPermission());
            this.hytaleFoundations = hytaleFoundations;
            this.homeName = withRequiredArg("name", "Identifier of the home to teleport to.", ArgTypes.STRING);
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store,
                               @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String name = commandContext.get(this.homeName);
            PlayerProfile profile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(playerRef.getUsername());
            if (profile == null) {
                playerRef.sendMessage(LM.getPlayerMessage(playerRef.getUsername(),
                        LangKey.HOME_TELEPORT_FAILURE, name));
                return;
            }
            Location location = profile.getHomes().get(name);
            if (location == null) {
                playerRef.sendMessage(LM.getPlayerMessage(playerRef.getUsername(),
                        LangKey.HOME_TELEPORT_FAILURE, name));
                return;
            }
            TeleportManager tm = hytaleFoundations.getUtilityManager().getTeleportManager();
            if (tm.teleport(playerRef, location.getPosition(), location.getRotation())) {
                playerRef.sendMessage(LM.getPlayerMessage(playerRef.getUsername(),
                        LangKey.HOME_TELEPORT_SUCCESS, name));
            } else {
                playerRef.sendMessage(LM.getPlayerMessage(playerRef.getUsername(),
                        LangKey.HOME_TELEPORT_FAILURE, name));
            }
        }
    }
}
