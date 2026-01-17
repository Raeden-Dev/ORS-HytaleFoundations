package com.raeden.hytale.core.data;

import com.hypixel.hytale.math.vector.Location;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.raeden.hytale.modules.admin.PlayerOffence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerMetaData {
    private UUIDComponent UUID;
    private List<String> username;
    private String nickname;

    private long firstJoined;
    private long lastJoined;
    private long playTimeMillis;

    private HashMap<String, Double> balances = new HashMap<>();

    private int totalDeaths;
    private int playerKills;
    private int mobKills;
    private int blocksBroken;
    private int blocksPlaced;
    private double distanceWalked;

    private int itemsCrafted;
    private int itemsBroken;

    private boolean godModeEnabled;
    private boolean isVanished;
    private boolean isFlying;

    private final Map<String, Location> homes = new HashMap<>();
    private boolean isMuted;
    private List<PlayerOffence> offences;


    public UUIDComponent getUUID() {return UUID;}
    public void setUUID(UUIDComponent UUID) {this.UUID = UUID;}

    public List<String> getUsername() {return username;}
    public void addUsername(String name) {username.add(name);}
    public void setUsername(List<String> username) {this.username = username;}

    public String getNickname() {return nickname;}
    public void setNickname(String nickname) {this.nickname = nickname;}

    public long getFirstJoined() {return firstJoined;}
    public void setFirstJoined(long firstJoined) {this.firstJoined = firstJoined;}
    public long getLastJoined() {return lastJoined;}
    public void setLastJoined(long lastJoined) {this.lastJoined = lastJoined;}
    public long getPlayTimeMillis() {return playTimeMillis;}
    public void setPlayTimeMillis(long playTimeMillis) {this.playTimeMillis = playTimeMillis;}


    public double getCurrencyBalance(String currency) {return balances.get(currency);}
    public HashMap<String, Double> getBalances() {return balances;}
    public void setCurrencyBalance(String currency, double balance) {this.balances.put(currency, balance);}
    public void setBalances(HashMap<String, Double> balances) {this.balances = balances;}

    public int getTotalDeaths() {return totalDeaths;}
    public void addDeath() { this.totalDeaths++;}
    public void setTotalDeaths(int totalDeaths) {this.totalDeaths = totalDeaths;}

    public int getPlayerKills() {return playerKills;}
    public void addPlayerKill() {this.playerKills++;}
    public void setPlayerKills(int playerKills) {this.playerKills = playerKills;}

    public int getMobKills() {return mobKills;}
    public void addMobKill() {this.mobKills++;}
    public void setMobKills(int mobKills) {this.mobKills = mobKills;}

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

    public boolean isGodModeEnabled() {return godModeEnabled;}
    public void setGodModeEnabled(boolean godModeEnabled) {this.godModeEnabled = godModeEnabled;}

    public boolean isVanished() {return isVanished;}
    public void setVanished(boolean vanished) {isVanished = vanished;}
    public boolean isFlying() {return isFlying;}
    public void setFlying(boolean flying) {isFlying = flying;}

    public void addHome(String name, Location location) {homes.put(name, location);}
    public void removeHome(String name) {homes.remove(name);}
    public Map<String, Location> getHomes() {return homes;}

    public boolean isMuted() {return isMuted;}

    public void setMuted(boolean muted) {isMuted = muted;}

    public List<PlayerOffence> getOffences() {return offences;}
    public void addOffence(PlayerOffence offence) {offences.add(offence);}
    public void setOffences(List<PlayerOffence> offences) {this.offences = offences;}
}
