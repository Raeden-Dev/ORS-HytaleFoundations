package com.raeden.hytale.core.data;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleEssentials;
import com.raeden.hytale.lang.LangKey;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Objects;

import static com.raeden.hytale.HytaleEssentials.*;
import static com.raeden.hytale.utils.GeneralUtils.findPlayerByName;

public class PlayerDataManager {
    //private final HytaleEssentials hytaleEssentials;
    private final Path playerDataPath;

    private final LinkedHashMap<String, PlayerData> activePlayers;

    public PlayerDataManager(HytaleEssentials hytaleEssentials) {
        //this.hytaleEssentials = hytaleEssentials;
        playerDataPath = hytaleEssentials.getDataDirectory().resolve("data").resolve("players");
        activePlayers = new LinkedHashMap<>();
        verifyPath();
    }

    private void verifyPath() {
        try {
            if(!Files.exists(playerDataPath)) {
                Files.createDirectories(playerDataPath);
                myLogger.atInfo().log(langManager.getMessage(LangKey.CREATE_DIRECTORY_W_LOC, "player data", playerDataPath.toString()).getAnsiMessage());
            }
        } catch (IOException e) {
            myLogger.atWarning().log(langManager.getMessage(LangKey.CREATE_DIRECTORY_FAIL_W_LOC, "player data", playerDataPath.toString()).getAnsiMessage());
        }
    }

    public boolean doesPlayerDataExist(String username) {
        File playerDataFile = playerDataPath.resolve(username + ".json").toFile();
        return playerDataFile.exists();
    }

    public PlayerData getPlayerDataFromFile(String username) {
        Path dataFile = playerDataPath.resolve(username + ".json");
        if(Files.exists(dataFile)) {
            try {
                String readPlayerData = Files.readString(dataFile, StandardCharsets.UTF_8);
                PlayerData playerData = GSON.fromJson(readPlayerData, PlayerData.class);

                if(playerData == null) {
                    myLogger.atSevere().log(langManager.getMessage(LangKey.LOAD_FAILURE, "player data of: ", username).getAnsiMessage());
                } else {
                    return playerData;
                }

            } catch (IOException e) {
                myLogger.atSevere().log(langManager.getMessage(LangKey.LOAD_FAILURE, "player data of: ", username).getAnsiMessage());
            }
        }
        return null;
    }

    public PlayerData getPlayerData(String username) {
        return activePlayers.get(username);
    }

    public void addNewActivePlayer(String username, PlayerData playerData) {
        activePlayers.put(username, playerData);
    }

    public void removeActivePlayer(String username) {
        activePlayers.remove(username);
    }

    public LinkedHashMap<String, PlayerData> getActivePlayers() {
        return activePlayers;
    }

    public void savePlayerData(String username, PlayerData playerData) {
        Path savePath = playerDataPath.resolve(username + ".json");
        String toJson = GSON.toJson(playerData);
        try {
            Files.writeString(savePath, toJson, StandardCharsets.UTF_8);
        } catch (IOException e) {
            myLogger.atSevere().log(langManager.getMessage(LangKey.SAVE_FAILURE, "player data for player: ", username).getAnsiMessage());
        }
    }

    public void loadPlayerData(String username) {
        Path dataFile = playerDataPath.resolve(username + ".json");
        if(Files.exists(dataFile)) {
            try {
                String readPlayerData = Files.readString(dataFile, StandardCharsets.UTF_8);
                PlayerData metaData = GSON.fromJson(readPlayerData, PlayerData.class);

                if(metaData == null) {
                    myLogger.atSevere().log(langManager.getMessage(LangKey.LOAD_FAILURE, "player data of: ", username).getAnsiMessage());
                } else {
                    addNewActivePlayer(username, metaData);
                    return;
                }

            } catch (IOException e) {
                myLogger.atSevere().log(langManager.getMessage(LangKey.LOAD_FAILURE, "player data of: ", username).getAnsiMessage());
            }
        }

        createDefaultPlayerData(findPlayerByName(username));
    }

    public void createDefaultPlayerData(PlayerRef playerRef) {
        if(playerRef == null) {
            myLogger.atSevere().log(langManager.getMessage(LangKey.CREATE_FAILURE, "player data!").getAnsiMessage());
            return;
        }

        PlayerData data = new PlayerData();
        String username = playerRef.getUsername();

        Ref<EntityStore> ref = playerRef.getReference();
        Store<EntityStore> store = Objects.requireNonNull(ref).getStore();
        UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());

        data.setUUID(Objects.requireNonNull(uuidComponent).getUuid());
        data.addUsername(username);
        data.setLanguage("en-us");
        data.setNickname("");

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

        data.setTotalMessagesSent(0);
        data.setMuted(false);
        data.setSilenced(false);

        addNewActivePlayer(username, data);
        savePlayerData(username, data);
    }


    public Path getPlayerDataPath() {return playerDataPath;}

    public Object getPlayerData(Ref<EntityStore> targetPlayer) {
        return null;
    }
}
