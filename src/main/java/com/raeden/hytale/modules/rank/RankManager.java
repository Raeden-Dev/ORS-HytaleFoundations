package com.raeden.hytale.modules.rank;

import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.modules.chat.AffixManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.raeden.hytale.HytaleFoundations.RANK_VERSION;

public class RankManager {
    private final HytaleFoundations hytaleFoundations;
    private final Map<String, Rank> RANK_MAP;
    public RankManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        RANK_MAP = new LinkedHashMap<>();
        initializeRankManager();
    }
    private void initializeRankManager() {

    }

    public Map<String, Rank> getRankMap() {return RANK_MAP;}

    public static class Rank {
        private String id;
        private String permissionGroup;
        private String parentRank;
        private String chatPrefix;
        private String chatSuffix;
        private int rankPriority;

        public String getId() {return id;}
        public void setId(String id) {this.id = id;}
        public String getPermissionGroup() {return permissionGroup;}
        public void setPermissionGroup(String permissionGroup) {this.permissionGroup = permissionGroup;}
        public String getParentRank() {return parentRank;}
        public void setParentRank(String parentRank) {this.parentRank = parentRank;}
        public String getChatPrefix() {return chatPrefix;}
        public void setChatPrefix(String chatPrefix) {this.chatPrefix = chatPrefix;}
        public String getChatSuffix() {return chatSuffix;}
        public void setChatSuffix(String chatSuffix) {this.chatSuffix = chatSuffix;}
        public int getRankPriority() {return rankPriority;}
        public void setRankPriority(int rankPriority) {this.rankPriority = rankPriority;}
    }
    public static class RankHolder {
        private final String VERSION = RANK_VERSION;
        private List<Rank> RANK_LIST = new ArrayList<>();

        public List<Rank> getRankList() {return RANK_LIST;}
        public void setRankList(List<Rank> RANK_LIST) {this.RANK_LIST = RANK_LIST;}
        public void addRank(Rank rank) {this.RANK_LIST.add(rank);}
        public void removeRank(Rank rank) {
            Rank toRemove = null;
            for(Rank i : RANK_LIST) {
                if(rank.getId().equals(i.getId())) {
                    toRemove = i;
                }
            }
            RANK_LIST.remove(toRemove);
        }
        public Map<String, Rank> getRankListAsMap() {
            Map<String, Rank> rankMap = new ConcurrentHashMap<>();
            for(Rank rank : RANK_LIST) {
                rankMap.put(rank.getId(), rank);
            }
            return rankMap;
        }

        public String getVersion() {return VERSION;}
    }
}
