package com.raeden.hytale.core.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.permissions.provider.PermissionProvider;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.utils.PermissionManager;
import com.raeden.hytale.core.utils.Permissions;
import com.raeden.hytale.lang.LangKey;

import javax.annotation.Nonnull;
import java.util.*;

import static com.raeden.hytale.HytaleFoundations.LM;
import static com.raeden.hytale.utils.GeneralUtils.findPlayerByName;

public class PermissionCommand extends AbstractCommandCollection {
    public PermissionCommand(HytaleFoundations hytaleFoundations) {
        super("permission", "Argument for all permission related commands.");
        this.requirePermission(Permissions.ADMIN.getPermission());
        this.addAliases("perm");
        this.addSubCommand(new PermissionListCommand(hytaleFoundations));
        this.addSubCommand(new PermissionGroupListCommand(hytaleFoundations));
        this.addSubCommand(new PermissionInspectCommand(hytaleFoundations));
        this.addSubCommand(new PermissionAddCommand(hytaleFoundations));
        this.addSubCommand(new PermissionRemoveCommand(hytaleFoundations));
        this.addSubCommand(new PermissionRemoveAllCommand(hytaleFoundations));
        this.addSubCommand(new PermissionGroupAddCommand(hytaleFoundations));
        this.addSubCommand(new PermissionGroupRemoveCommand(hytaleFoundations));
    }

    public static class PermissionListCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        public PermissionListCommand(HytaleFoundations hytaleFoundations) {
            super("list", "Show list of all available hytale foundations permissions.");
            this.hytaleFoundations = hytaleFoundations;
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            PermissionManager permissionManager = hytaleFoundations.getPermissionManager();
            String playerUsername = commandContext.sender().getDisplayName();
            if(permissionManager == null) return;
            if(permissionManager.getPermissionsMap() == null || permissionManager.getPermissionsMap().isEmpty()) {
                commandContext.sender().sendMessage(LM.getPlayerMessage(playerUsername, LangKey.NOTHING_FOUND, "permission", "Empty map"));
                return;
            }
            commandContext.sender().sendMessage(LM.getPlayerMessage(playerUsername, LangKey.LIST_CONTEXT, "permission(s)"));
            for(Map.Entry<String, String> entry : permissionManager.getPermissionsMap().entrySet()) {
                commandContext.sender().sendMessage(LM.getPlayerMessage(playerUsername, LangKey.LIST_ITEM,
                        "&r&e" + entry.getKey() + "  &r&f&l[Node: &r&7" + entry.getValue() + "&r&f&l]"));
            }
        }
    }

    public static class PermissionGroupListCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        public PermissionGroupListCommand(HytaleFoundations hytaleFoundations) {
            super("grouplist", "Show list of all available hytale foundations permission groups.");
            this.addAliases("glist");
            this.hytaleFoundations = hytaleFoundations;
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            PermissionManager permissionManager = hytaleFoundations.getPermissionManager();
            String playerUsername = commandContext.sender().getDisplayName();
            if(permissionManager == null) return;
            if(permissionManager.getPermissionGroupMap() == null || permissionManager.getPermissionGroupMap().isEmpty()) {
                commandContext.sender().sendMessage(LM.getPlayerMessage(playerUsername, LangKey.NOTHING_FOUND, "permission group", "Empty map"));
                return;
            }
            commandContext.sender().sendMessage(LM.getPlayerMessage(playerUsername, LangKey.LIST_CONTEXT, "permission group(s)"));
            for(Map.Entry<String, Set<String>> entry : permissionManager.getPermissionGroupMap().entrySet()) {
                commandContext.sender().sendMessage(LM.getPlayerMessage(playerUsername, LangKey.LIST_ITEM,
                        "&fGroup &r&7&l[&r&b&l" + entry.getKey() + "&r&7&l] &r&fpermission list:"));

                List<String> permSet = entry.getValue().stream().toList();
                for(int i = 1; i <= permSet.size(); i++) {
                    commandContext.sender().sendMessage(LM.getPlayerMessage(playerUsername, LangKey.LIST_NUMBERED_ITEM, String.valueOf(i), permSet.get(i - 1)));
                }
            }
        }
    }

    public static class PermissionInspectCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> targetPlayer;
        public PermissionInspectCommand(HytaleFoundations hytaleFoundations) {
            super("inspect", "Show list of all permissions & permission groups the player has.");
            this.addAliases("show", "ins");
            this.hytaleFoundations = hytaleFoundations;
            this.targetPlayer = withRequiredArg("Target", "Player to add permission node to.", ArgTypes.STRING);
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String targetUsername = commandContext.get(this.targetPlayer);
            String senderUsername = commandContext.sender().getDisplayName();
            UUID targetUUID = hytaleFoundations.getPlayerDataManager().getPlayerUUID(targetUsername);
            if(!hytaleFoundations.getPlayerDataManager().doesPlayerExist(targetUsername)) {
                commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PLAYER_NOT_FOUND_MSG, targetUsername));
                return;
            }
            PermissionsModule permissionsModule = PermissionsModule.get();
            Set<String> perms = new HashSet<>();
            Set<String> permGroups = new HashSet<>();
            for(PermissionProvider permissionProvider : permissionsModule.getProviders()) {
                perms.addAll(permissionProvider.getUserPermissions(targetUUID));
                permGroups.addAll(permissionProvider.getGroupsForUser(targetUUID));
            }
            commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PLAYER_PERM_LIST, targetUsername));
            if(!perms.isEmpty()) {
                for(String perm : perms) {
                    commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.LIST_ITEM, "&b&l" + perm));
                }
            } else {
                commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.NOTHING_FOUND, "permission", "nothing added"));
            }
            commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PLAYER_PERM_GROUP_LIST, targetUsername));
            if(!permGroups.isEmpty()) {
                for(String group : permGroups) {
                    commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.LIST_ITEM, "&b&l" + group));
                }
            } else {
                commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.NOTHING_FOUND, "permission group", "nothing added"));
            }
        }
    }

    public static class PermissionAddCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> targetPlayer;
        private final RequiredArg<List<String>> permissionNodes;
        private final FlagArg isThirdParty;
        public PermissionAddCommand(HytaleFoundations hytaleFoundations) {
            super("add", "Add a permission node for target");
            this.hytaleFoundations = hytaleFoundations;
            this.targetPlayer = withRequiredArg("Target", "Player to add permission node to.", ArgTypes.STRING);
            this.permissionNodes = withListRequiredArg("Permission Name(s)", "Permission node(s) to add to Player.", ArgTypes.STRING);
            this.isThirdParty = withFlagArg("thirdParty", "If the permission node isn't present in hytale foundations.");
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String senderUsername = commandContext.sender().getDisplayName();
            String targetUsername = commandContext.get(this.targetPlayer);
            List<String> nodes = commandContext.get(this.permissionNodes);
            boolean isThirdParty = Boolean.TRUE.equals(commandContext.get(this.isThirdParty));

            if (!hytaleFoundations.getPlayerDataManager().doesPlayerExist(targetUsername)) {
                commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PLAYER_NOT_FOUND_MSG, targetUsername));
                return;
            }

            UUID targetUUID = hytaleFoundations.getPlayerDataManager().getPlayerUUID(targetUsername);
            List<String> successfullyAdded = new ArrayList<>();

            for (String node : nodes) {
                if (!isThirdParty && !hytaleFoundations.getPermissionManager().getPermissionsMap().containsKey(node)) {
                    commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PERMISSION_NOT_FOUND, node));
                    continue;
                }

                String actualNode = isThirdParty ? node : hytaleFoundations.getPermissionManager().getPermissionsMap().get(node);
                hytaleFoundations.getPermissionManager().addPermissionForUser(targetUUID, actualNode);
                successfullyAdded.add(actualNode);
            }

            if (!successfullyAdded.isEmpty()) {
                String joinedNodes = String.join(", ", successfullyAdded);
                commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PERMISSION_ADD, joinedNodes, targetUsername));
            }
        }
    }

    public static class PermissionRemoveCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> targetPlayer;
        private final RequiredArg<List<String>> permissionNodes;

        public PermissionRemoveCommand(HytaleFoundations hytaleFoundations) {
            super("remove", "Remove a permission node for target");
            this.hytaleFoundations = hytaleFoundations;
            this.targetPlayer = withRequiredArg("Target", "Player to remove permission node from.", ArgTypes.STRING);
            this.permissionNodes = withListRequiredArg("Permission Node(s)", "Permission node(s) to remove from Player.", ArgTypes.STRING);
        }

        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String senderUsername = commandContext.sender().getDisplayName();
            String targetUsername = commandContext.get(this.targetPlayer);
            List<String> nodes = commandContext.get(this.permissionNodes);
            if (!hytaleFoundations.getPlayerDataManager().doesPlayerExist(targetUsername)) {
                commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PLAYER_NOT_FOUND_MSG, targetUsername));
                return;
            }
            UUID targetUUID = hytaleFoundations.getPlayerDataManager().getPlayerUUID(targetUsername);
            PermissionManager permManager = hytaleFoundations.getPermissionManager();
            List<String> successfullyRemoved = new ArrayList<>();
            for (String node : nodes) {
                boolean removed = false;
                if (permManager.getPermissionsMap().containsKey(node)) {
                    String mappedNode = permManager.getPermissionsMap().get(node);
                    if (permManager.hasPermission(targetUUID, mappedNode)) {
                        permManager.removePermissionFromUser(targetUUID, mappedNode);
                        successfullyRemoved.add(mappedNode);
                        removed = true;
                    }
                }
                if (!removed && permManager.hasPermission(targetUUID, node)) {
                    permManager.removePermissionFromUser(targetUUID, node);
                    successfullyRemoved.add(node);
                    removed = true;
                }
                if (!removed) commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PERMISSION_NOT_FOUND, node));
            }
            if (!successfullyRemoved.isEmpty()) commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername,
                    LangKey.PERMISSION_REMOVE, String.join(", ", successfullyRemoved), targetUsername));
        }
    }

    public static class PermissionRemoveAllCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> targetPlayer;

        public PermissionRemoveAllCommand(HytaleFoundations hytaleFoundations) {
            super("removeall", "Remove all permission nodes from target");
            this.addAliases("clear", "rmvall");
            this.hytaleFoundations = hytaleFoundations;
            this.targetPlayer = withRequiredArg("Target", "Player to remove all permissions from.", ArgTypes.STRING);
        }

        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String senderUsername = commandContext.sender().getDisplayName();
            String targetUsername = commandContext.get(this.targetPlayer);

            if (!hytaleFoundations.getPlayerDataManager().doesPlayerExist(targetUsername)) {
                commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PLAYER_NOT_FOUND_MSG, targetUsername));
                return;
            }

            UUID targetUUID = hytaleFoundations.getPlayerDataManager().getPlayerUUID(targetUsername);
            PermissionManager permManager = hytaleFoundations.getPermissionManager();
            PermissionsModule permissionsModule = PermissionsModule.get();

            Set<String> perms = new HashSet<>();
            for (PermissionProvider permissionProvider : permissionsModule.getProviders()) {
                perms.addAll(permissionProvider.getUserPermissions(targetUUID));
            }

            if (perms.isEmpty()) {
                commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.NOTHING_FOUND, "permission", "nothing to remove"));
                return;
            }

            for (String perm : perms) {
                permManager.removePermissionFromUser(targetUUID, perm);
            }

            commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PERMISSION_REMOVE, "all permissions", targetUsername));
        }
    }

    public static class PermissionGroupAddCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> targetPlayer;
        private final RequiredArg<List<String>> permissionGroups;
        private final FlagArg isThirdParty;

        public PermissionGroupAddCommand(HytaleFoundations hytaleFoundations) {
            super("groupadd", "Add a permission group for target");
            this.addAliases("gadd");
            this.hytaleFoundations = hytaleFoundations;
            this.targetPlayer = withRequiredArg("Target", "Player to add permission group to.", ArgTypes.STRING);
            this.permissionGroups = withListRequiredArg("Permission Group(s)", "Permission group(s) to add to Player.", ArgTypes.STRING);
            this.isThirdParty = withFlagArg("thirdParty", "If the permission group isn't present in hytale foundations.");
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String senderUsername = commandContext.sender().getDisplayName();
            String targetUsername = commandContext.get(this.targetPlayer);
            List<String> groups = commandContext.get(this.permissionGroups);
            boolean isThirdParty = Boolean.TRUE.equals(commandContext.get(this.isThirdParty));
            if (!hytaleFoundations.getPlayerDataManager().doesPlayerExist(targetUsername)) {
                commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PLAYER_NOT_FOUND_MSG, targetUsername));
                return;
            }
            List<String> successfullyAdded = new ArrayList<>();
            for (String group : groups) {
                if (!hytaleFoundations.getPermissionManager().getPermissionGroupMap().containsKey(group) && !isThirdParty) {
                    commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PERMISSION_GROUP_NF, group));
                    continue;
                }
                successfullyAdded.add(group);
            }
            if (!successfullyAdded.isEmpty()) {
                commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PERMISSION_GROUP_ADD, String.join(", ", successfullyAdded), targetUsername));
            }
        }
    }

    public static class PermissionGroupRemoveCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> targetPlayer;
        private final RequiredArg<List<String>> permissionGroups;

        public PermissionGroupRemoveCommand(HytaleFoundations hytaleFoundations) {
            super("groupremove", "Remove a permission group for target");
            this.addAliases("gremove", "grmv");
            this.hytaleFoundations = hytaleFoundations;
            this.targetPlayer = withRequiredArg("Target", "Player to remove permission group from.", ArgTypes.STRING);
            this.permissionGroups = withListRequiredArg("Permission Group(s)", "Permission group(s) to remove from Player.", ArgTypes.STRING);
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String senderUsername = commandContext.sender().getDisplayName();
            String targetUsername = commandContext.get(this.targetPlayer);
            List<String> groups = commandContext.get(this.permissionGroups);
            if (!hytaleFoundations.getPlayerDataManager().doesPlayerExist(targetUsername)) {
                commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PLAYER_NOT_FOUND_MSG, targetUsername));
                return;
            }
            UUID targetUUID = hytaleFoundations.getPlayerDataManager().getPlayerUUID(targetUsername);
            PermissionManager permManager = hytaleFoundations.getPermissionManager();
            List<String> successfullyRemoved = new ArrayList<>();
            for (String group : groups) {
                if (permManager.hasPermissionGroup(targetUUID, group)) {
                    permManager.removePermissionGroupForUser(targetUUID, group);
                    successfullyRemoved.add(group);
                } else {
                    commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PERMISSION_GROUP_NF, group));
                }
            }
            if (!successfullyRemoved.isEmpty()) {
                commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PERMISSION_GROUP_REMOVE, String.join(", ", successfullyRemoved), targetUsername));
            }
        }
    }
}