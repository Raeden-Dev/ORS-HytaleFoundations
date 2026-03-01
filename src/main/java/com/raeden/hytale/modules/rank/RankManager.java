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

    private RankHolder rankHolder;

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
            myLogger.atInfo().log(LM.getMessage(LangKey.CREATE_SUCCESS, true, rankFileName, rankFilePath.toString()).getAnsiMessage());
            saveDefaultRankFile();
        }
    }
    private void saveDefaultRankFile() {
        rankHolder = new RankHolder();
        rankHolder.setForceAddAffix(true);
        rankHolder.setSwitchGroupOnCreate(true);
        rankHolder.setRankList(new ArrayList<>(rankMap.values()));
        rankHolder.getRankGroups().putAll(rankGroupMap);
        saveJsonFile(rankFileName, rankFilePath, rankHolder, true);
    }
    public void saveRankFile() {
        rankHolder = new RankHolder();
        rankHolder.setForceAddAffix(rankHolder.isForceAddAffix());
        rankHolder.setRankList(new ArrayList<>(rankMap.values()));
        rankHolder.getRankGroups().putAll(rankGroupMap);
        saveJsonFile(rankFileName, rankFilePath, rankHolder, false);
    }
    public void loadRanks() {
        Type type = new TypeToken<RankHolder>(){}.getType();
        RankHolder loadedRankHolder = loadJsonFile(rankFileName, rankFilePath, type, true);
        if(loadedRankHolder != null && loadedRankHolder.rankList != null) {
            int newRanks = 0;
            for (Rank rank : loadedRankHolder.rankList) {
                if (!rankMap.containsKey(rank.id)) {
                    newRanks++;
                }
                rankMap.put(rank.id, rank);
            }
            int newChains = 0;
            for(Map.Entry<String, List<String>> chain : loadedRankHolder.rankGroups.entrySet()) {
                if(!rankGroupMap.containsKey(chain.getKey())) {
                    newChains++;
                }
                rankGroupMap.put(chain.getKey(), chain.getValue());
                for(String rankId : chain.getValue()) {
                    Rank rank = rankMap.get(rankId);
                    if(rank != null) rank.setRankGroup(chain.getKey());
                }
            }
            if (newRanks > 0)  myLogger.atInfo().log(LM.getMessage(LangKey.LOAD_SUCCESS, true, newRanks + " rank(s)").getAnsiMessage());
            if (newChains > 0)  myLogger.atInfo().log(LM.getMessage(LangKey.LOAD_SUCCESS, true, newChains + " rank group(s)").getAnsiMessage());
            rankHolder = loadedRankHolder;
        } else {
            saveDefaultRankFile();
        }
    }
    // Player Interaction
    public void setRankOfTarget(String targetUsername, String rankId) {
        setRankOfTarget(null, targetUsername, rankId);
    }
    public void setRankOfTarget(PlayerRef sender, String targetUsername, String rankId) {
        Rank rank = getRank(rankId);
        if(rank == null) {
            if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_NOT_FOUND, false, rankId));
            return;
        }
        PlayerProfile profile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(targetUsername);
        if(profile == null) {
            if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.PLAYER_NOT_FOUND_MSG,false, targetUsername));
            return;
        }
        String oldRankId = profile.getRankId();
        Rank oldRank = (oldRankId != null) ? getRank(oldRankId) : null;
        profile.setRankId(rankId);
        if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_SET_SUCCESS,false, hytaleFoundations.getChatManager().getAffixManager().getAffixDisplay(rank.getChatPrefixId()), targetUsername));
        // Affix Handling
        if(oldRank != null) {
            if(oldRank.getChatPrefixId() != null && !oldRank.getChatPrefixId().isEmpty()) {
                hytaleFoundations.getChatManager().getAffixManager().removePrefixFromPlayer(targetUsername, oldRank.getChatPrefixId());
            }
            if(oldRank.getChatSuffixId() != null && !oldRank.getChatSuffixId().isEmpty()) {
                hytaleFoundations.getChatManager().getAffixManager().removeSuffixFromPlayer(targetUsername, oldRank.getChatSuffixId());
            }
        }
        if(rank.getChatPrefixId() != null && !rank.getChatPrefixId().isEmpty()) {
            hytaleFoundations.getChatManager().getAffixManager().addPrefixToPlayer(null, targetUsername, rank.getChatPrefixId(), true);
        }
        if(rank.getChatSuffixId() != null && !rank.getChatSuffixId().isEmpty()) {
            hytaleFoundations.getChatManager().getAffixManager().addSuffixToPlayer(null, targetUsername, rank.getChatSuffixId(), true);
        }
    }
    public void createRankGroup(String groupName, List<String> rankIds) {
        createRankGroup(null, groupName, rankIds);
    }
    public void createRankGroup(PlayerRef sender, String groupName, List<String> rankIds) {
        if(rankGroupMap.containsKey(groupName)) {
            if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_GROUP_EXISTS, false, groupName));
            return;
        }
        List<String> rankChain = new ArrayList<>();
        for(String id : rankIds) {
            String cleanId = id.replaceAll("\\s+", "");
            if(!rankMap.containsKey(cleanId)) {
                if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_GROUP_IGNORE_RANK, false, id));
                continue;
            }
            Rank rank = rankMap.get(cleanId);
            if(rank.getRankGroup() != null && !rank.getRankGroup().isEmpty()) {
                if(rankHolder.isSwitchGroupOnCreate()) {
                    removeRankFromGroup(null, rank.getRankGroup(), rank.getId());
                    if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_SWITCH_GROUP, false, cleanId, rank.getRankGroup(), groupName));
                } else {
                    if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_GROUP_BELONGS, false, cleanId, rank.getRankGroup()));
                    continue;
                }
            }
            rank.setRankGroup(groupName);
            rankChain.add(id);
        }
        rankGroupMap.put(groupName, rankChain);
        saveRankFile();
        if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_GROUP_CREATE, false, groupName));
    }

    public void addRankToGroup(PlayerRef sender, String groupName, String rankId, int position) {
        if(!rankGroupMap.containsKey(groupName)) {
            if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_GROUP_NOT_FOUND, false, groupName));
            return;
        }
        if(!rankMap.containsKey(rankId)) {
            if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_NOT_FOUND, false, rankId));
            return;
        }
        Rank rank = rankMap.get(rankId);
        rank.setRankGroup(groupName);
        List<String> groupMap = rankGroupMap.get(groupName);
        int groupSize = groupMap.size();
        if(position > groupSize) {
            position = groupSize;
        }
        if(position < 0) {
            position = 0;
        }
        groupMap.add(position, rankId);
        rankGroupMap.put(groupName, groupMap);
        saveRankFile();
        if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_GROUP_APPEND, false, rankId, groupName));
    }
    public void removeRankFromGroup(PlayerRef sender, String groupName, String rankId) {
        if(!rankGroupMap.containsKey(groupName)) {
            if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_GROUP_NOT_FOUND, false, groupName));
            return;
        }
        if(!rankMap.containsKey(rankId)) {
            if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_NOT_FOUND, false, rankId));
            return;
        }
        Rank rank = rankMap.get(rankId);
        rank.setRankGroup(null);
        List<String> groupMap = rankGroupMap.get(groupName);
        if(!groupMap.contains(rankId)) {
            if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_GROUP_REMOVE_NF, false, rankId));
            return;
        }
        groupMap.remove(rankId);
        rankGroupMap.put(groupName, groupMap);
        saveRankFile();
        if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_GROUP_REMOVE, false, rankId, groupName));
    }
    public void moveRankInGroup(PlayerRef sender, String groupName, String rankId, int position) {
        if(!rankGroupMap.containsKey(groupName)) {
            if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_GROUP_NOT_FOUND, false, groupName));
            return;
        }
        if(!rankMap.containsKey(rankId)) {
            if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_NOT_FOUND, false, rankId));
            return;
        }
        List<String> groupMap = rankGroupMap.get(groupName);

        if(!groupMap.contains(rankId)) {
            if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_GROUP_REMOVE_NF, false, rankId));
            return;
        }
        int oldPos = groupMap.indexOf(rankId);
        groupMap.remove(rankId);
        int groupSize = groupMap.size();
        if(position > groupSize) position = groupSize;
        else if(position < 0) position = 0;
        groupMap.add(position, rankId);
        rankGroupMap.put(groupName, groupMap);
        saveRankFile();
        if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_GROUP_MOVE, false, rankId, String.valueOf(oldPos), String.valueOf(position),  groupName));
    }
    public void showRankChain(PlayerRef sender, String groupName) {
        if(!rankGroupMap.containsKey(groupName)) {
            if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_GROUP_NOT_FOUND, false, groupName));
            return;
        }
        if(sender != null) {
            sender.sendMessage(LM.getPlayerMessage(sender.getUsername(), LangKey.GENERAL_LIST, "rank(s) in group"));
            sender.sendMessage(LM.getPlayerMessage(sender.getUsername(), LangKey.GENERAL_LIST_ITEM, "&l" + getRankChainText(groupName)));
        }
    }
    public void deleteRankGroup(PlayerRef sender, String groupName) {
        if(!rankGroupMap.containsKey(groupName)) {
            if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_GROUP_NOT_FOUND, false, groupName));
            return;
        }
        List<String> rankList = rankGroupMap.get(groupName);
        for(String rankId : rankList) {
            Rank rank = getRank(rankId);
            rank.setRankGroup(null);
        }
        rankGroupMap.remove(groupName);
        saveRankFile();
        if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_GROUP_DELETE, false, groupName));
    }
    public void promotePlayer(PlayerRef sender, String targetUsername) {
        PlayerProfile profile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(targetUsername);
        if(profile == null) {
            if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.PLAYER_NOT_FOUND_MSG,false, targetUsername));
            return;
        }
        String rankId = profile.getRankId();
        if(rankId == null || rankId.isEmpty()) {
            if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_NOT_SET,false, targetUsername));
            return;
        }
        if(!rankMap.containsKey(profile.getRankId())) {
            if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_NOT_FOUND, false, profile.getRankId()));
            return;
        }
        Rank rank = rankMap.get(profile.getRankId());
        if(rank.getRankGroup() == null || rank.getRankGroup().isEmpty()) {
            if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_INDEPENDENT, false));
            return;
        }
        List<String> rankChain = rankGroupMap.get(rank.getRankGroup());
        if(rankChain == null) return;
        int rankIdx = rankChain.indexOf(rank.getId());
        if(rankIdx ==  rankChain.size() - 1) {
            if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_PROMOTE_CAP, false, targetUsername));
            return;
        }
        String nextRankId = rankChain.get(rankIdx + 1);
        setRankOfTarget(targetUsername, nextRankId);
        if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_PROMOTE, false, targetUsername, rank.getId(), nextRankId));
    }
    public void demotePlayer(PlayerRef sender, String targetUsername) {
        PlayerProfile profile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(targetUsername);
        if(profile == null) {
            if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.PLAYER_NOT_FOUND_MSG,false, targetUsername));
            return;
        }
        String rankId = profile.getRankId();
        if(rankId == null || rankId.isEmpty()) {
            if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_NOT_SET,false, targetUsername));
            return;
        }
        if(!rankMap.containsKey(profile.getRankId())) {
            if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_NOT_FOUND, false, profile.getRankId()));
            return;
        }
        Rank rank = rankMap.get(profile.getRankId());
        if(rank.getRankGroup() == null || rank.getRankGroup().isEmpty()) {
            if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_INDEPENDENT, false));
            return;
        }
        List<String> rankChain = rankGroupMap.get(rank.getRankGroup());
        if(rankChain == null) return;
        int rankIdx = rankChain.indexOf(rank.getId());
        if(rankIdx ==  0) {
            if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_DEMOTE_CAP, false, targetUsername));
            return;
        }
        String nextRankId = rankChain.get(rankIdx - 1);
        setRankOfTarget(targetUsername, nextRankId);
        if(sender != null) sender.sendMessage(LM.getMessage(sender.getUsername(), LangKey.RANK_DEMOTE, false, targetUsername, rank.getId(), nextRankId));
    }

    // Getters And Setters
    public String getRankChainText(String groupName) {
        if(!rankGroupMap.containsKey(groupName)) {
            return "<NOT FOUND>";
        }
        List<String> rankChain = rankGroupMap.get(groupName);
        return String.join(" > ", rankChain);
    }
    public Rank getRank(String rankId) {return rankMap.get(rankId);}
    public Map<String, Rank> getRankMap() {return rankMap;}
    public Map<String, List<String>> getRankGroupMap() {return rankGroupMap;}
    private Map<String, Rank> getDefaultRankMap() {
        Map<String, Rank> map = new ConcurrentHashMap<>();
        for(DefaultRank rank : DefaultRank.values()) {
            map.put(rank.getId(), new Rank(rank.getId(), rank.getPermissionGroup(), rank.getRankGroup(), rank.getChatPrefixId(), rank.getChatSuffixId(), rank.getRankPriority()));
        }
        return map;
    }

    // Classes
    public static class Rank {
        @SerializedName("rank_id")
        private String id;
        @SerializedName("rank_permission_group")
        private String permissionGroup;
        @SerializedName("rank_group")
        private String rankGroup;
        @SerializedName("chat_prefix_id")
        private String chatPrefixId;
        @SerializedName("chat_suffix_id")
        private String chatSuffixId;
        @SerializedName("rank_priority")
        private int rankPriority;

        public Rank() {}
        public Rank(String id, String permissionGroup, String rankGroup, String chatPrefixId, String chatSuffixId, int rankPriority) {
            this.id = id;
            this.permissionGroup = permissionGroup;
            this.rankGroup = rankGroup;
            this.chatPrefixId = chatPrefixId;
            this.chatSuffixId = chatSuffixId;
            this.rankPriority = rankPriority;
        }
        public String getId() {return id;}
        public void setId(String newId) {this.id = newId;}
        public String getPermissionGroup() {return permissionGroup;}
        public void setPermissionGroup(String permissionGroup) {this.permissionGroup = permissionGroup;}
        public String getRankGroup() {return rankGroup;}
        public void setRankGroup(String rankGroup) {this.rankGroup = rankGroup;}
        public String getChatPrefixId() {return chatPrefixId;}
        public void setChatPrefixId(String chatPrefixId) {this.chatPrefixId = chatPrefixId;}
        public String getChatSuffixId() {return chatSuffixId;}
        public void setChatSuffixId(String chatSuffixId) {this.chatSuffixId = chatSuffixId;}
        public int getRankPriority() {return rankPriority;}
        public void setRankPriority(int rankPriority) {this.rankPriority = rankPriority;}
    }
    public static class RankHolder {
        @SerializedName("VERSION")
        private final String version = RANK_VERSION;
        @SerializedName("FORCE_ADD_AFFIX")
        private boolean forceAddAffix;
        @SerializedName("ALLOW_AFFIX_REPLACEMENT")
        private boolean allowAffixReplacement;
        @SerializedName("ALLOW_RANK_GROUP_SWITCHING")
        private boolean switchGroupOnCreate;
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

        public boolean isSwitchGroupOnCreate() {return switchGroupOnCreate;}
        public void setSwitchGroupOnCreate(boolean switchGroupOnCreate) {this.switchGroupOnCreate = switchGroupOnCreate;}
    }
}
