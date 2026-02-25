package com.raeden.hytale.core.config.containers;

import com.google.gson.annotations.SerializedName;
import com.raeden.hytale.modules.rank.RankManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.raeden.hytale.core.config.ConfigManager.RANK_VERSION;

public class RankConfig {
    @SerializedName("VERSION")
    private final String version = RANK_VERSION;
    @SerializedName("FORCE_ADD_AFFIX")
    private boolean forceAddAffix;
    @SerializedName("RANK_LIST")
    private List<RankManager.Rank> rankList = new ArrayList<>();

    public List<RankManager.Rank> getRankList() {return rankList;}
    public void setRankList(List<RankManager.Rank> RANK_LIST) {this.rankList = RANK_LIST;}
    public void addRank(RankManager.Rank rank) {this.rankList.add(rank);}
    public void removeRank(RankManager.Rank rank) {
        rankList.removeIf(i -> i.getId().equals(rank.getId()));
    }
    public Map<String, RankManager.Rank> getRankListAsMap() {
        Map<String, RankManager.Rank> rankMap = new ConcurrentHashMap<>();
        for(RankManager.Rank rank : rankList) {
            rankMap.put(rank.getId(), rank);
        }
        return rankMap;
    }

    public String getVersion() {return version;}
}
