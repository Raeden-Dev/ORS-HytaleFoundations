package com.raeden.hytale.core.player;

import com.google.gson.annotations.SerializedName;

public class PlayerStats {
    @SerializedName("FIRST_JOINED")
    private long firstJoined;
    @SerializedName("LAST_JOINED")
    private long lastJoined;
    @SerializedName("PLAY_TIME_MILLIS")
    private long playTimeMillis;

    // Stats
    @SerializedName("COLLECT_STATS")
    private boolean collectStats;
    @SerializedName("TOTAL_DEATHS")
    private int totalDeaths;
    @SerializedName("PLAYER_KILLS")
    private int playerKills;
    @SerializedName("MOB_KILLS")
    private int mobKills;
    @SerializedName("PVE_DAMAGE_TAKEN")
    private int damageTakenPve;
    @SerializedName("PVE_DAMAGE_GIVEN")
    private int damageGivenPve;
    @SerializedName("PVP_DAMAGE_TAKEN")
    private int damageTakenPvp;
    @SerializedName("PVP_DAMAGE_GIVEN")
    private int damageGivenPvp;
    @SerializedName("BLOCKS_BROKEN")
    private int blocksBroken;
    @SerializedName("BLOCKS_PLACED")
    private int blocksPlaced;
    @SerializedName("DISTANCE_WALKED")
    private double distanceWalked;
    @SerializedName("ITEMS_CRAFTED")
    private int itemsCrafted;
    @SerializedName("ITEMS_BROKEN")
    private int itemsBroken;

    // Chat
    @SerializedName("TOTAL_MESSAGES_SENT")
    private long totalMessagesSent;
    @SerializedName("TOTAL_TIMES_REPORTED_BY_PLAYERS")
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

    public int getDamageTakenPvp() {return damageTakenPvp;}
    public void setDamageTakenPvp(int damageTakenPvp) {this.damageTakenPvp = damageTakenPvp;}
    public void addDamageTakenPvp(int damageTakenPvp) {this.damageTakenPvp += damageTakenPvp;}

    public int getDamageGivenPvp() {return damageGivenPvp;}
    public void setDamageGivenPvp(int damageGivenPvp) {this.damageGivenPvp = damageGivenPvp;}
    public void addDamageGivenPvp(int damageGivenPvp) {this.damageGivenPvp += damageGivenPvp;}
}
