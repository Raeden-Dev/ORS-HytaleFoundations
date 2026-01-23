package com.raeden.hytale.core.utils;

import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;

public class Permissions {
    public static boolean isPlayerAdmin(CommandSender sender) {
        return sender.hasPermission("hytale.command.*");
    }
}
