package com.raeden.hytale.modules.chat;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.player.PlayerDataManager;
import com.raeden.hytale.core.player.PlayerProfile;
import com.raeden.hytale.core.lang.LangKey;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.raeden.hytale.HytaleFoundations.*;
import static com.raeden.hytale.core.config.ConfigManager.AFFIX_FILENAME;
import static com.raeden.hytale.core.config.ConfigManager.AFFIX_VERSION;
import static com.raeden.hytale.utils.FileUtils.loadJsonFile;
import static com.raeden.hytale.utils.FileUtils.saveJsonFile;
import static com.raeden.hytale.utils.GeneralUtils.findPlayerByName;

public class AffixManager {
    private final HytaleFoundations hytaleFoundations;
    private final PlayerDataManager playerDataManager;
    private final String affixFileName = AFFIX_FILENAME;
    private final Path affixFilePath;

    private final Map<String, PlayerAffix> affixMap;

    public enum AffixType { PREFIX, SUFFIX }

    public AffixManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        playerDataManager = hytaleFoundations.getPlayerDataManager();
        affixFilePath = hytaleFoundations.getDataDirectory().resolve(affixFileName);
        affixMap = new ConcurrentHashMap<>();

        initializeAffixManager();
    }
    // Initialization and Loading
    private void initializeAffixManager() {
        affixMap.putAll(getDefaultAffixMap());
        if (Files.exists(affixFilePath)) {
            loadAffixes();
        } else {
            saveAffixFile();
        }
    }
    private void saveAffixFile() {
        AffixHolder affixHolder = new AffixHolder();
        affixHolder.affixList = new ArrayList<>(affixMap.values());
        saveJsonFile(affixFileName, affixFilePath, affixHolder, true);
    }
    public void loadAffixes() {
        Type type = new TypeToken<AffixHolder>(){}.getType();
        AffixHolder affixHolder = loadJsonFile(affixFileName, affixFilePath, type, true);
        if(affixHolder != null && affixHolder.affixList != null) {
            int newAffixes = 0;
            for (PlayerAffix affix : affixHolder.affixList) {
                if (!affixMap.containsKey(affix.id)) {
                    newAffixes++;
                }
                affixMap.put(affix.id, affix);
            }

            if (newAffixes > 0) {
                myLogger.atInfo().log(LM.getConsoleMessage(LangKey.LOAD_SUCCESS, newAffixes + " affixes").getAnsiMessage());
            }
        } else {
            saveAffixFile();
        }
    }
    // Player Interaction
    public void addPrefixToPlayer(String username, String affixId, boolean forceAdd) {
        modifyPlayerAffix(null, username, affixId, AffixType.PREFIX,false,  forceAdd);
    }
    public void addPrefixToPlayer(PlayerRef caller, String username, String affixId, boolean forceAdd) {
        modifyPlayerAffix(caller, username, affixId, AffixType.PREFIX,false,  forceAdd);
    }
    public void addSuffixToPlayer(String username, String affixId, boolean forceAdd) {
        modifyPlayerAffix(null, username, affixId, AffixType.SUFFIX,false,  forceAdd);
    }
    public void addSuffixToPlayer(PlayerRef caller, String username, String affixId, boolean forceAdd) {
        modifyPlayerAffix(caller, username, affixId, AffixType.SUFFIX, false, forceAdd);
    }

    public void removePrefixFromPlayer(String username, String affixId) {
        modifyPlayerAffix(null, username, affixId, AffixType.PREFIX, true, false);
    }
    public void removePrefixFromPlayer(PlayerRef caller, String username, String affixId) {
        modifyPlayerAffix(caller, username, affixId, AffixType.PREFIX, true, false);
    }
    public void removeSuffixFromPlayer(String username, String affixId) {
        modifyPlayerAffix(null, username, affixId, AffixType.SUFFIX, true, false);
    }
    public void removeSuffixFromPlayer(PlayerRef caller, String username, String affixId) {
        modifyPlayerAffix(caller, username, affixId, AffixType.SUFFIX, true, false);
    }
    public void removeAllAffixFromPlayer(PlayerRef caller, String targetUsername) {
        PlayerProfile profile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(targetUsername);
        if (profile == null) {
            if(caller != null) caller.sendMessage(LM.getPlayerMessage(caller.getUsername(),
                    LangKey.PLAYER_NOT_FOUND, targetUsername));
            return;
        }
        profile.clearActivePrefix();
        profile.clearActiveSuffix();
        if(caller != null) caller.sendMessage(LM.getPlayerMessage(caller.getUsername(),
                LangKey.AFFIX_REMOVE_ALL, "affix(s)",targetUsername));
    }
    public void removeAllCertainAffixFromPlayer(PlayerRef caller, String targetUsername, boolean isPrefix) {
        PlayerProfile profile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(targetUsername);
        String targetAffix = isPrefix ? "prefix(es)" : "suffix(es)";
        if (profile == null) {
            if(caller != null) caller.sendMessage(LM.getPlayerMessage(caller.getUsername(),
                    LangKey.PLAYER_NOT_FOUND_MSG, targetUsername));
            return;
        }
        if(isPrefix) profile.clearActivePrefix();
        else profile.clearActiveSuffix();
        if(caller != null) caller.sendMessage(LM.getPlayerMessage(caller.getUsername(),
                LangKey.AFFIX_REMOVE_ALL, targetAffix,targetUsername));
    }
    private void modifyPlayerAffix(PlayerRef caller, String targetUsername, String affixId, AffixType type, boolean remove, boolean forceAdd) {
        PlayerAffix affix = affixMap.get(affixId);
        if (affix == null) {
            if(caller != null) caller.sendMessage(LM.getPlayerMessage(caller.getUsername(),
                    LangKey.AFFIX_NOT_FOUND, affixId));
            return;
        }
        PlayerProfile profile = playerDataManager.getPlayerProfile(targetUsername);
        if (profile == null) {
            if(caller != null) caller.sendMessage(LM.getPlayerMessage(caller.getUsername(),
                    LangKey.PLAYER_NOT_FOUND, targetUsername));
            return;
        }
        boolean isPrefix = (type == AffixType.PREFIX);
        Map<String, String> activeMap = isPrefix ? profile.getActivePrefix() : profile.getActiveSuffix();
        String affixTypeString = isPrefix ? "prefix" : "suffix";
        // Remove First
        if(remove) {
            if(activeMap.containsKey(affixId)) {
                if(isPrefix) profile.removeActivePrefix(affixId);
                else profile.removeActiveSuffix(affixId);
                if(caller != null) caller.sendMessage(LM.getPlayerMessage(caller.getUsername(),
                        LangKey.AFFIX_REMOVE_SUCCESS, affix.getDisplayText(), targetUsername));
            } else {
                if(caller != null) caller.sendMessage(LM.getPlayerMessage(caller.getUsername(),
                        LangKey.AFFIX_INACTIVE, targetUsername,affixTypeString,affix.getDisplayText()));
            }
            return;
        }
        if ((isPrefix && !canHavePrefix(targetUsername)) || (!isPrefix && !canHaveSuffix(targetUsername))) {
            return;
        }
        if (activeMap.containsKey(affixId)) {
            if(caller != null) caller.sendMessage(LM.getPlayerMessage(caller.getUsername(),
                    LangKey.AFFIX_ACTIVE, targetUsername,affixTypeString,affix.getDisplayText()));
            return;
        }
        int maxAllowed = isPrefix ? profile.getMaxPrefix() : profile.getMaxSuffix();
        if(activeMap.size() >= maxAllowed) {
            if(forceAdd) {
                String affixToRemove = findAffixWithLeastPriority(activeMap);
                if (!affixToRemove.isEmpty()) {
                    if (isPrefix) profile.removeActivePrefix(affixToRemove);
                    else profile.removeActiveSuffix(affixToRemove);
                    if (isPrefix) profile.addToActivePrefix(affixId, affix.getDisplayText());
                    else profile.addToActiveSuffix(affixId, affix.getDisplayText());
                    if(caller != null) caller.sendMessage(LM.getPlayerMessage(caller.getUsername(),
                            LangKey.AFFIX_REPLACE, affixMap.get(affixToRemove).getDisplayText(),affix.getDisplayText(), targetUsername));
                }
            } else {
                if(caller != null) caller.sendMessage(LM.getPlayerMessage(caller.getUsername(),
                        LangKey.AFFIX_MAX, affixTypeString,targetUsername, String.valueOf(profile.getMaxSuffix())));
            }
            return;
        }
        if (isPrefix) profile.addToActivePrefix(affixId, affix.getDisplayText());
        else profile.addToActiveSuffix(affixId, affix.getDisplayText());
        if(caller != null) caller.sendMessage(LM.getPlayerMessage(caller.getUsername(), LangKey.AFFIX_ADD_SUCCESS, affix.getDisplayText(), targetUsername));
    }
    public String findAffixWithLeastPriority(Map<String, String> activeAffix) {
        String toRemove = "";
        int priorityNow = Integer.MAX_VALUE;
        for(String affixId : activeAffix.keySet()) {
            PlayerAffix affix = affixMap.get(affixId);
            if(affix != null && affix.getPriority() < priorityNow) {
                toRemove = affixId;
                priorityNow = affix.getPriority();
            }
        }
        return toRemove;
    }
    // Classes and Getter / Setter
    private boolean canHavePrefix(String username) {
        return checkAffixConfig(username, true, false);
    }
    private boolean canHaveSuffix(String username) {
        return checkAffixConfig(username, false, false);
    }
    private boolean checkAffixConfig(String username, boolean isPrefix, boolean forceAdd) {
        if(forceAdd) return true;
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
        for(DefaultAffix affix : DefaultAffix.values()) {
            map.put(affix.getId(), new PlayerAffix(affix.getId(), affix.getText(), affix.getPriority()));
        }
        return map;
    }

    public Map<String, PlayerAffix> getAffixMap() {return affixMap;}
    public String getAffixDisplay(String id) {
        if(affixMap.containsKey(id)) {
            return affixMap.get(id).getDisplayText();
        }
        return "<UNDEFINED>";
    }

    // Classes
    public static class PlayerAffix {
        private String id;
        private String displayText;
        private int priority;
        public PlayerAffix(){}
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
    private static class AffixHolder {
        @SerializedName("VERSION")
        private final String version = AFFIX_VERSION;
        @SerializedName("AFFIX_LIST")
        private List<PlayerAffix> affixList = new ArrayList<>();

        public String getVersion() {return version;}
        public List<PlayerAffix> getAffixList() {return affixList;}
        public void setAffixList(List<PlayerAffix> AFFIX_LIST) {this.affixList = AFFIX_LIST;}
        public void addToAffixList(PlayerAffix affix) { affixList.add(affix);}

        public Map<String, PlayerAffix> getAffixListAsMap() {
            Map<String, PlayerAffix> affixMap = new ConcurrentHashMap<>();
            for(PlayerAffix affix : affixList) {
                affixMap.put(affix.getId(), affix);
            }
            return affixMap;
        }
    }
}
