package com.raeden.hytale.core.events.playerEvents;

import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.raeden.hytale.HytaleEssentials;
import com.raeden.hytale.core.data.PlayerDataManager;
import com.raeden.hytale.core.data.PlayerData;

public class PlayerServerDisconnectListener {
    public static void onPlayerDisconnect(PlayerDisconnectEvent e, HytaleEssentials hytaleEssentials) {
        PlayerRef playerRef = e.getPlayerRef();
        String username = playerRef.getUsername();

        PlayerDataManager dataManager = hytaleEssentials.getPlayerDataManager();
        PlayerData data = dataManager.getPlayerMetaData(username);

        if(data == null) return;

        disconnectActions(playerRef, dataManager, data);
        dataManager.savePlayerMetaData(username, data);
        dataManager.removeActivePlayer(username);
    }

    private static void disconnectActions(PlayerRef playerRef, PlayerDataManager playerDataManager, PlayerData playerData) {
        savePlayTime(playerDataManager, playerData);
    }

    private static void savePlayTime(PlayerDataManager dataManager, PlayerData playerData) {
        long timeNow = System.currentTimeMillis();
        long sessionDuration = timeNow -  playerData.getSessionStart();

        if(sessionDuration > 0) {
            playerData.setPlayTimeMillis(playerData.getPlayTimeMillis() + sessionDuration);
        }

        playerData.setSessionStart(System.currentTimeMillis());
    }
}
