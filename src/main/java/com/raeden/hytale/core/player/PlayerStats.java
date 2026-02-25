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
    @SerializedName("TOTAL_DEATHS")
    private int totalDeaths;
    @SerializedName("PLAYER_KILLS")
    private int playerKills;
    @SerializedName("MOB_KILLS")
    private int mobKills;
    @SerializedName("DAMAGE_TAKEN")
    private int damageTaken;
    @SerializedName("DAMAGE_GIVEN")
    private int damageGiven;
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

    public int getTotalDeaths() {return totalDeaths;}
    public void addDeath() { this.totalDeaths++;}
    public void setTotalDeaths(int totalDeaths) {this.totalDeaths = totalDeaths;}

    public int getPlayerKills() {return playerKills;}
    public void addPlayerKill() {this.playerKills++;}
    public void setPlayerKills(int playerKills) {this.playerKills = playerKills;}

    public int getMobKills() {return mobKills;}
    public void addMobKill() {this.mobKills++;}
    public void setMobKills(int mobKills) {this.mobKills = mobKills;}

    public int getDamageTaken() {return damageTaken;}
    public void setDamageTaken(int damageTaken) {this.damageTaken = damageTaken;}
    public int getDamageGiven() {return damageGiven;}
    public void setDamageGiven(int damageGiven) {this.damageGiven = damageGiven;}

    public int getBlocksBroken() {return blocksBroken;}
    public void addBlockBreak() {this.blocksBroken++;}
    public void setBlocksBroken(int blocksBroken) {this.blocksBroken = blocksBroken;}

    public int getBlocksPlaced() {return blocksPlaced;}
    public void addBlockPlace() {this.blocksPlaced++;}
    public void setBlocksPlaced(int blocksPlaced) {this.blocksPlaced = blocksPlaced;}

    public double getDistanceWalked() {return distanceWalked;}
    public void setDistanceWalked(double distanceWalked) {this.distanceWalked = distanceWalked;}

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
