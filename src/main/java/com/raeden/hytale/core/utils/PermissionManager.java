package com.raeden.hytale.core.utils;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.lang.LangKey;
import com.raeden.hytale.utils.FileManager;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.raeden.hytale.HytaleFoundations.*;
import static com.raeden.hytale.core.config.ConfigManager.*;
import static com.raeden.hytale.utils.FileManager.*;

public class PermissionManager {
    private final HytaleFoundations hytaleFoundations;

    private final String permissionFileName = PERMISSION_FILENAME;
    private final Path permissionFilePath;

    private final Map<String, String> permissionMap;
    private final Map<String, Set<String>> permissionGroupMap;
    private PermissionFile permissionFile;

    public PermissionManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        permissionFilePath = hytaleFoundations.getDataDirectory().resolve(permissionFileName);
        permissionMap = new ConcurrentHashMap<>();
        permissionGroupMap = new ConcurrentHashMap<>();
        initializePermissionManager();
    }

    // Initialization and Loading
    private void initializePermissionManager() {
        if(Files.exists(permissionFilePath)) {
            loadPermissions();
        } else {
            saveDefaultPermissionFile();
        }
    }

    public void savePermissionFile() {
        permissionFile = new PermissionFile();
        permissionFile.setPermissionGroups(permissionGroupMap);
        permissionFile.setPermissions(permissionMap);
        saveJsonFile(permissionFileName, permissionFilePath, permissionFile, false);
    }

    private void saveDefaultPermissionFile() {
        permissionMap.putAll(getDefaultPermissions());
        permissionGroupMap.putAll(getDefaultPermissionGroups());
        permissionFile = new PermissionFile();
        permissionFile.setPermissions(getDefaultPermissions());
        permissionFile.setPermissionGroups(getDefaultPermissionGroups());
        saveJsonFile(permissionFileName, permissionFilePath, permissionFile, true);
    }

    public void loadPermissions() {
        Type type = new TypeToken<PermissionFile>(){}.getType();
        PermissionFile loadedPermissionFile = loadJsonFile(permissionFileName, permissionFilePath, type, true);

        if(loadedPermissionFile != null && loadedPermissionFile.getPermissions() != null) {
            int newPermissions = 0;
            for(Map.Entry<String, String> permission : loadedPermissionFile.getPermissions().entrySet()) {
                if(!permissionMap.containsKey(permission.getKey())) {
                    newPermissions++;
                }
                permissionMap.put(permission.getKey(), permission.getValue());
            }

            int newPermissionGroups = 0;
            int updatedPermissionGroups = 0;

            for(Map.Entry<String, Set<String>> permissionGroup : loadedPermissionFile.getPermissionGroups().entrySet()) {
                String groupName = permissionGroup.getKey();
                Set<String> loadedPerms = permissionGroup.getValue();

                if(!permissionGroupMap.containsKey(groupName)) {
                    newPermissionGroups++;
                    permissionGroupMap.put(groupName, new HashSet<>(loadedPerms));
                } else {
                    Set<String> existingPerms = permissionGroupMap.get(groupName);
                    Set<String> mergedPerms = new HashSet<>(existingPerms);
                    int oldSize = mergedPerms.size();
                    mergedPerms.addAll(loadedPerms);
                    if(mergedPerms.size() != oldSize) {
                        updatedPermissionGroups++;
                    }
                    permissionGroupMap.put(groupName, mergedPerms);
                }
            }

            if (newPermissions > 0)  myLogger.atInfo().log(LM.getConsoleMessage(LangKey.LOAD_SUCCESS, newPermissions + " permission(s)").getAnsiMessage());
            if (newPermissionGroups > 0)  myLogger.atInfo().log(LM.getConsoleMessage(LangKey.LOAD_SUCCESS, newPermissionGroups + " permission group(s)").getAnsiMessage());
            if (updatedPermissionGroups > 0) myLogger.atInfo().log(LM.getConsoleMessage(LangKey.LOAD_SUCCESS, updatedPermissionGroups + " permission group(s) updated").getAnsiMessage());

            permissionFile = loadedPermissionFile;
        } else {
            saveDefaultPermissionFile();
        }
    }

    private Map<String, Set<String>> getDefaultPermissionGroups() {
        Map<String, Set<String>> map = new ConcurrentHashMap<>();
        for(PermissionGroups key : PermissionGroups.values()) {
            map.put(key.name(), key.getPermissionSet());
        }
        return map;
    }

    private Map<String, String> getDefaultPermissions() {
        Map<String, String> map = new ConcurrentHashMap<>();
        for(Permissions key : Permissions.values()) {
            map.put(key.name(), key.getPermission());
        }
        return map;
    }

    public Map<String, String> getPermissionsMap() {return permissionMap;}
    public Map<String, Set<String>> getPermissionGroupMap() {return permissionGroupMap;}

    // Permission Checkers
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
            myLogger.atWarning().log(LM.getConsoleMessage(LangKey.CHECK_FAILURE,"permission [" + permission + "]").getAnsiMessage());
            return false;
        }
    }

    public boolean hasPermissionGroup(UUID playerID, String groupName) {
        try {
            PermissionsModule permissionsModule = PermissionsModule.get();
            Set<String> playerGroups = permissionsModule.getGroupsForUser(playerID);
            return playerGroups.contains(groupName);
        } catch (Exception e) {
            FileManager.logError("PermissionManager-HasPermission", e);
            myLogger.atWarning().log(LM.getConsoleMessage(LangKey.CHECK_FAILURE,"permission group [" + groupName + "]").getAnsiMessage());
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

    public static class PermissionFile {
        @SerializedName("VERSION")
        private final String version = PERMISSION_VERSION;
        @SerializedName("PERMISSION_LIST")
        private Map<String, String> permissions;
        @SerializedName("PERMISSION_GROUPS")
        private Map<String, Set<String>> permissionGroups;

        public String getVersion() {return version;}

        public Map<String, String> getPermissions() {return permissions;}
        public void setPermissions(Map<String, String> permissions) {this.permissions = permissions;}
        public void addPermission(String permissionName, String permissionNode) {this.permissions.put(permissionName, permissionNode);}

        public Map<String, Set<String>> getPermissionGroups() {return permissionGroups;}
        public void setPermissionGroups(Map<String, Set<String>> permissionGroups) {this.permissionGroups = permissionGroups;}
        public void addPermissionGroup(String permissionGroupName, Set<String> permissionSet) {this.permissionGroups.put(permissionGroupName, permissionSet);}
    }
}
