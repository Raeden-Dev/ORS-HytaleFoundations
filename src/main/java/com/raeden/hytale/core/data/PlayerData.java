package com.raeden.hytale.core.data;

import com.hypixel.hytale.math.vector.Location;
import com.raeden.hytale.modules.admin.PlayerOffence;

import java.util.*;

public class PlayerData {
    // general
    private UUID UUID;
    private final List<String> username = new ArrayList<>();
    private String language;
    private String nickname;

    // playtime
    private long firstJoined;
    private long lastJoined;
    private long playTimeMillis;
    private transient long sessionStart = 0;

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

    // Admin
    private boolean godModeEnabled;
    private boolean isVanished;
    private boolean isFlying;
    private final List<PlayerOffence> offences = new ArrayList<>();

    // Convenience
    private final Map<String, Location> homes = new HashMap<>();

    // Economy
    private final HashMap<String, Long> balances = new HashMap<>();

    // Chat
    private long totalMessagesSent;
    private boolean isMuted;
    private long muteDuration;
    private boolean isSilenced;
    private final List<String> blockedPlayers = new ArrayList<>();

    public UUID getUUID() {return UUID;}
    public void setUUID(UUID UUID) {this.UUID = UUID;}

    public String getLanguage() {return language;}
    public void setLanguage(String language) {this.language = language;}

    public List<String> getUsername() {return username;}
    public void addUsername(String name) {username.add(name);}

    public String getNickname() {return nickname;}
    public void setNickname(String nickname) {this.nickname = nickname;}

    public long getFirstJoined() {return firstJoined;}
    public void setFirstJoined(long firstJoined) {this.firstJoined = firstJoined;}
    public long getLastJoined() {return lastJoined;}
    public void setLastJoined(long lastJoined) {this.lastJoined = lastJoined;}
    public long getPlayTimeMillis() {return playTimeMillis;}
    public void setPlayTimeMillis(long playTimeMillis) {this.playTimeMillis = playTimeMillis;}

    public long getSessionStart() {return sessionStart;}
    public void setSessionStart(long sessionStart) {this.sessionStart = sessionStart;}

    public long getCurrencyBalance(String currency) {return balances.getOrDefault(currency, 0L);}
    public HashMap<String, Long> getBalances() {return balances;}
    public void setCurrencyBalance(String currency, long balance) {this.balances.put(currency, balance);}

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
    public void removeAllHome() {homes.clear();}
    public Map<String, Location> getHomes() {return homes;}

    public boolean isMuted() {return isMuted;}

    public void setMuted(boolean muted) {isMuted = muted;}

    public List<PlayerOffence> getOffences() {return offences;}
    public void addOffence(PlayerOffence offence) {offences.add(offence);}
    public void clearOffences() { offences.clear();}

    public int getDamageTaken() {return damageTaken;}
    public void setDamageTaken(int damageTaken) {this.damageTaken = damageTaken;}
    public int getDamageGiven() {return damageGiven;}
    public void setDamageGiven(int damageGiven) {this.damageGiven = damageGiven;}

    public long getTotalMessagesSent() {return totalMessagesSent;}
    public void increaseMessageSent() {totalMessagesSent++;}
    public void setTotalMessagesSent(long totalMessagesSent) {this.totalMessagesSent = totalMessagesSent;}

    public boolean isSilenced() {return isSilenced;}
    public void setSilenced(boolean silenced) {isSilenced = silenced;}

    public List<String> getBlockedPlayers() {return blockedPlayers;}
    public void addNewBlockedPlayer(String username) { blockedPlayers.add(username);}
    public void removeBlockedPlayer(String username) { blockedPlayers.remove(username);}
    public void clearBlockList() {blockedPlayers.clear();}

    public long getMuteDuration() {return muteDuration;}
    public void setMuteDuration(long muteDuration) {this.muteDuration = muteDuration;}
}
