package com.raeden.hytale.core.player;

import com.google.gson.reflect.TypeToken;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.config.containers.ChatConfig;
import com.raeden.hytale.core.lang.LangKey;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.raeden.hytale.HytaleFoundations.*;
import static com.raeden.hytale.utils.FileUtils.*;
import static com.raeden.hytale.utils.PlayerUtils.*;

public class PlayerDataManager {
    private final HytaleFoundations hytaleFoundations;
    private final String PATH_NAME = "players";
    public final String USERMAP_FILENAME = "usermap.json";
    public final String PROFILE_FILENAME = "profile.json";
    public final String STATS_FILENAME = "stats.json";
    public final String MAIL_FILENAME = "mailbox.json";
    public final String HISTORY_FILENAME = "history.json";

    private final Map<String, String> playerWorld;
    private final Map<String, Path> playerDataPaths;
    private final Map<String, PlayerProfile> playerProfiles;
    private final Map<String, PlayerStats> playerStats;

    private ChatConfig chatConfig;

    public PlayerDataManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        this.chatConfig = hytaleFoundations.getConfigManager().getDefaultChatConfig();

        playerWorld = new ConcurrentHashMap<>();
        playerDataPaths = new ConcurrentHashMap<>();
        playerProfiles = new ConcurrentHashMap<>();
        playerStats = new ConcurrentHashMap<>();

        validatePlayerDataPaths();
    }

    // data paths
    public void validatePlayerDataPaths() {
        hytaleFoundations.getDataGroupManager().createDataDirectories(PATH_NAME);
        playerDataPaths.putAll(hytaleFoundations.getDataGroupManager().getDataDirPaths(PATH_NAME));
        createUsermaps();
    }

    // User Map
    private void createUsermaps() {
        for(Map.Entry<String, Path> entry : playerDataPaths.entrySet()) {
            Path usermapPath = entry.getValue().resolve(USERMAP_FILENAME);
            Map<UUID, String> users = new ConcurrentHashMap<>();
            if(!Files.exists(usermapPath)) {
                saveJsonFile(USERMAP_FILENAME, usermapPath, users, true);
            }
        }
    }

    private Map<UUID, String> loadUsermap(String dataGroup) {
        if(!playerDataPaths.containsKey(dataGroup)) return null;
        Path userMapPath = playerDataPaths.get(dataGroup).resolve(USERMAP_FILENAME);
        Type type = new TypeToken<Map<UUID, String>>(){}.getType();
        return loadJsonFile(USERMAP_FILENAME, userMapPath, type);
    }

    private void updateUsermap(UUID id, String username) {
        String dataGroup = getPlayerDataGroup(username);
        if(dataGroup == null || dataGroup.isEmpty()) {
            myLogger.atWarning().log(LM.getConsoleMessage(LangKey.CHECK_FAILURE, "data group of " + username).getAnsiMessage());
            return;
        }
        Map<UUID, String> users = loadUsermap(dataGroup);
        if(users == null) users = new ConcurrentHashMap<>();
        String oldUsername = users.get(id);
        users.put(id, username);

        Path userMapPath = playerDataPaths.get(dataGroup).resolve(USERMAP_FILENAME);
        myLogger.atInfo().log(LM.getConsoleMessage(LangKey.UPDATE_SUCCESS, USERMAP_FILENAME).getAnsiMessage());
        saveJsonFile(USERMAP_FILENAME, userMapPath, users, false);

        if (oldUsername != null && !oldUsername.equals(username)) {
            Path oldDataPath = playerDataPaths.get(dataGroup).resolve(oldUsername);
            Path newDataPath = playerDataPaths.get(dataGroup).resolve(username);
            if (Files.exists(oldDataPath)) {
                try {
                    Files.move(oldDataPath, newDataPath);
                } catch (IOException e) {
                    logError(ERROR_LOG_DIRECTORY, "updateUserMap", e);
                }
            }
        }
    }

    private void verifyUserID(String username) {
        String dataGroup = getPlayerDataGroup(username);
        if(dataGroup == null || dataGroup.isEmpty()) {
            myLogger.atWarning().log(LM.getConsoleMessage(LangKey.CHECK_FAILURE, "data group of " + username).getAnsiMessage());
            return;
        }
        Map<UUID, String> users = loadUsermap(dataGroup);
        if(users == null) return;

        UUID playerID = getPlayerUUID(username);
        boolean mismatch = false;

        for(Map.Entry<UUID, String> entry : users.entrySet()) {
            if(playerID.equals(entry.getKey())) {
                if(!entry.getValue().equals(username)) {
                    myLogger.atWarning().log(LM.getConsoleMessage(LangKey.MISMATCH_FOUND, USERMAP_FILENAME + " |" + playerID + ", " + entry.getValue() + "[EXPECTED: " + username + "]").getAnsiMessage());
                    mismatch = true;
                    break;
                }
                break;
            }
        }
        if(mismatch) updateUsermap(playerID, username);
    }

    // Saving, Loading and Creating Player Data
    public void saveAllPlayerData() {
        Universe universe = Universe.get();
        for(PlayerRef player : universe.getPlayers()) {
            if(player == null || player.getWorldUuid() == null) continue;
            String dataGroup = Objects.requireNonNull(universe.getWorld(player.getWorldUuid())).getName();
            verifyUserID(player.getUsername());
            savePlayerData(player.getUsername(), PROFILE_FILENAME, getOnlinePlayerProfile(player.getUsername()), false);
            savePlayerData(player.getUsername(), STATS_FILENAME, getOnlinePlayerStats(player.getUsername()), false);
            myLogger.atInfo().log(LM.getConsoleMessage(LangKey.SAVE_PD_SUCCESS, player.getUsername()).getAnsiMessage());
        }
    }
    public void saveTargetPlayerData(String targetUsername) {
        verifyUserID(targetUsername);
        if(!isPlayerOnline(targetUsername)) return;
        savePlayerData(targetUsername, PROFILE_FILENAME, getOnlinePlayerProfile(targetUsername), false);
        savePlayerData(targetUsername, STATS_FILENAME, getOnlinePlayerStats(targetUsername), false);
        myLogger.atInfo().log(LM.getConsoleMessage(LangKey.SAVE_PD_SUCCESS, targetUsername).getAnsiMessage());
    }
    public void reloadAllPlayerData() {
        Universe universe = Universe.get();
        for(PlayerRef player : universe.getPlayers()) {
            verifyUserID(player.getUsername());
            loadPlayerData(player.getUsername());
            myLogger.atInfo().log(LM.getConsoleMessage(LangKey.RELOAD_PLAYER_DATA, player.getUsername()).getAnsiMessage());
        }
    }
    public void reloadTargetPlayerData(String targetUsername) {
        verifyUserID(targetUsername);
        if(!isPlayerOnline(targetUsername)) return;
        loadPlayerData(targetUsername);
        myLogger.atInfo().log(LM.getConsoleMessage(LangKey.RELOAD_PLAYER_DATA, targetUsername).getAnsiMessage());
    }

    public <T> void savePlayerData(String username, String jsonName, T data) {
        savePlayerData(username, jsonName, data, true);
    }
    public <T> void savePlayerData(String username, String jsonName, T data, boolean showInfo) {
        String dataGroup = getPlayerDataGroup(username);
        if(dataGroup == null || dataGroup.isEmpty()) {
            myLogger.atWarning().log(LM.getConsoleMessage(LangKey.CHECK_FAILURE, "data group of " + username).getAnsiMessage());
            return;
        }
        if (!jsonName.endsWith(".json")) {
            jsonName += ".json";
        }
        Path playerFolder = playerDataPaths.get(dataGroup).resolve(username);
        try {
            if (!Files.exists(playerFolder)) {
                Files.createDirectories(playerFolder);
            }
        } catch (IOException e) {
            logError("PlayerDataManager-SavePlayerData", e);
            myLogger.atSevere().log(LM.getConsoleMessage(LangKey.CREATE_FAILURE,"player directory").getAnsiMessage());
            return;
        }
        Path savePath = playerFolder.resolve(jsonName);
        saveJsonFile(jsonName, savePath, data, showInfo);
    }
    // Getting data online or offline
    public UUID getPlayerUUID(String targetUsername) {
        return getPlayerUUID(null, targetUsername);
    }
    public UUID getPlayerUUID(PlayerRef sender, String targetUsername) {
        UUID targetUUID;
        PlayerRef targetRef = findPlayerByName(targetUsername);

        if(targetRef == null) {
            PlayerProfile profile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(targetUsername);
            if(profile == null) {
                if(sender != null) sender.sendMessage(LM.getPlayerMessage(sender.getUsername(), LangKey.PLAYER_NOT_FOUND_MSG, targetUsername));
                return null;
            }
            targetUUID = profile.getUuid();
        } else {
            targetUUID = targetRef.getUuid();
        }
        return targetUUID;
    }
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
        String dataGroup = getPlayerDataGroup(username);
        if(dataGroup == null || dataGroup.isEmpty()) {
            myLogger.atWarning().log(LM.getConsoleMessage(LangKey.CHECK_FAILURE, "data group of " + username).getAnsiMessage());
            return null;
        }
        Path profileJsonPath = playerDataPaths.get(dataGroup).resolve(username).resolve(PROFILE_FILENAME);
        if(!Files.exists(profileJsonPath)) {
            myLogger.atSevere().log(LM.getConsoleMessage(LangKey.FILE_NOT_FOUND_LOC, profileJsonPath.getFileName().toString(), profileJsonPath.toString()).getAnsiMessage());
            return null;
        }
        String fileName = PROFILE_FILENAME + ": " + username;
        return loadJsonFile(fileName, profileJsonPath, PlayerProfile.class, true);
    }

    public PlayerStats getPlayerStatsFromFile(String username) {
        String dataGroup = getPlayerDataGroup(username);
        if(dataGroup == null || dataGroup.isEmpty()) {
            myLogger.atWarning().log(LM.getConsoleMessage(LangKey.CHECK_FAILURE, "data group of " + username).getAnsiMessage());
            return null;
        }
        Path statsJsonPath = playerDataPaths.get(dataGroup).resolve(username).resolve(STATS_FILENAME);
        if(!Files.exists(statsJsonPath)) {
            myLogger.atSevere().log(LM.getConsoleMessage(LangKey.FILE_NOT_FOUND_LOC, statsJsonPath.getFileName().toString(), statsJsonPath.toString()).getAnsiMessage());
            return null;
        }
        String fileName = STATS_FILENAME + ": " + username;
        return loadJsonFile(fileName, statsJsonPath, PlayerStats.class, true);
    }

    public PlayerHistory getPlayerHistory(String username) {
        String dataGroup = getPlayerDataGroup(username);
        if(dataGroup == null || dataGroup.isEmpty()) {
            myLogger.atWarning().log(LM.getConsoleMessage(LangKey.CHECK_FAILURE, "data group of " + username).getAnsiMessage());
            return null;
        }
        Path historyJsonPath = playerDataPaths.get(dataGroup).resolve(username).resolve(HISTORY_FILENAME);
        if(!Files.exists(historyJsonPath)) {
            myLogger.atSevere().log(LM.getConsoleMessage(LangKey.FILE_NOT_FOUND_LOC, historyJsonPath.getFileName().toString(), historyJsonPath.toString()).getAnsiMessage());
            return null;
        }
        String fileName = HISTORY_FILENAME + ": " + username;
        return loadJsonFile(fileName, historyJsonPath, PlayerHistory.class, true);
    }

    public PlayerMailbox getPlayerMailbox(String username) {
        String dataGroup = getPlayerDataGroup(username);
        if(dataGroup == null || dataGroup.isEmpty()) {
            myLogger.atWarning().log(LM.getConsoleMessage(LangKey.CHECK_FAILURE, "data group of " + username).getAnsiMessage());
            return null;
        }
        if(username == null) return null;
        Path mailJsonPath = playerDataPaths.get(dataGroup).resolve(username).resolve(MAIL_FILENAME);
        if(!Files.exists(mailJsonPath)) {
            myLogger.atSevere().log(LM.getConsoleMessage(LangKey.FILE_NOT_FOUND_LOC, mailJsonPath.getFileName().toString(), mailJsonPath.toString()).getAnsiMessage());
            return null;
        }
        String fileName = MAIL_FILENAME + ": " + username;
        return loadJsonFile(fileName, mailJsonPath, PlayerMailbox.class, false);
    }

    // Load Player Data
    public void loadPlayerData(String username) {
        String dataGroup = getPlayerDataGroup(username);
        if(dataGroup == null || dataGroup.isEmpty()) {
            myLogger.atWarning().log(LM.getConsoleMessage(LangKey.CHECK_FAILURE, "data group of " + username).getAnsiMessage());
            return;
        }
        verifyUserID(username);
        Path dataFolder = playerDataPaths.get(dataGroup).resolve(username);
        if(!Files.exists(dataFolder)) {
            createDefaultPlayerData(findPlayerByName(username));
            return;
        }
        Path profileJson = dataFolder.resolve(PROFILE_FILENAME);
        PlayerProfile profile = loadJsonFile(PROFILE_FILENAME, profileJson, PlayerProfile.class);
        addPlayerProfile(username, profile);

        Path statsJson = dataFolder.resolve(STATS_FILENAME);
        PlayerStats stats = loadJsonFile(STATS_FILENAME, statsJson, PlayerStats.class);
        addPlayerStats(username, stats);
    }

    // Create generic profiles
    private PlayerProfile createPlayerProfile(PlayerRef playerRef) {
        String username = playerRef.getUsername();
        UUID playerID = playerRef.getUuid();

        PlayerProfile profile = new PlayerProfile();
        profile.setUuid(playerID);
        profile.addUsername(username);
        profile.setLanguage("en-us");
        profile.setNickname("");
        profile.setUsernameColorCode("&f");
        profile.setShowNickname(chatConfig.isShowNickname());
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
        profile.setRankId("");

        return profile;
    }

    private PlayerStats createPlayerStats() {
        PlayerStats stats = new PlayerStats();
        stats.setFirstJoined(0);
        stats.setLastJoined(0);
        stats.setPlayTimeMillis(0);
        stats.setCollectStats(true);
        stats.setTotalDeaths(0);
        stats.setPlayerKills(0);
        stats.setMobKills(0);
        stats.setDamageGivenPve(0);
        stats.setDamageTakenPve(0);
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
            myLogger.atSevere().log(LM.getConsoleMessage(LangKey.CREATE_FAILURE,"player data").getAnsiMessage());
            return;
        }

        String username = playerRef.getUsername();
        UUID id = playerRef.getUuid();

        PlayerProfile profile = createPlayerProfile(playerRef);
        PlayerStats stats = createPlayerStats();
        PlayerMailbox mailbox = new PlayerMailbox();
        PlayerHistory history = new PlayerHistory();

        addPlayerProfile(username, profile);
        addPlayerStats(username, stats);

        savePlayerData(username, PROFILE_FILENAME, profile);
        savePlayerData(username, STATS_FILENAME, stats);
        savePlayerData(username, MAIL_FILENAME, mailbox);
        savePlayerData(username, HISTORY_FILENAME, history);

        updateUsermap(id, username);
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
        try {
            String username = player.getDisplayName();
            playerWorld.put(username, Objects.requireNonNull(player.getWorld()).getName());

            loadPlayerData(username);
            PlayerStats stats = getOnlinePlayerStats(username);
            PlayerProfile profile = getOnlinePlayerProfile(username);
            if(stats.getPlayTimeMillis() == 0) {
                player.sendMessage(Message.raw("Welcome " + player.getDisplayName() + " to the server!"));
                stats.setFirstJoined(System.currentTimeMillis());
            }
            stats.setLastJoined(System.currentTimeMillis());
            profile.setSessionStart(System.currentTimeMillis());
        } catch (Exception e) {
            logError("playerLogin", e);
        }
    }

    public void playerLogout(PlayerRef playerRef) {
        String username = playerRef.getUsername();
        UUID id = playerRef.getUuid();
        savePlayTime(username);
        savePlayerData(username, PROFILE_FILENAME, getOnlinePlayerProfile(username));
        savePlayerData(username, STATS_FILENAME, getOnlinePlayerStats(username));

        removePlayerStats(username);
        removePlayerProfile(username);
        hytaleFoundations.getPlayerMovementListener().removePlayerFromCache(id);
    }

    // Management and getters
    public boolean doesPlayerExist(String username) {
        return (findPlayerByName(username) != null) || doesPlayerDataExist(username);
    }
    public boolean doesPlayerDataExist(String username) {
        String dataGroup = getPlayerDataGroup(username);
        if(dataGroup == null || dataGroup.isEmpty()) {
            myLogger.atWarning().log(LM.getConsoleMessage(LangKey.CHECK_FAILURE, "data group of " + username).getAnsiMessage());
            return false;
        }
        File playerDataFile = playerDataPaths.get(dataGroup).resolve(username + ".json").toFile();
        return playerDataFile.exists();
    }

    public String getPlayerDataGroup(String username) {
        return hytaleFoundations.getDataGroupManager().getDataGroupOfWorld(playerWorld.get(username));
    }

    public PlayerProfile getOnlinePlayerProfile(String username) { return playerProfiles.get(username);}
    public void addPlayerProfile(String username, PlayerProfile profile) {playerProfiles.put(username, profile);}
    public void removePlayerProfile(String username) { playerProfiles.remove(username);}
    public Map<String, PlayerProfile> getPlayerProfileMap(){return playerProfiles;}

    public PlayerStats getOnlinePlayerStats(String username) { return playerStats.get(username);}
    public void addPlayerStats(String username, PlayerStats stats) {playerStats.put(username, stats);}
    public void removePlayerStats(String username) { playerStats.remove(username);}
    public Map<String, PlayerStats> getPlayerStatsMap(){return playerStats;}

}
