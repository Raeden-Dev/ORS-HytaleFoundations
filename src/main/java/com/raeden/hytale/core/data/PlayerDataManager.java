package com.raeden.hytale.core.data;

import com.google.gson.reflect.TypeToken;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.lang.LangKey;
import com.raeden.hytale.modules.chat.MailManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static com.raeden.hytale.HytaleFoundations.*;
import static com.raeden.hytale.utils.GeneralUtils.findPlayerByName;
import static com.raeden.hytale.utils.GeneralUtils.getPlayerUUID;

public class PlayerDataManager {
    //private final HytaleFoundations hytaleFoundations;
    public final String USERMAP_JSON = "usermap.json";
    public final String PROFILE_JSON = "profile.json";
    public final String STATS_JSON = "stats.json";
    public final String MAIL_JSON = "mailbox.json";
    public final String HISTORY_JSON = "history.json";
    private final Path playerDataPath;

    private final LinkedHashMap<String, PlayerProfile> playerProfiles;
    private final LinkedHashMap<String, PlayerStats> playerStats;

    public PlayerDataManager(HytaleFoundations hytaleFoundations) {
        //this.hytaleFoundations = hytaleFoundations;
        playerDataPath = hytaleFoundations.getDataDirectory().resolve("data").resolve("players");

        playerProfiles = new LinkedHashMap<>();
        playerStats = new LinkedHashMap<>();
        verifyDataPath();
        createUserMap();
    }

    private void verifyDataPath() {
        try {
            if(!Files.exists(playerDataPath)) {
                Files.createDirectories(playerDataPath);
                myLogger.atInfo().log(langManager.getMessage(LangKey.CREATE_DIRECTORY_W_LOC, "player data", playerDataPath.toString()).getAnsiMessage());
            }
        } catch (IOException e) {
            myLogger.atWarning().log(langManager.getMessage(LangKey.CREATE_DIRECTORY_FAIL_W_LOC, "player data", playerDataPath.toString()).getAnsiMessage());
        }
    }

    // User Map
    private void createUserMap() {
        Path userMapPath = playerDataPath.resolve(USERMAP_JSON);
        LinkedHashMap<UUID, String> users = new LinkedHashMap<>();
        String toJson = GSON.toJson(users);

        if(!Files.exists(userMapPath)) {
            try {
                Files.writeString(userMapPath, toJson, StandardCharsets.UTF_8);
                myLogger.atInfo().log(langManager.getMessage(LangKey.CREATE_SUCCESS, USERMAP_JSON).getAnsiMessage());
            } catch (IOException e) {
                myLogger.atWarning().log(langManager.getMessage(LangKey.CREATE_FAILURE, USERMAP_JSON).getAnsiMessage());
            }
        }
    }

    private LinkedHashMap<UUID, String> loadUserMap() {
        Path userMapPath = playerDataPath.resolve(USERMAP_JSON);
        if(Files.exists(userMapPath)) {
            try {
                String userMap = Files.readString(userMapPath, StandardCharsets.UTF_8);
                Type type = new TypeToken<LinkedHashMap<UUID, String>>(){}.getType();
                return GSON.fromJson(userMap, type);
            } catch (IOException e) {
                myLogger.atSevere().log(langManager.getMessage(LangKey.LOAD_FAILURE, USERMAP_JSON).getAnsiMessage());
                return null;
            }
        }

        return null;
    }

    private void updateUserMap(UUID id, String username) {
        LinkedHashMap<UUID, String> users = loadUserMap();
        if(users == null) users = new LinkedHashMap<>();
        String oldUsername = users.get(id);
        users.put(id, username);

        Path userMapPath = playerDataPath.resolve(USERMAP_JSON);
        String toJson = GSON.toJson(users);
        try {
            Files.writeString(userMapPath, toJson, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            myLogger.atWarning().log(langManager.getMessage(LangKey.SAVE_FAILURE, USERMAP_JSON).getAnsiMessage());
        }

        if (oldUsername != null && !oldUsername.equals(username)) {
            Path oldDataPath = playerDataPath.resolve(oldUsername);
            Path newDataPath = playerDataPath.resolve(username);

            if (Files.exists(oldDataPath)) {
                try {
                    Files.move(oldDataPath, newDataPath);
                    Files.delete(oldDataPath);
                } catch (IOException e) {
                    myLogger.atSevere().log(e.getMessage());
                }
            }
        }
    }

    private void verifyUserID(String username) {
        LinkedHashMap<UUID, String> users = loadUserMap();
        if(users == null) return;

        UUID playerID = getPlayerUUID(username);
        boolean mismatch = false;

        for(Map.Entry<UUID, String> entry : users.entrySet()) {
            if(playerID.equals(entry.getKey())) {
                if(!entry.getValue().equals(username)) {
                    myLogger.atWarning().log(langManager.getMessage(LangKey.MISMATCH_FOUND, USERMAP_JSON + " |" + playerID + ", " + entry.getValue() + "[EXPECTED: " + username + "]").getAnsiMessage());
                    mismatch = true;
                    break;
                }
                break;
            }
        }
        if(mismatch) updateUserMap(playerID, username);
    }

    // Saving, Loading and Creating Player Data
    public <T> void savePlayerData(String username, String jsonName, T data) {
        if (!jsonName.endsWith(".json")) {
            jsonName += ".json";
        }
        Path playerFolder = playerDataPath.resolve(username);
        Path savePath = playerFolder.resolve(jsonName);
        String toJson = GSON.toJson(data);
        try {
            if (!Files.exists(playerFolder)) {
                Files.createDirectories(playerFolder);
            }
            Files.writeString(savePath, toJson, StandardCharsets.UTF_8);
        } catch (IOException e) {
            myLogger.atSevere().log(langManager.getMessage(LangKey.SAVE_FAILURE, jsonName + ".json for player: ", username).getAnsiMessage());
        }
    }

    public PlayerProfile getPlayerProfileFromFile(String username) {
        Path profileJsonPath = playerDataPath.resolve(username).resolve(PROFILE_JSON);
        if(Files.exists(profileJsonPath)) {
            try {
                String profile = Files.readString(profileJsonPath, StandardCharsets.UTF_8);
                return GSON.fromJson(profile, PlayerProfile.class);
            } catch (IOException e) {
                myLogger.atSevere().log(langManager.getMessage(LangKey.LOAD_FAILURE, PROFILE_JSON + ": ", username).getAnsiMessage());
            }
        }
        myLogger.atSevere().log(langManager.getMessage(LangKey.LOAD_FAILURE, PROFILE_JSON + ": ", username).getAnsiMessage());
        return null;
    }

    public PlayerStats getPlayerStatsFromFile(String username) {
        Path statsJsonPath = playerDataPath.resolve(username).resolve(STATS_JSON);
        if(Files.exists(statsJsonPath)) {
            try {
                String stats = Files.readString(statsJsonPath, StandardCharsets.UTF_8);
                return GSON.fromJson(stats, PlayerStats.class);
            } catch (IOException e) {
                myLogger.atSevere().log(langManager.getMessage(LangKey.LOAD_FAILURE, STATS_JSON + ": ", username).getAnsiMessage());
            }
        }
        myLogger.atSevere().log(langManager.getMessage(LangKey.LOAD_FAILURE, STATS_JSON + ": ", username).getAnsiMessage());
        return null;
    }

    public PlayerHistory getPlayerHistory(String username) {
        Path historyJsonPath = playerDataPath.resolve(username).resolve(HISTORY_JSON);
        if(Files.exists(historyJsonPath)) {
            try {
                String history = Files.readString(historyJsonPath, StandardCharsets.UTF_8);
                return GSON.fromJson(history, PlayerHistory.class);
            } catch (IOException e) {
                myLogger.atSevere().log(langManager.getMessage(LangKey.LOAD_FAILURE, HISTORY_JSON + ": ", username).getAnsiMessage());
            }
        }
        myLogger.atSevere().log(langManager.getMessage(LangKey.LOAD_FAILURE, HISTORY_JSON + ": ", username).getAnsiMessage());
        return null;
    }

    public PlayerMailbox getPlayerMailbox(String username) {
        Path mailJsonPath = playerDataPath.resolve(username).resolve(MAIL_JSON);
        if(Files.exists(mailJsonPath)) {
            try {
                String mails = Files.readString(mailJsonPath, StandardCharsets.UTF_8);
                return GSON.fromJson(mails, PlayerMailbox.class);
            } catch (IOException e) {
                myLogger.atSevere().log(langManager.getMessage(LangKey.LOAD_FAILURE, MAIL_JSON + ": ", username).getAnsiMessage());
            }
        }
        myLogger.atSevere().log(langManager.getMessage(LangKey.LOAD_FAILURE, MAIL_JSON + ": ", username).getAnsiMessage());
        return null;
    }

    public void loadPlayerData(String username) {
        verifyUserID(username);

        Path dataFolder = playerDataPath.resolve(username);
        if(!Files.exists(dataFolder)) {
            createDefaultPlayerData(findPlayerByName(username));
            return;
        }
        // Player Profile
        Path profileJson = dataFolder.resolve(PROFILE_JSON);
        if(Files.exists(profileJson)) {
            try {
                String playerProfile = Files.readString(profileJson, StandardCharsets.UTF_8);
                PlayerProfile profile = GSON.fromJson(playerProfile, PlayerProfile.class);
                addPlayerProfile(username, profile);
            } catch (IOException e) {
                myLogger.atSevere().log(langManager.getMessage(LangKey.LOAD_FAILURE, PROFILE_JSON + ": ", username).getAnsiMessage());
            }
        }
        // Player Stats
        Path statsJson = dataFolder.resolve(STATS_JSON);
        if(Files.exists(statsJson)) {
            try {
                String playerStats = Files.readString(statsJson, StandardCharsets.UTF_8);
                PlayerStats stats = GSON.fromJson(playerStats, PlayerStats.class);
                addPlayerStats(username, stats);
            } catch (IOException e) {
                myLogger.atSevere().log(langManager.getMessage(LangKey.LOAD_FAILURE, STATS_JSON + ": ", username).getAnsiMessage());
            }
        }


    }

    private PlayerProfile createPlayerProfile(PlayerRef playerRef) {
        String username = playerRef.getUsername();
        UUID playerID = getPlayerUUID(playerRef);

        PlayerProfile profile = new PlayerProfile();
        profile.setUUID(playerID);
        profile.addUsername(username);
        profile.setLanguage("en-us");
        profile.setNickname("");
        profile.setGodModeEnabled(false);
        profile.setVanished(false);
        profile.setFlying(false);
        profile.setMuted(false);
        profile.setMuteDuration(0);
        profile.setSilenced(false);

        return profile;
    }

    private PlayerStats createPlayerStats() {
        PlayerStats stats = new PlayerStats();
        stats.setFirstJoined(0);
        stats.setLastJoined(0);
        stats.setPlayTimeMillis(0);
        stats.setTotalDeaths(0);
        stats.setPlayerKills(0);
        stats.setMobKills(0);
        stats.setDamageGiven(0);
        stats.setDamageTaken(0);
        stats.setBlocksBroken(0);
        stats.setBlocksPlaced(0);
        stats.setDistanceWalked(0);
        stats.setItemsCrafted(0);
        stats.setItemsBroken(0);
        stats.setTotalMessagesSent(0);

        return stats;
    }

    private void createDefaultPlayerData(PlayerRef playerRef) {
        if(playerRef == null) {
            myLogger.atSevere().log(langManager.getMessage(LangKey.CREATE_FAILURE, "player data!").getAnsiMessage());
            return;
        }

        String username = playerRef.getUsername();
        UUID id = getPlayerUUID(username);

        PlayerProfile profile = createPlayerProfile(playerRef);
        PlayerStats stats = createPlayerStats();
        PlayerMailbox mailbox = new PlayerMailbox();
        PlayerHistory history = new PlayerHistory();

        addPlayerProfile(username, profile);
        addPlayerStats(username, stats);

        savePlayerData(username, PROFILE_JSON, profile);
        savePlayerData(username, STATS_JSON, stats);
        savePlayerData(username, MAIL_JSON, mailbox);
        savePlayerData(username, HISTORY_JSON, history);

        updateUserMap(id, username);
    }

    public void savePlayTime(String username) {
        PlayerStats stats = getPlayerStats(username);
        PlayerProfile profile = getPlayerProfile(username);

        long timeNow = System.currentTimeMillis();
        long sessionDuration = timeNow -  profile.getSessionStart();
        if(sessionDuration > 0) {
            stats.setPlayTimeMillis(stats.getPlayTimeMillis() + sessionDuration);
        }
        profile.setSessionStart(System.currentTimeMillis());
    }

    // Login / Logout used by Events
    public void playerLogin(Player player) {
        String username = player.getDisplayName();

        loadPlayerData(username);

        PlayerStats stats = getPlayerStats(username);
        PlayerProfile profile = getPlayerProfile(username);

        if(stats.getPlayTimeMillis() == 0) {
            player.sendMessage(Message.raw("Welcome " + player.getDisplayName() + " to the server!"));
            stats.setFirstJoined(System.currentTimeMillis());
        }

        stats.setLastJoined(System.currentTimeMillis());
        profile.setSessionStart(System.currentTimeMillis());
    }

    public void playerLogout(PlayerRef playerRef) {
        String username = playerRef.getUsername();
        savePlayTime(username);
        savePlayerData(username, PROFILE_JSON, getPlayerProfile(username));
        savePlayerData(username, STATS_JSON, getPlayerStats(username));

        removePlayerStats(username);
        removePlayerProfile(username);
    }

    // Management and getters
    public boolean doesPlayerDataExist(String username) {
        File playerDataFile = playerDataPath.resolve(username).toFile();
        return playerDataFile.exists();
    }

    public PlayerProfile getPlayerProfile(String username) { return playerProfiles.get(username);}
    public void addPlayerProfile(String username, PlayerProfile profile) {playerProfiles.put(username, profile);}
    public void removePlayerProfile(String username) { playerProfiles.remove(username);}
    public LinkedHashMap<String, PlayerProfile> getPlayerProfiles(){return playerProfiles;}

    public PlayerStats getPlayerStats(String username) { return playerStats.get(username);}
    public void addPlayerStats(String username, PlayerStats stats) {playerStats.put(username, stats);}
    public void removePlayerStats(String username) { playerStats.remove(username);}
    public LinkedHashMap<String, PlayerStats> getPlayerStats(){return playerStats;}

}
