package com.raeden.hytale.core.events.playerEvents;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.raeden.hytale.HytaleEssentials;
import com.raeden.hytale.core.data.PlayerDataManager;
import com.raeden.hytale.core.data.PlayerMetaData;

import static com.raeden.hytale.HytaleEssentials.myLogger;

public class playerServerDisconnectEvent {
    public static void onPlayerDisconnect(PlayerDisconnectEvent e, HytaleEssentials hytaleEssentials) {
        PlayerRef playerRef = e.getPlayerRef();
        String username = playerRef.getUsername();

        PlayerDataManager dataManager = hytaleEssentials.getPlayerDataManager();
        PlayerMetaData data = dataManager.getPlayerMetaData(username);

        if(data == null) return;

        disconnectActions(playerRef, dataManager, data);
        dataManager.savePlayerMetaData(username, data);
        dataManager.removeActivePlayer(username);
    }

    private static void disconnectActions(PlayerRef playerRef, PlayerDataManager playerDataManager, PlayerMetaData playerMetaData) {
        savePlayTime(playerDataManager, playerMetaData);
    }

    private static void savePlayTime(PlayerDataManager dataManager, PlayerMetaData playerMetaData) {
        long timeNow = System.currentTimeMillis();
        long sessionDuration = timeNow -  playerMetaData.getSessionStart();

        if(sessionDuration > 0) {
            playerMetaData.setPlayTimeMillis(playerMetaData.getPlayTimeMillis() + sessionDuration);
        }

        playerMetaData.setSessionStart(System.currentTimeMillis());
    }
}
