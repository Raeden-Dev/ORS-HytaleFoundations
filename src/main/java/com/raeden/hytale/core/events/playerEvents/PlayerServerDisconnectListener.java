package com.raeden.hytale.core.events.playerEvents;

import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.data.PlayerDataManager;
import com.raeden.hytale.core.data.PlayerData;

public class PlayerServerDisconnectListener {
    public static void onPlayerDisconnect(PlayerDisconnectEvent e, HytaleFoundations hytaleFoundations) {
        PlayerRef playerRef = e.getPlayerRef();
        String username = playerRef.getUsername();

        PlayerDataManager dataManager = hytaleFoundations.getPlayerDataManager();
        PlayerData data = dataManager.getPlayerData(username);

        if(data == null) return;

        disconnectActions(playerRef, dataManager, data);
        dataManager.savePlayerData(username, data);
        dataManager.removeActivePlayer(username);
    }

    private static void disconnectActions(PlayerRef playerRef, PlayerDataManager playerDataManager, PlayerData playerData) {
        playerDataManager.savePlayTime(playerData);
    }
}
