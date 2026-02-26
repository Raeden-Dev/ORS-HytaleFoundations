package com.raeden.hytale.modules.rank;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.player.PlayerProfile;
import com.raeden.hytale.lang.LangKey;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.raeden.hytale.HytaleFoundations.*;
import static com.raeden.hytale.core.config.ConfigManager.RANK_FILENAME;
import static com.raeden.hytale.core.config.ConfigManager.RANK_VERSION;
import static com.raeden.hytale.utils.FileManager.loadJsonFile;
import static com.raeden.hytale.utils.FileManager.saveJsonFile;

public class RankManager {
    private final HytaleFoundations hytaleFoundations;
    private final Map<String, List<String>> rankGroupMap;
    private final Map<String, Rank> rankMap;

    private final String rankFileName = RANK_FILENAME;
    private final Path rankFilePath;

    public RankManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        rankMap = new ConcurrentHashMap<>();
        rankGroupMap = new ConcurrentHashMap<>();
        rankFilePath = hytaleFoundations.getDataDirectory().resolve(rankFileName);

        initializeRankManager();
    }
    // Initialization and Loading
    private void initializeRankManager() {
        rankMap.putAll(getDefaultRankMap());
        if(Files.exists(rankFilePath)) {
            loadRanks();
        } else {
            myLogger.atInfo().log(langManager.getMessage(LangKey.CREATE_SUCCESS, true, rankFileName, rankFilePath.toString()).getAnsiMessage());
            saveRankFile();
        }
    }
    private void saveRankFile() {
        RankHolder rankHolder = getCurrentRankFile();
        if(rankHolder == null) rankHolder = new RankHolder();
        rankHolder.setForceAddAffix(rankHolder.isForceAddAffix());
        rankHolder.setRankList(new ArrayList<>(rankMap.values()));
        rankHolder.getRankGroups().putAll(rankGroupMap);
        saveJsonFile(rankFileName, rankFilePath, rankHolder, true);
    }
    public void loadRanks() {
        Type type = new TypeToken<RankHolder>(){}.getType();
        RankHolder rankHolder = loadJsonFile(rankFileName, rankFilePath, type, true);
        if(rankHolder != null && rankHolder.rankList != null) {
            int newRanks = 0;
            for (Rank rank : rankHolder.rankList) {
                if (!rankMap.containsKey(rank.id)) {
                    newRanks++;
                }
                rankMap.put(rank.id, rank);
            }
            if (newRanks > 0)  myLogger.atInfo().log(langManager.getMessage(LangKey.LOAD_SUCCESS, true, newRanks + " rank(s)").getAnsiMessage());

            int newChains = 0;
            for(Map.Entry<String, List<String>> chain : rankHolder.rankGroups.entrySet()) {
                if(!rankGroupMap.containsKey(chain.getKey())) {
                    newChains++;
                }
                rankGroupMap.put(chain.getKey(), chain.getValue());
            }
            if (newChains > 0)  myLogger.atInfo().log(langManager.getMessage(LangKey.LOAD_SUCCESS, true, newChains + " rank group(s)").getAnsiMessage());
        } else {
            saveRankFile();
        }
    }
    private RankHolder getCurrentRankFile() {
        Type type = new TypeToken<RankHolder>(){}.getType();
        return loadJsonFile(rankFileName, rankFilePath, type, false);
    }
    // Player Interaction
    public void setRankOfTarget(String targetUsername, String rankId) {
        setRankOfTarget(null, targetUsername, rankId);
    }
    public void setRankOfTarget(PlayerRef sender, String targetUsername, String rankId) {
        Rank rank = getRank(rankId);
        if(rank == null) {
            if(sender != null) sender.sendMessage(langManager.getMessage(sender.getUsername(), LangKey.RANK_NOT_FOUND, false, rankId));
            return;
        }
        PlayerProfile profile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(targetUsername);
        if(profile == null) {
            if(sender != null) sender.sendMessage(langManager.getMessage(sender.getUsername(), LangKey.PLAYER_NOT_FOUND_MSG,false, targetUsername));
            return;
        }
        profile.setRankId(rankId);
        if(sender != null) sender.sendMessage(langManager.getMessage(sender.getUsername(), LangKey.RANK_SET_SUCCESS,false, targetUsername, hytaleFoundations.getChatManager().getAffixManager().getAffixDisplay(rank.getChatPrefixId())));
        if(rank.getChatPrefixId() != null && !rank.getChatPrefixId().isEmpty()) {
            hytaleFoundations.getChatManager().getAffixManager().addPrefixToPlayer(sender, targetUsername, rank.getChatPrefixId(), true);
        }
        if(rank.getChatSuffixId() != null && !rank.getChatSuffixId().isEmpty()) {
            hytaleFoundations.getChatManager().getAffixManager().addSuffixToPlayer(sender, targetUsername, rank.getChatSuffixId(), true);
        }
    }
    public void createRankGroup(String groupName, String... rankIds) {
        createRankGroup(null, groupName, rankIds);
    }
    public void createRankGroup(PlayerRef sender, String groupName, String... ranksIds) {
        Map<String, List<String>> rankGroup = new LinkedHashMap<>();
        List<String> rankChain = new ArrayList<>();
        for(String id : ranksIds) {
            if(!rankMap.containsKey(id)) {
                if(sender != null) sender.sendMessage(langManager.getMessage(sender.getUsername(), LangKey.RANK_GROUP_IGNORE_RANK, false, id));
                continue;
            }
            rankChain.add(id);
        }
        rankGroup.put(groupName, rankChain);
        if(sender != null) sender.sendMessage(langManager.getMessage(sender.getUsername(), LangKey.RANK_GROUP_CREATE, false, groupName));
    }
    public void addRankToGroup(PlayerRef sender, String groupName, String rankId, int position) {

    }
    public void removeRankFromGroup(PlayerRef sender, String groupName, String rankId) {

    }
    public void moveRankInGroup(PlayerRef sender, String groupName, String rankId, int position) {

    }
    public void showRankChain(PlayerRef sender, String groupName) {

    }
    public void deleteRankGroup(PlayerRef sender, String groupName) {

    }
    // Getters And Setters
    public Rank getRank(String rankId) {return rankMap.get(rankId);}
    public Map<String, Rank> getRankMap() {return rankMap;}
    private Map<String, Rank> getDefaultRankMap() {
        Map<String, Rank> map = new ConcurrentHashMap<>();
        for(DefaultRank rank : DefaultRank.values()) {
            map.put(rank.getId(), new Rank(rank.getId(), rank.getPermissionGroup(), rank.getRankGroup(), rank.getChatPrefixId(), rank.getChatSuffixId(), rank.getRankPriority()));
        }
        return map;
    }

    // Classes
    public static class Rank {
        private final String id;
        private final String permissionGroup;
        private final String rankGroup;
        private final String chatPrefixId;
        private final String chatSuffixId;
        private final int rankPriority;

        public Rank(String id, String permissionGroup, String rankGroup, String chatPrefixId, String chatSuffixId, int rankPriority) {
            this.id = id;
            this.permissionGroup = permissionGroup;
            this.rankGroup = rankGroup;
            this.chatPrefixId = chatPrefixId;
            this.chatSuffixId = chatSuffixId;
            this.rankPriority = rankPriority;
        }

        public String getId() {return id;}
        public String getPermissionGroup() {return permissionGroup;}
        public String getRankGroup() {return rankGroup;}
        public String getChatPrefixId() {return chatPrefixId;}
        public String getChatSuffixId() {return chatSuffixId;}
        public int getRankPriority() {return rankPriority;}
    }
    public static class RankHolder {
        @SerializedName("VERSION")
        private final String version = RANK_VERSION;
        @SerializedName("FORCE_ADD_AFFIX")
        private boolean forceAddAffix;
        @SerializedName("RANK_GROUPS")
        private final Map<String, List<String>> rankGroups = new ConcurrentHashMap<>();
        @SerializedName("RANK_LIST")
        private List<Rank> rankList = new ArrayList<>();

        public List<Rank> getRankList() {return rankList;}
        public void setRankList(List<Rank> RANK_LIST) {this.rankList = RANK_LIST;}
        public void addRank(Rank rank) {this.rankList.add(rank);}
        public void removeRank(Rank rank) {
            rankList.removeIf(i -> i.getId().equals(rank.getId()));
        }
        public Map<String, Rank> getRankListAsMap() {
            Map<String, Rank> rankMap = new ConcurrentHashMap<>();
            for(Rank rank : rankList) {
                rankMap.put(rank.getId(), rank);
            }
            return rankMap;
        }

        public String getVersion() {return version;}
        public boolean isForceAddAffix() {return forceAddAffix;}
        public void setForceAddAffix(boolean forceAddAffix) {this.forceAddAffix = forceAddAffix;}

        public Map<String, List<String>> getRankGroups() {return rankGroups;}
        public void addRankGroup(String groupName, List<String> rankList) {rankGroups.put(groupName, rankList);}
        public void removeRankGroup(String groupName) { rankGroups.remove(groupName);}

    }
}
