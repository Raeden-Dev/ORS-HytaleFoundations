package com.raeden.hytale.utils;

import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;

import static com.raeden.hytale.HytaleEssentials.myLogger;

public class generalUtils {
    private static PlayerRef findPlayerByName(String username) {
        try {
            Universe universe = Universe.get();
            if(universe == null) {
                return null;
            } else {
                PlayerRef player = universe.getPlayerByUsername(username, NameMatching.EXACT);
                if(player == null) {
                    myLogger.atWarning().log("Failed to find player with username: " + username);
                }

                return player;
            }
        } catch (Exception e) {
            myLogger.atWarning().log("Failed to find player with username: " + username + " - " + e.getMessage());
            return null;
        }
    }

    public static boolean isPlayerOnline(String username) {
        try {
            PlayerRef playerRef = findPlayerByName(username);
        }
    }
}
