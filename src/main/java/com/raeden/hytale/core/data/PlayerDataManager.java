package com.raeden.hytale.core.data;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleEssentials;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Objects;

import static com.raeden.hytale.HytaleEssentials.GSON;
import static com.raeden.hytale.HytaleEssentials.myLogger;
import static com.raeden.hytale.utils.generalUtils.findPlayerByName;

public class PlayerDataManager {
    private final HytaleEssentials hytaleEssentials;
    private final Path playerDataPath;

    private final LinkedHashMap<String, PlayerMetaData> activePlayers;

    public PlayerDataManager(HytaleEssentials hytaleEssentials) {
        this.hytaleEssentials = hytaleEssentials;
        playerDataPath = hytaleEssentials.getDataDirectory().resolve("data").resolve("players");
        activePlayers = new LinkedHashMap<>();
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

    public void removeActivePlayer(String username) {
        activePlayers.remove(username);
    }

    public LinkedHashMap<String, PlayerMetaData> getActivePlayers() {
        return activePlayers;
    }

    public void savePlayerMetaData(String username, PlayerMetaData metaData) {
        Path savePath = playerDataPath.resolve(username + ".json");
        String toJson = GSON.toJson(metaData);
        try {
            Files.writeString(savePath, toJson, StandardCharsets.UTF_8);
        } catch (IOException e) {
            myLogger.atSevere().log("Failed to save player metadata for player: " + username);
        }
    }

    public void loadPlayerMetaData(String username) {
        Path dataFile = playerDataPath.resolve(username + ".json");
        if(Files.exists(dataFile)) {
            try {
                String readPlayerData = Files.readString(dataFile, StandardCharsets.UTF_8);
                PlayerMetaData metaData = GSON.fromJson(readPlayerData, PlayerMetaData.class);

                if(metaData == null) {
                    myLogger.atWarning().log("No data found for player: " + username);
                } else {
                    addNewActivePlayer(username, metaData);
                    return;
                }

            } catch (IOException e) {
                myLogger.atSevere().log("Failed to load player metadata for player: " + username);
            }
        }

        createDefaultPlayerData(findPlayerByName(username));
    }

    public void createDefaultPlayerData(PlayerRef playerRef) {
        if(playerRef == null) {
            myLogger.atSevere().log("Failed to create MetaData for a player!");
            return;
        }

        PlayerMetaData data = new PlayerMetaData();
        String username = playerRef.getUsername();

        data.setNickname("");
        data.setLanguage("en-us");
        data.setFirstJoined(0);
        data.setLastJoined(0);
        data.setPlayTimeMillis(0);
        data.setTotalDeaths(0);
        data.setPlayerKills(0);
        data.setMobKills(0);
        data.setDamageGiven(0);
        data.setDamageTaken(0);
        data.setBlocksBroken(0);
        data.setBlocksPlaced(0);
        data.setDistanceWalked(0);
        data.setItemsCrafted(0);
        data.setItemsBroken(0);
        data.setGodModeEnabled(false);
        data.setVanished(false);
        data.setFlying(false);
        data.setMuted(false);

        Ref<EntityStore> ref = playerRef.getReference();
        Store<EntityStore> store = Objects.requireNonNull(ref).getStore();
        UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());

        data.setUUID(Objects.requireNonNull(uuidComponent).getUuid());
        data.addUsername(username);

        addNewActivePlayer(username, data);
        savePlayerMetaData(username, data);

    }


    public Path getPlayerDataPath() {return playerDataPath;}
}
