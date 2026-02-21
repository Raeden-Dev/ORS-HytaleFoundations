package com.raeden.hytale.modules.chat;

import com.google.gson.reflect.TypeToken;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.player.PlayerDataManager;
import com.raeden.hytale.core.player.PlayerProfile;
import com.raeden.hytale.lang.LangKey;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.raeden.hytale.HytaleFoundations.langManager;
import static com.raeden.hytale.HytaleFoundations.myLogger;
import static com.raeden.hytale.utils.FileManager.loadJsonFile;
import static com.raeden.hytale.utils.FileManager.saveJsonFile;
import static com.raeden.hytale.utils.GeneralUtils.findPlayerByName;

public class AffixManager {
    private final HytaleFoundations hytaleFoundations;
    private final PlayerDataManager playerDataManager;
    private final String AFFIX_FILE_NAME = "affix.json";
    private final Path affixFilePath;

    private final Map<String, PlayerAffix> AFFIX_MAP;

    public enum AffixType { PREFIX, SUFFIX }

    public AffixManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        playerDataManager = hytaleFoundations.getPlayerDataManager();
        affixFilePath = hytaleFoundations.getDataDirectory().resolve(AFFIX_FILE_NAME);
        AFFIX_MAP = new ConcurrentHashMap<>();

        initializeAffixManager();
    }
    // Initialization and Loading
    public void initializeAffixManager() {
        AFFIX_MAP.putAll(getDefaultAffixMap());
        if (Files.exists(affixFilePath)) {
            loadAffixes();
        } else {
            myLogger.atInfo().log(langManager.getMessage(LangKey.CREATE_SUCCESS, true, AFFIX_FILE_NAME, affixFilePath.toString()).getAnsiMessage());
            saveAffixFile();
        }
    }
    public void saveAffixFile() {
        AffixHolder affixHolder = new AffixHolder();
        affixHolder.AFFIX_LIST = new ArrayList<>(AFFIX_MAP.values());
        saveJsonFile(AFFIX_FILE_NAME, affixFilePath, affixHolder, true);
    }
    public void loadAffixes() {
        Type type = new TypeToken<AffixHolder>(){}.getType();
        AffixHolder affixHolder = loadJsonFile(AFFIX_FILE_NAME, affixFilePath, type, true);
        if(affixHolder != null && affixHolder.AFFIX_LIST != null) {
            int newAffixes = 0;
            for (PlayerAffix affix : affixHolder.AFFIX_LIST) {
                if (!AFFIX_MAP.containsKey(affix.id)) {
                    newAffixes++;
                }
                AFFIX_MAP.put(affix.id, affix);
            }

            if (newAffixes > 0) {
                myLogger.atInfo().log(langManager.getMessage(LangKey.LOAD_SUCCESS, true, newAffixes + " affixes").getAnsiMessage());
            }
        } else {
            saveAffixFile();
        }
    }
    // Player Interaction
    public void addPrefixToPlayer(String username, String affixId) {
        modifyPlayerAffix(null, username, affixId, AffixType.PREFIX);
    }
    public void addPrefixToPlayer(PlayerRef caller, String username, String affixId) {
        modifyPlayerAffix(caller, username, affixId, AffixType.PREFIX);
    }
    public void addSuffixToPlayer(String username, String affixId) {
        modifyPlayerAffix(null, username, affixId, AffixType.SUFFIX);
    }
    public void addSuffixToPlayer(PlayerRef caller, String username, String affixId) {
        modifyPlayerAffix(caller, username, affixId, AffixType.SUFFIX);
    }
    private void modifyPlayerAffix(PlayerRef caller, String targetUsername, String affixId, AffixType type) {
        boolean isConsole = (caller == null);
        PlayerAffix affix = AFFIX_MAP.get(affixId);
        if (affix == null) {
            sendResponse(caller, LangKey.AFFIX_NOT_FOUND, isConsole, affixId);
            return;
        }
        PlayerProfile profile = playerDataManager.getPlayerProfile(targetUsername);
        if (profile == null) {
            if (isConsole) sendResponse(null, LangKey.PLAYER_NOT_FOUND, true, targetUsername);
            else sendResponse(caller, LangKey.PLAYER_NOT_FOUND, false, targetUsername);
            return;
        }
        if (type == AffixType.PREFIX) {
            if (profile.getActivePrefix().size() >= profile.getMaxPrefix()) {
                sendResponse(caller, LangKey.AFFIX_MAX, isConsole, "prefix", String.valueOf(profile.getMaxPrefix()));
                return;
            }
            profile.addToActivePrefix(affixId, affix.displayText);
        } else {
            if (profile.getActiveSuffix().size() >= profile.getMaxSuffix()) {
                sendResponse(caller, LangKey.AFFIX_MAX, isConsole, "suffix", String.valueOf(profile.getMaxSuffix()));
                return;
            }
            profile.addToActiveSuffix(affixId, affix.displayText);
        }
    }
    private void sendResponse(PlayerRef caller, LangKey key, boolean isConsole, String... args) {
        if (isConsole) {
            myLogger.atInfo().log(langManager.getMessage(key, true, args).getAnsiMessage());
        } else {
            caller.sendMessage(langManager.getMessage(key, false, args));
        }
    }
    // Classes and Getter / Setter
    public boolean canHavePrefix(String username) {
        return checkAffixConfig(username, true);
    }
    public boolean canHaveSuffix(String username) {
        return checkAffixConfig(username, false);
    }
    private boolean checkAffixConfig(String username, boolean isPrefix) {
        boolean globalSetting = isPrefix
                ? hytaleFoundations.getConfigManager().getDefaultChatConfig().isShowPrefix()
                : hytaleFoundations.getConfigManager().getDefaultChatConfig().isShowSuffix();

        if (!globalSetting) return false;
        if (username == null) return true;

        PlayerProfile profile = playerDataManager.getPlayerProfile(username);
        if (profile != null) {
            return isPrefix ? profile.isShowPrefix() : profile.isShowSuffix();
        }
        return false;
    }
    private Map<String, PlayerAffix> getDefaultAffixMap() {
        Map<String, PlayerAffix> map = new ConcurrentHashMap<>();
        addToMap(map, "df_op",      "&7&l[&r&b&lOP&r&7&l]",         10000);
        addToMap(map, "df_manager", "&7&l[&r&c&lManager&r&7&l]",    9999);
        addToMap(map, "df_admin",   "&7&l[&r&4&lAdmin&r&7&l]",      9998);
        addToMap(map, "df_mod",     "&7&l[&r&3&lModerator&r&7&l]",  9997);
        addToMap(map, "df_player",  "&7&l[&r&f&lPlayer&r&7&l]",     1);
        addToMap(map, "df_cracked", "&7&l[&r&c&lC&r&0&lR&r&f&lAC&r&6&lK&r&c&lED&r&7&l]", 10);
        addToMap(map, "df_beast",   "&7&l[&r&e&6&lBEAST&r&7&l]",    10);
        addToMap(map, "df_noob",    "&7&l[&r&5&d&lNOOB&r&7&l]",     10);
        addToMap(map, "df_pro",     "&7&l[&r&9&lPRO&r&7&l]",        10);
        addToMap(map, "df_amaze",   "&7&l[&r&c&6&e&2&a&b&3&1&d&5&lAMAZE&r&7&l]", 10);
        return map;
    }
    private void addToMap(Map<String, PlayerAffix> map, String id, String text, int priority) {
        map.put(id, new PlayerAffix(id, text, priority));
    }

    // Classes
    private static class AffixHolder {
        private String VERSION = "v1.0";
        private List<PlayerAffix> AFFIX_LIST = new ArrayList<>();

        public String getVersion() {return VERSION;}
        public void setVersion(String VERSION) {this.VERSION = VERSION;}
        public List<PlayerAffix> getAffixList() {return AFFIX_LIST;}
        public void setAffixList(List<PlayerAffix> AFFIX_LIST) {this.AFFIX_LIST = AFFIX_LIST;}
        public void addToAffixList(PlayerAffix affix) { AFFIX_LIST.add(affix);}

        public Map<String, PlayerAffix> getAffixListAsMap() {
            Map<String, PlayerAffix> affixMap = new ConcurrentHashMap<>();
            for(PlayerAffix affix : AFFIX_LIST) {
                affixMap.put(affix.id, affix);
            }
            return affixMap;
        }
    }

    private static class PlayerAffix {
        private String id;
        private String displayText;
        private int priority;
        public PlayerAffix(String id, String displayText, int priority) {
            this.id = id;
            this.displayText = displayText;
            this.priority = priority;
        }
        public String getId() {return id;}
        public void setId(String id) {this.id = id;}
        public String getDisplayText() {return displayText;}
        public void setDisplayText(String displayText) {this.displayText = displayText;}
        public int getPriority() {return priority;}
        public void setPriority(int priority) {this.priority = priority;}
    }

}
