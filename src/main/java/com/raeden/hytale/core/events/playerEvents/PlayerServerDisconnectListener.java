package com.raeden.hytale.core.events.playerEvents;

import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.data.PlayerDataManager;

public class PlayerServerDisconnectListener {
    public static void onPlayerDisconnect(PlayerDisconnectEvent e, HytaleFoundations hytaleFoundations) {
        PlayerRef playerRef = e.getPlayerRef();
        String username = playerRef.getUsername();

        PlayerDataManager dataManager = hytaleFoundations.getPlayerDataManager();
        dataManager.playerLogout(playerRef);
    }
}
