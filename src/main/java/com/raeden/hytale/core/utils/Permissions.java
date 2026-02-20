package com.raeden.hytale.core.utils;

import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.raeden.hytale.lang.LangKey;

import java.util.UUID;

import static com.raeden.hytale.HytaleFoundations.langManager;
import static com.raeden.hytale.HytaleFoundations.myLogger;
import static com.raeden.hytale.utils.FileManager.logExceptionError;

public class Permissions {
    public static boolean isPlayerAdmin(PlayerRef playerRef) {
        return hasPermission(playerRef, "hytale.command.*");
    }
    public static boolean isPlayerAdmin(CommandSender sender) {
        return hasPermission(sender, "hytale.command.*");
    }

    public static boolean hasPermission(PlayerRef playerRef, String permission) {
        if(playerRef == null) return false;
        return hasPermission(playerRef.getUuid(), permission);
    }

    public static boolean hasPermission(CommandSender sender, String permission) {
        if(sender == null) return false;
        return hasPermission(sender.getUuid(), permission);
    }

    public static boolean hasPermission(UUID playerID, String permission) {
        try {
            PermissionsModule permissionsModule = PermissionsModule.get();
            return permissionsModule.hasPermission(playerID, permission);
        } catch (Exception e) {
            logExceptionError("Permissions-HasPermission", e);
            myLogger.atWarning().log(langManager.getMessage(LangKey.CHECK_FAILURE,true, "permission [" + permission + "]").getAnsiMessage());
            return false;
        }
    }

    public enum HFPermissions {
        ACCESS("access"),
        ADMIN("admin"),
        NICK("nick"),
        AFFIX("affix"),
        VANISH("admin.vanish"),
        PLAYER_DATABASE("admin.playerdb"),
        ANALYTICS("analytics"),
        BLOCK_PLAYER("block.player"),
        MUTE_PLAYER("mute.player"),
        SILENCE_PLAYER("silence.player"),
        CLEAR_CHAT("chat.clear"),
        CHAT_COLORS("chat.colors"),
        PLAYTIME("utils.playtime"),
        ANNOUNCE("utils.announce"),
        TITLE("utils.send.title");


        private final String permission;
        HFPermissions(String permission) {
            String PREFIX = "hytalefoundations.";
            this.permission = PREFIX + permission;
        }
        public String getPermission() {return permission;}
    }
}
