package com.raeden.hytale.core.data;

import com.hypixel.hytale.math.vector.Location;

import java.util.*;

public class PlayerProfile {
    private transient long sessionStart = 0;

    private UUID UUID;
    private final List<String> username = new ArrayList<>();
    private String language;
    private String nickname;

    // Admin
    private boolean godModeEnabled;
    private boolean isVanished;
    private boolean isFlying;

    // Convenience
    private final Map<String, Location> homes = new HashMap<>();

    // Economy
    private final HashMap<String, Long> balances = new HashMap<>();

    // Chat
    private boolean isMuted;
    private long muteDuration;
    private boolean isSilenced;
    private final List<String> blockedPlayers = new ArrayList<>();

    public long getSessionStart() {return sessionStart;}
    public void setSessionStart(long sessionStart) {this.sessionStart = sessionStart;}

    public UUID getUUID() {return UUID;}
    public void setUUID(UUID UUID) {this.UUID = UUID;}

    public String getLanguage() {return language;}
    public void setLanguage(String language) {this.language = language;}

    public List<String> getUsername() {return username;}
    public void addUsername(String name) {username.add(name);}

    public String getNickname() {return nickname;}
    public void setNickname(String nickname) {this.nickname = nickname;}

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

    public long getCurrencyBalance(String currency) {return balances.getOrDefault(currency, 0L);}
    public HashMap<String, Long> getBalances() {return balances;}
    public void setCurrencyBalance(String currency, long balance) {this.balances.put(currency, balance);}

    public boolean isMuted() {return isMuted;}
    public void setMuted(boolean muted) {isMuted = muted;}

    public boolean isSilenced() {return isSilenced;}
    public void setSilenced(boolean silenced) {isSilenced = silenced;}

    public List<String> getBlockedPlayers() {return blockedPlayers;}
    public void addNewBlockedPlayer(String username) { blockedPlayers.add(username);}
    public void removeBlockedPlayer(String username) { blockedPlayers.remove(username);}
    public void clearBlockList() {blockedPlayers.clear();}

    public long getMuteDuration() {return muteDuration;}
    public void setMuteDuration(long muteDuration) {this.muteDuration = muteDuration;}
}
