package com.raeden.hytale.core.player;

import com.google.gson.reflect.TypeToken;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.config.containers.ChatConfig;
import com.raeden.hytale.lang.LangKey;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.raeden.hytale.HytaleFoundations.*;
import static com.raeden.hytale.utils.FileManager.*;
import static com.raeden.hytale.utils.GeneralUtils.findPlayerByName;
import static com.raeden.hytale.utils.GeneralUtils.getPlayerUUID;

public class PlayerDataManager {
    private final HytaleFoundations hytaleFoundations;
    public final String USERMAP_JSON = "usermap.json";
    public final String PROFILE_JSON = "profile.json";
    public final String STATS_JSON = "stats.json";
    public final String MAIL_JSON = "mailbox.json";
    public final String HISTORY_JSON = "history.json";
    private final Path playerDataPath;

    private final Map<String, PlayerProfile> playerProfiles;
    private final Map<String, PlayerStats> playerStats;

    private ChatConfig chatConfig;

    public PlayerDataManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        this.chatConfig = hytaleFoundations.getConfigManager().getDefaultChatConfig();
        playerDataPath = hytaleFoundations.getDataDirectory().resolve("data").resolve("players");

        playerProfiles = new ConcurrentHashMap<>();
        playerStats = new ConcurrentHashMap<>();
        verifyDataPath();
        createUserMap();
    }

    private void verifyDataPath() {
        createDirectory(playerDataPath, true);
    }

    // User Map
    private void createUserMap() {
        Path userMapPath = playerDataPath.resolve(USERMAP_JSON);
        Map<UUID, String> users = new ConcurrentHashMap<>();
        if(!Files.exists(userMapPath)) {
            saveJsonFile(USERMAP_JSON, userMapPath, users, true);
        }
    }

    private Map<UUID, String> loadUserMap() {
        Path userMapPath = playerDataPath.resolve(USERMAP_JSON);
        Type type = new TypeToken<Map<UUID, String>>(){}.getType();
        return loadJsonFile(USERMAP_JSON, userMapPath, type);
    }

    private void updateUserMap(UUID id, String username) {
        Map<UUID, String> users = loadUserMap();
        if(users == null) users = new ConcurrentHashMap<>();
        String oldUsername = users.get(id);
        users.put(id, username);

        Path userMapPath = playerDataPath.resolve(USERMAP_JSON);
        saveJsonFile(USERMAP_JSON, userMapPath, users, true);

        if (oldUsername != null && !oldUsername.equals(username)) {
            Path oldDataPath = playerDataPath.resolve(oldUsername);
            Path newDataPath = playerDataPath.resolve(username);
            if (Files.exists(oldDataPath)) {
                try {
                    Files.move(oldDataPath, newDataPath);
                } catch (IOException e) {
                    myLogger.atSevere().log(e.getMessage());
                }
            }
        }
    }

    private void verifyUserID(String username) {
        Map<UUID, String> users = loadUserMap();
        if(users == null) return;

        UUID playerID = getPlayerUUID(username);
        boolean mismatch = false;

        for(Map.Entry<UUID, String> entry : users.entrySet()) {
            if(playerID.equals(entry.getKey())) {
                if(!entry.getValue().equals(username)) {
                    myLogger.atWarning().log(langManager.getMessage(LangKey.MISMATCH_FOUND, true, USERMAP_JSON + " |" + playerID + ", " + entry.getValue() + "[EXPECTED: " + username + "]").getAnsiMessage());
                    mismatch = true;
                    break;
                }
                break;
            }
        }
        if(mismatch) updateUserMap(playerID, username);
    }
    public <T> void savePlayerData(String username, String jsonName, T data) {
        savePlayerData(username, jsonName, data, true);
    }
    // Saving, Loading and Creating Player Data
    public <T> void savePlayerData(String username, String jsonName, T data, boolean showInfo) {
        if (!jsonName.endsWith(".json")) {
            jsonName += ".json";
        }
        Path playerFolder = playerDataPath.resolve(username);
        try {
            if (!Files.exists(playerFolder)) {
                Files.createDirectories(playerFolder);
            }
        } catch (IOException e) {
            logError("PlayerDataManager-SavePlayerData", e);
            myLogger.atSevere().log(langManager.getMessage(LangKey.CREATE_FAILURE,true, "player directory").getAnsiMessage());
            return;
        }
        Path savePath = playerFolder.resolve(jsonName);
        saveJsonFile(jsonName, savePath, data, showInfo);
    }
    // Getting data online or offline
    public PlayerProfile getPlayerProfile(String username) {
        PlayerRef ref = findPlayerByName(username);
        if(ref != null) {
            return getOnlinePlayerProfile(username);
        }
        if(doesPlayerDataExist(username)) {
            return getPlayerProfileFromFile(username);
        }
        return null;
    }
    public PlayerStats getPlayerStats(String username) {
        PlayerRef ref = findPlayerByName(username);
        if(ref != null) {
            return getOnlinePlayerStats(username);
        }
        if(doesPlayerDataExist(username)) {
            return getPlayerStatsFromFile(username);
        }
        return null;
    }
    // Getting data from Files
    public PlayerProfile getPlayerProfileFromFile(String username) {
        Path profileJsonPath = playerDataPath.resolve(username).resolve(PROFILE_JSON);
        if(!Files.exists(profileJsonPath)) {
            myLogger.atSevere().log(langManager.getMessage(LangKey.FILE_NOT_FOUND_LOC, true, profileJsonPath.getFileName().toString(), profileJsonPath.toString()).getAnsiMessage());
            return null;
        }
        String fileName = PROFILE_JSON + ": " + username;
        return loadJsonFile(fileName, profileJsonPath, PlayerProfile.class, true);
    }

    public PlayerStats getPlayerStatsFromFile(String username) {
        Path statsJsonPath = playerDataPath.resolve(username).resolve(STATS_JSON);
        if(!Files.exists(statsJsonPath)) {
            myLogger.atSevere().log(langManager.getMessage(LangKey.FILE_NOT_FOUND_LOC, true, statsJsonPath.getFileName().toString(), statsJsonPath.toString()).getAnsiMessage());
            return null;
        }
        String fileName = STATS_JSON + ": " + username;
        return loadJsonFile(fileName, statsJsonPath, PlayerStats.class, true);
    }

    public PlayerHistory getPlayerHistory(String username) {
        Path historyJsonPath = playerDataPath.resolve(username).resolve(HISTORY_JSON);
        if(!Files.exists(historyJsonPath)) {
            myLogger.atSevere().log(langManager.getMessage(LangKey.FILE_NOT_FOUND_LOC, true, historyJsonPath.getFileName().toString(), historyJsonPath.toString()).getAnsiMessage());
            return null;
        }
        String fileName = HISTORY_JSON + ": " + username;
        return loadJsonFile(fileName, historyJsonPath, PlayerHistory.class, true);
    }

    public PlayerMailbox getPlayerMailbox(String username) {
        if(username == null) return null;
        Path mailJsonPath = playerDataPath.resolve(username).resolve(MAIL_JSON);
        if(!Files.exists(mailJsonPath)) {
            myLogger.atSevere().log(langManager.getMessage(LangKey.FILE_NOT_FOUND_LOC, true, mailJsonPath.getFileName().toString(), mailJsonPath.toString()).getAnsiMessage());
            return null;
        }
        String fileName = MAIL_JSON + ": " + username;
        return loadJsonFile(fileName, mailJsonPath, PlayerMailbox.class, false);
    }

    // Load Player Data
    public void loadPlayerData(String username) {
        verifyUserID(username);
        Path dataFolder = playerDataPath.resolve(username);
        if(!Files.exists(dataFolder)) {
            createDefaultPlayerData(findPlayerByName(username));
            return;
        }
        // Player Profile
        Path profileJson = dataFolder.resolve(PROFILE_JSON);
        PlayerProfile profile = loadJsonFile(PROFILE_JSON, profileJson, PlayerProfile.class);
        addPlayerProfile(username, profile);
        // Player Stats
        Path statsJson = dataFolder.resolve(STATS_JSON);
        PlayerStats stats = loadJsonFile(STATS_JSON, statsJson, PlayerStats.class);
        addPlayerStats(username, stats);
    }

    // Create generic profiles
    private PlayerProfile createPlayerProfile(PlayerRef playerRef) {
        String username = playerRef.getUsername();
        UUID playerID = getPlayerUUID(playerRef);

        PlayerProfile profile = new PlayerProfile();
        profile.setUUID(playerID);
        profile.addUsername(username);
        profile.setLanguage("en-us");
        profile.setNickname("");
        profile.setUsernameColorCode("#FFFFFF");
        profile.setShowNickname(chatConfig.isShowNickNames());
        profile.setShowPrefix(chatConfig.isShowPrefix());
        profile.setShowSuffix(chatConfig.isShowSuffix());
        profile.setMaxSuffix(chatConfig.getMaxSuffix());
        profile.setMaxPrefix(chatConfig.getMaxPrefix());
        profile.setGodModeEnabled(false);
        profile.setVanished(false);
        profile.setFlying(false);
        profile.setAnonymous(false);
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
        stats.setTotalTimesReportedByPlayers(0);

        return stats;
    }

    private void createDefaultPlayerData(PlayerRef playerRef) {
        if(playerRef == null) {
            myLogger.atSevere().log(langManager.getMessage(LangKey.CREATE_FAILURE,true, "player data!").getAnsiMessage());
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
        PlayerStats stats = getOnlinePlayerStats(username);
        PlayerProfile profile = getOnlinePlayerProfile(username);
        if(stats == null || profile == null) return;
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
        PlayerStats stats = getOnlinePlayerStats(username);
        PlayerProfile profile = getOnlinePlayerProfile(username);
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
        savePlayerData(username, PROFILE_JSON, getOnlinePlayerProfile(username));
        savePlayerData(username, STATS_JSON, getOnlinePlayerStats(username));

        removePlayerStats(username);
        removePlayerProfile(username);
    }

    // Management and getters
    public boolean doesPlayerDataExist(String username) {
        File playerDataFile = playerDataPath.resolve(username).toFile();
        return playerDataFile.exists();
    }

    public PlayerProfile getOnlinePlayerProfile(String username) { return playerProfiles.get(username);}
    public void addPlayerProfile(String username, PlayerProfile profile) {playerProfiles.put(username, profile);}
    public void removePlayerProfile(String username) { playerProfiles.remove(username);}
    public Map<String, PlayerProfile> getPlayerProfiles(){return playerProfiles;}

    public PlayerStats getOnlinePlayerStats(String username) { return playerStats.get(username);}
    public void addPlayerStats(String username, PlayerStats stats) {playerStats.put(username, stats);}
    public void removePlayerStats(String username) { playerStats.remove(username);}
    public Map<String, PlayerStats> getOnlinePlayerStats(){return playerStats;}

}
