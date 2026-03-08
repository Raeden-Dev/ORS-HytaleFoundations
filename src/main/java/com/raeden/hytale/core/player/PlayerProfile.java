package com.raeden.hytale.core.player;

import com.google.gson.annotations.SerializedName;
import com.hypixel.hytale.math.vector.Location;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerProfile {
    private transient long sessionStart = 0;

    @SerializedName("UUID")
    private UUID uuid;
    @SerializedName("USERNAME")
    private final List<String> username = new ArrayList<>();
    @SerializedName("LANGUAGE")
    private String language;
    @SerializedName("NICKNAME")
    private String nickname;
    @SerializedName("USERNAME_COLOR_CODE")
    private String usernameColorCode;

    // Admin
    @SerializedName("GOD_MODE_ENABLED")
    private boolean godModeEnabled;
    @SerializedName("IS_VANISHED")
    private boolean isVanished;
    @SerializedName("IS_FLYING")
    private boolean isFlying;
    @SerializedName("IS_ANONYMOUS")
    private boolean isAnonymous;

    // Convenience
    @SerializedName("HOMES")
    private final Map<String, Location> homes = new ConcurrentHashMap<>();

    // Economy
    @SerializedName("BALANCES")
    private final Map<String, Long> balances = new ConcurrentHashMap<>();

    // Chat
    @SerializedName("IS_MUTED")
    private boolean isMuted;
    @SerializedName("MUTE_DURATION")
    private long muteDuration;
    @SerializedName("IS_SILENCED")
    private boolean isSilenced;
    @SerializedName("SHOW_NICKNAME")
    private boolean showNickname;
    @SerializedName("SHOW_PREFIX")
    private boolean showPrefix;
    @SerializedName("SHOW_SUFFIX")
    private boolean showSuffix;
    @SerializedName("MAX_PREFIX")
    private int maxPrefix;
    @SerializedName("MAX_SUFFIX")
    private int maxSuffix;

    @SerializedName("ACTIVE_PREFIX")
    private final Map<String, String> activePrefix = new ConcurrentHashMap<>();
    @SerializedName("ACTIVE_SUFFIX")
    private final Map<String, String> activeSuffix = new ConcurrentHashMap<>();
    @SerializedName("BLOCKED_PLAYERS")
    private final List<String> blockedPlayers = new ArrayList<>();
    @SerializedName("IGNORED_PLAYERS")
    private final List<String> ignoredPlayers = new ArrayList<>();

    // Rank
    @SerializedName("RANK_ID")
    private String rankId;

    public long getSessionStart() {return sessionStart;}
    public void setSessionStart(long sessionStart) {this.sessionStart = sessionStart;}

    public UUID getUuid() {return uuid;}
    public void setUuid(UUID uuid) {this.uuid = uuid;}

    public String getLanguage() {return language;}
    public void setLanguage(String language) {this.language = language;}

    public List<String> getUsername() {return username;}
    public void addUsername(String name) {username.add(name);}

    public String getNickname() {return nickname;}
    public void setNickname(String nickname) {this.nickname = nickname;}

    public boolean isShowNickname() {return showNickname;}
    public void setShowNickname(boolean showNickname) {this.showNickname = showNickname;}
    public boolean isShowPrefix() {return showPrefix;}
    public void setShowPrefix(boolean showPrefix) {this.showPrefix = showPrefix;}

    public boolean isGodModeEnabled() {return godModeEnabled;}
    public void setGodModeEnabled(boolean godModeEnabled) {this.godModeEnabled = godModeEnabled;}
    public boolean isVanished() {return isVanished;}
    public void setVanished(boolean vanished) {isVanished = vanished;}
    public boolean isFlying() {return isFlying;}
    public void setFlying(boolean flying) {isFlying = flying;}
    public boolean isAnonymous() {return isAnonymous;}
    public void setAnonymous(boolean anonymous) {isAnonymous = anonymous;}

    public void addHome(String name, Location location) {homes.put(name, location);}
    public void removeHome(String name) {homes.remove(name);}
    public void removeAllHome() {homes.clear();}
    public Map<String, Location> getHomes() {return homes;}

    public long getCurrencyBalance(String currency) {return balances.getOrDefault(currency, 0L);}
    public Map<String, Long> getBalances() {return balances;}
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

    public boolean isShowSuffix() {return showSuffix;}
    public void setShowSuffix(boolean showSuffix) {this.showSuffix = showSuffix;}
    public int getMaxSuffix() {return maxSuffix;}
    public void setMaxSuffix(int maxSuffix) {this.maxSuffix = maxSuffix;}
    public int getMaxPrefix() {return maxPrefix;}
    public void setMaxPrefix(int maxPrefix) {this.maxPrefix = maxPrefix;}

    public Map<String, String> getActivePrefix() {return activePrefix;}
    public void addToActivePrefix(String ID, String prefix) {activePrefix.put(ID, prefix);}
    public void clearActivePrefix() {activePrefix.clear();}
    public void removeActivePrefix(String ID) { activePrefix.remove(ID);}
    public Map<String, String> getActiveSuffix() {return activeSuffix;}
    public void addToActiveSuffix(String ID, String suffix) { activeSuffix.put(ID, suffix);}
    public void clearActiveSuffix() {activeSuffix.clear();}
    public void removeActiveSuffix(String ID) { activeSuffix.remove(ID);}

    public String getUsernameColorCode() {return usernameColorCode;}
    public void setUsernameColorCode(String usernameColorCode) {this.usernameColorCode = usernameColorCode;}

    public String getRankId() {return rankId;}
    public void setRankId(String rankId) {this.rankId = rankId;}

    public List<String> getIgnoredPlayers() {return ignoredPlayers;}
}
