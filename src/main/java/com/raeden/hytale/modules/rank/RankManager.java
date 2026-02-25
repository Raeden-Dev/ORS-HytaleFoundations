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
    private final Map<String, Rank> rankMap;

    private final String rankFileName = RANK_FILENAME;
    private final Path rankFilePath;

    public RankManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        rankMap = new ConcurrentHashMap<>();
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
        RankHolder rankHolder = new RankHolder();
        rankHolder.rankList = new ArrayList<>(rankMap.values());
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

            if (newRanks > 0) {
                myLogger.atInfo().log(langManager.getMessage(LangKey.LOAD_SUCCESS, true, newRanks + " ranks").getAnsiMessage());
            }
        } else {
            saveRankFile();
        }
    }
    // Player Interaction
    public void setRank(PlayerRef sender, String targetUsername, String rankId) {
        Rank rank = getRank(rankId);
        if(rank == null) {
            if(sender != null) {
                sender.sendMessage(langManager.getMessage(sender.getUsername(), LangKey.RANK_NOT_FOUND, false, rankId));
            }
            return;
        }
        PlayerProfile profile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(targetUsername);
        if(profile == null) {
           sender.sendMessage(langManager.getMessage(sender.getUsername(), LangKey.PLAYER_NOT_FOUND_MSG,false, targetUsername));
            return;
        }
        profile.setRankId(rankId);
        if(rank.getChatPrefixId() != null && !rank.getChatPrefixId().isEmpty()) {
            hytaleFoundations.getChatManager().getAffixManager().addPrefixToPlayer(sender, targetUsername, rank.getChatPrefixId(), true);
        }
        if(rank.getChatSuffixId() != null && !rank.getChatSuffixId().isEmpty()) {
            hytaleFoundations.getChatManager().getAffixManager().addSuffixToPlayer(sender, targetUsername, rank.getChatSuffixId(), true);
        }
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
}
