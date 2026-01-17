package com.raeden.hytale.core.data;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleEssentials;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

import static com.raeden.hytale.HytaleEssentials.myLogger;

public class PlayerDataManager {
    private final HytaleEssentials hytaleEssentials;
    private final Path playerDataPath;

    private LinkedHashMap<String, PlayerMetaData> activePlayers;

    public PlayerDataManager(HytaleEssentials hytaleEssentials) {
        this.hytaleEssentials = hytaleEssentials;
        playerDataPath = hytaleEssentials.getDataDirectory().resolve("data").resolve("players");
        verifyPath();
    }

    private void verifyPath() {
        try {
            if(!Files.exists(playerDataPath)) {
                Files.createDirectories(playerDataPath);
                myLogger.atInfo().log("Created data folder: " + playerDataPath);
            }
        } catch (IOException e) {
            myLogger.atWarning().log("Failed to create data folder: " + playerDataPath);
        }
    }

    public PlayerMetaData getPlayerMetaData(String username) {
        return activePlayers.get(username);
    }

    public void addNewActivePlayer(String username, PlayerMetaData metaData) {
        activePlayers.put(username, metaData);
    }

    public LinkedHashMap<String, PlayerMetaData> getActivePlayers() {
        return activePlayers;
    }

    public void loadPlayerMetaData(String username) {

    }

    public void createPlayerMetaData(PlayerRef playerRef) {
        PlayerMetaData data = createDefaultPlayerData();

        Ref<EntityStore> ref = playerRef.getReference();
        Store<EntityStore> store = Objects.requireNonNull(ref).getStore();
        UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());

        data.setUUID(uuidComponent);
        data.addUsername(playerRef.getUsername());
    }

    private PlayerMetaData createDefaultPlayerData() {
        PlayerMetaData data = new PlayerMetaData();

        data.setNickname("");
        data.setFirstJoined(0);
        data.setLastJoined(0);
        data.setPlayTimeMillis(0);
        data.setTotalDeaths(0);
        data.setPlayerKills(0);
        data.setMobKills(0);
        data.setBlocksBroken(0);
        data.setBlocksPlaced(0);
        data.setDistanceWalked(0);
        data.setItemsCrafted(0);
        data.setItemsBroken(0);
        data.setGodModeEnabled(false);
        data.setVanished(false);
        data.setFlying(false);
        data.setMuted(false);

        return data;
    }

    public Path getPlayerDataPath() {return playerDataPath;}
}
