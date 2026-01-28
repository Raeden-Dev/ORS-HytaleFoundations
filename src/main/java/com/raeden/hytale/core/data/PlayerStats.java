package com.raeden.hytale.core.data;

public class PlayerStats {
    // playtime
    private long firstJoined;
    private long lastJoined;
    private long playTimeMillis;

    // Stats
    private int totalDeaths;
    private int playerKills;
    private int mobKills;
    private int damageTaken;
    private int damageGiven;
    private int blocksBroken;
    private int blocksPlaced;
    private double distanceWalked;

    private int itemsCrafted;
    private int itemsBroken;

    // Chat
    private long totalMessagesSent;

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

}
