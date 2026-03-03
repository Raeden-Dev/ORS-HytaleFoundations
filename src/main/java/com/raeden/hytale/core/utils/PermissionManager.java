package com.raeden.hytale.core.utils;

import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.lang.LangKey;
import com.raeden.hytale.utils.FileManager;

import java.util.Set;
import java.util.UUID;

import static com.raeden.hytale.HytaleFoundations.*;
import static com.raeden.hytale.utils.FileManager.logError;

public class PermissionManager {
    private final HytaleFoundations hytaleFoundations;

    public PermissionManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
    }

    public boolean isPlayerAdmin(PlayerRef playerRef) {
        return hasPermission(playerRef, "hytale.command.*");
    }
    public boolean isPlayerAdmin(CommandSender sender) {
        return hasPermission(sender, "hytale.command.*");
    }

    public boolean hasPermission(PlayerRef playerRef, String permission) {
        if(playerRef == null) return false;
        return hasPermission(playerRef.getUuid(), permission);
    }

    public boolean hasPermission(CommandSender sender, String permission) {
        if(sender == null) return false;
        return hasPermission(sender.getUuid(), permission);
    }

    public boolean hasPermission(UUID playerID, String permission) {
        try {
            PermissionsModule permissionsModule = PermissionsModule.get();
            return permissionsModule.hasPermission(playerID, permission);
        } catch (Exception e) {
            FileManager.logError("PermissionManager-HasPermission", e);
            myLogger.atWarning().log(LM.getMessage(LangKey.CHECK_FAILURE,true, "permission [" + permission + "]").getAnsiMessage());
            return false;
        }
    }

    public void addPermissionForUser(UUID uuid, String permission) {
        PermissionsModule permissionsModule = PermissionsModule.get();
        permissionsModule.addUserPermission(uuid, Set.of(permission));
    }
    public void addMultiplePermissionForUser(UUID uuid, Set<String> permissions) {
        PermissionsModule permissionsModule = PermissionsModule.get();
        permissionsModule.addUserPermission(uuid, permissions);
    }
    public void removePermissionFromUser(UUID uuid, String permission) {
        PermissionsModule permissionsModule = PermissionsModule.get();
        permissionsModule.removeUserPermission(uuid, Set.of(permission));
    }

    public void createPermissionGroup(String name, Set<String> permissions) {
        PermissionsModule permissionsModule = PermissionsModule.get();
        permissionsModule.addGroupPermission(name, permissions);
    }
    public void addPermissionGroupForUser(UUID uuid, String groupName) {
        PermissionsModule permissionsModule = PermissionsModule.get();
        permissionsModule.addUserToGroup(uuid, groupName);
    }
    public void removePermissionGroupForUser(UUID uuid, String groupName) {
        PermissionsModule permissionsModule = PermissionsModule.get();
        permissionsModule.removeUserFromGroup(uuid, groupName);
    }
}
