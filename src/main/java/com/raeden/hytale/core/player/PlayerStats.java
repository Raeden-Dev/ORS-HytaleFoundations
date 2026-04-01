package com.raeden.hytale.core.player;

import com.google.gson.annotations.SerializedName;

public class PlayerStats {
    @SerializedName("first_joined")
    private long firstJoined;
    @SerializedName("last_joined")
    private long lastJoined;
    @SerializedName("play_time_millis")
    private long playTimeMillis;

    @SerializedName("collect_stats")
    private boolean collectStats;
    @SerializedName("total_deaths")
    private int totalDeaths;
    @SerializedName("player_kills")
    private int playerKills;
    @SerializedName("mob_kills")
    private int mobKills;
    @SerializedName("pve_damage_taken")
    private int damageTakenPve;
    @SerializedName("pve_damage_given")
    private int damageGivenPve;
    @SerializedName("pvp_damage_taken")
    private int damageTakenPvp;
    @SerializedName("pvp_damage_given")
    private int damageGivenPvp;
    @SerializedName("blocks_broken")
    private int blocksBroken;
    @SerializedName("blocks_placed")
    private int blocksPlaced;
    @SerializedName("distance_walked")
    private double distanceWalked;
    @SerializedName("items_crafted")
    private int itemsCrafted;
    @SerializedName("items_broken")
    private int itemsBroken;

    @SerializedName("total_messages_sent")
    private long totalMessagesSent;
    @SerializedName("total_times_reported_by_players")
    private int totalTimesReportedByPlayers;

    public long getFirstJoined() {return firstJoined;}
    public void setFirstJoined(long firstJoined) {this.firstJoined = firstJoined;}

    public long getLastJoined() {return lastJoined;}
    public void setLastJoined(long lastJoined) {this.lastJoined = lastJoined;}

    public long getPlayTimeMillis() {return playTimeMillis;}
    public void setPlayTimeMillis(long playTimeMillis) {this.playTimeMillis = playTimeMillis;}

    public boolean isCollectStats() {return collectStats;}
    public void setCollectStats(boolean collectStats) {this.collectStats = collectStats;}

    public int getTotalDeaths() {return totalDeaths;}
    public void addDeath() { this.totalDeaths++;}
    public void setTotalDeaths(int totalDeaths) {this.totalDeaths = totalDeaths;}

    public int getPlayerKills() {return playerKills;}
    public void addPlayerKill() {this.playerKills++;}
    public void setPlayerKills(int playerKills) {this.playerKills = playerKills;}

    public int getMobKills() {return mobKills;}
    public void addMobKill() {this.mobKills++;}
    public void setMobKills(int mobKills) {this.mobKills = mobKills;}

    public int getDamageTakenPve() {return damageTakenPve;}
    public void setDamageTakenPve(int damageTakenPve) {this.damageTakenPve = damageTakenPve;}
    public void addDamageTakenPve(int damageTakenPve) {this.damageTakenPve += damageTakenPve;}

    public int getDamageGivenPve() {return damageGivenPve;}
    public void setDamageGivenPve(int damageGivenPve) {this.damageGivenPve = damageGivenPve;}
    public void addDamageGivenPve(int damageGivenPve) {this.damageGivenPve += damageGivenPve;}

    public int getDamageTakenPvp() {return damageTakenPvp;}
    public void setDamageTakenPvp(int damageTakenPvp) {this.damageTakenPvp = damageTakenPvp;}
    public void addDamageTakenPvp(int damageTakenPvp) {this.damageTakenPvp += damageTakenPvp;}

    public int getDamageGivenPvp() {return damageGivenPvp;}
    public void setDamageGivenPvp(int damageGivenPvp) {this.damageGivenPvp = damageGivenPvp;}
    public void addDamageGivenPvp(int damageGivenPvp) {this.damageGivenPvp += damageGivenPvp;}

    public int getBlocksBroken() {return blocksBroken;}
    public void addBlockBreak() {this.blocksBroken++;}
    public void setBlocksBroken(int blocksBroken) {this.blocksBroken = blocksBroken;}

    public int getBlocksPlaced() {return blocksPlaced;}
    public void addBlockPlace() {this.blocksPlaced++;}
    public void setBlocksPlaced(int blocksPlaced) {this.blocksPlaced = blocksPlaced;}

    public double getDistanceWalked() {return distanceWalked;}
    public void setDistanceWalked(double distanceWalked) {this.distanceWalked = distanceWalked;}
    public void addDistanceWalked(double distance){this.distanceWalked += distance;}

    public int getItemsCrafted() {return itemsCrafted;}
    public void addItemCraft() {itemsCrafted++;}
    public void setItemsCrafted(int itemsCrafted) {this.itemsCrafted = itemsCrafted;}

    public int getItemsBroken() {return itemsBroken;}
    public void addItemBroken() {itemsBroken++;}
    public void setItemsBroken(int itemsBroken) {this.itemsBroken = itemsBroken;}

    public long getTotalMessagesSent() {return totalMessagesSent;}
    public void increaseMessageSent() {totalMessagesSent++;}
    public void setTotalMessagesSent(long totalMessagesSent) {this.totalMessagesSent = totalMessagesSent;}

    public int getTotalTimesReportedByPlayers() {return totalTimesReportedByPlayers;}
    public void setTotalTimesReportedByPlayers(int totalTimesReportedByPlayers) {this.totalTimesReportedByPlayers = totalTimesReportedByPlayers;}
}