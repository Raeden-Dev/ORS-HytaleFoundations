package com.raeden.hytale.core.player;

import com.google.gson.annotations.SerializedName;
import com.hypixel.hytale.math.vector.Location;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerProfile {
    private transient long sessionStart = 0;

    @SerializedName("uuid")
    private UUID uuid;
    @SerializedName("username")
    private final List<String> username = new ArrayList<>();
    @SerializedName("language")
    private String language;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("username_color_code")
    private String usernameColorCode;

    @SerializedName("god_mode_enabled")
    private boolean godModeEnabled;
    @SerializedName("is_vanished")
    private boolean isVanished;
    @SerializedName("is_flying")
    private boolean isFlying;
    @SerializedName("is_anonymous")
    private boolean isAnonymous;

    @SerializedName("homes")
    private final Map<String, Location> homes = new ConcurrentHashMap<>();

    @SerializedName("balances")
    private final Map<String, Long> balances = new ConcurrentHashMap<>();

    @SerializedName("is_muted")
    private boolean isMuted;
    @SerializedName("mute_duration")
    private long muteDuration;
    @SerializedName("is_silenced")
    private boolean isSilenced;
    @SerializedName("show_nickname")
    private boolean showNickname;
    @SerializedName("show_prefix")
    private boolean showPrefix;
    @SerializedName("show_suffix")
    private boolean showSuffix;
    @SerializedName("max_prefix")
    private int maxPrefix;
    @SerializedName("max_suffix")
    private int maxSuffix;

    @SerializedName("active_prefix")
    private final Map<String, String> activePrefix = new ConcurrentHashMap<>();
    @SerializedName("active_suffix")
    private final Map<String, String> activeSuffix = new ConcurrentHashMap<>();
    @SerializedName("blocked_players")
    private final List<String> blockedPlayers = new ArrayList<>();
    @SerializedName("ignored_players")
    private final List<String> ignoredPlayers = new ArrayList<>();

    @SerializedName("warm_up_durations")
    private Map<String, Double> warmupDurations = new ConcurrentHashMap<>();

    @SerializedName("rank_id")
    private String rankId;

    public long getSessionStart() {return sessionStart;}
    public void setSessionStart(long sessionStart) {this.sessionStart = sessionStart;}

    public UUID getUuid() {return uuid;}
    public void setUuid(UUID uuid) {this.uuid = uuid;}

    public List<String> getUsername() {return username;}
    public void addUsername(String name) {username.add(name);}

    public String getLanguage() {return language;}
    public void setLanguage(String language) {this.language = language;}

    public String getNickname() {return nickname;}
    public void setNickname(String nickname) {this.nickname = nickname;}

    public String getUsernameColorCode() {return usernameColorCode;}
    public void setUsernameColorCode(String usernameColorCode) {this.usernameColorCode = usernameColorCode;}

    public boolean isGodModeEnabled() {return godModeEnabled;}
    public void setGodModeEnabled(boolean godModeEnabled) {this.godModeEnabled = godModeEnabled;}

    public boolean isVanished() {return isVanished;}
    public void setVanished(boolean vanished) {this.isVanished = vanished;}

    public boolean isFlying() {return isFlying;}
    public void setFlying(boolean flying) {this.isFlying = flying;}

    public boolean isAnonymous() {return isAnonymous;}
    public void setAnonymous(boolean anonymous) {this.isAnonymous = anonymous;}

    public void addHome(String name, Location location) {homes.put(name, location);}
    public void removeHome(String name) {homes.remove(name);}
    public void removeAllHome() {homes.clear();}
    public Map<String, Location> getHomes() {return homes;}

    public long getCurrencyBalance(String currency) {return balances.getOrDefault(currency, 0L);}
    public Map<String, Long> getBalances() {return balances;}
    public void setCurrencyBalance(String currency, long balance) {this.balances.put(currency, balance);}

    public boolean isMuted() {return isMuted;}
    public void setMuted(boolean muted) {this.isMuted = muted;}

    public long getMuteDuration() {return muteDuration;}
    public void setMuteDuration(long muteDuration) {this.muteDuration = muteDuration;}

    public boolean isSilenced() {return isSilenced;}
    public void setSilenced(boolean silenced) {this.isSilenced = silenced;}

    public boolean isShowNickname() {return showNickname;}
    public void setShowNickname(boolean showNickname) {this.showNickname = showNickname;}

    public boolean isShowPrefix() {return showPrefix;}
    public void setShowPrefix(boolean showPrefix) {this.showPrefix = showPrefix;}

    public boolean isShowSuffix() {return showSuffix;}
    public void setShowSuffix(boolean showSuffix) {this.showSuffix = showSuffix;}

    public int getMaxPrefix() {return maxPrefix;}
    public void setMaxPrefix(int maxPrefix) {this.maxPrefix = maxPrefix;}

    public int getMaxSuffix() {return maxSuffix;}
    public void setMaxSuffix(int maxSuffix) {this.maxSuffix = maxSuffix;}

    public Map<String, String> getActivePrefix() {return activePrefix;}
    public void addToActivePrefix(String ID, String prefix) {activePrefix.put(ID, prefix);}
    public void clearActivePrefix() {activePrefix.clear();}
    public void removeActivePrefix(String ID) { activePrefix.remove(ID);}

    public Map<String, String> getActiveSuffix() {return activeSuffix;}
    public void addToActiveSuffix(String ID, String suffix) { activeSuffix.put(ID, suffix);}
    public void clearActiveSuffix() {activeSuffix.clear();}
    public void removeActiveSuffix(String ID) { activeSuffix.remove(ID);}

    public List<String> getBlockedPlayers() {return blockedPlayers;}
    public void addNewBlockedPlayer(String username) { blockedPlayers.add(username);}
    public void removeBlockedPlayer(String username) { blockedPlayers.remove(username);}
    public void clearBlockList() {blockedPlayers.clear();}

    public List<String> getIgnoredPlayers() {return ignoredPlayers;}

    public Map<String, Double> getWarmupDurations() {return warmupDurations;}
    public void setWarmupDurations(Map<String, Double> warmupDurations) {this.warmupDurations = warmupDurations;}

    public String getRankId() {return rankId;}
    public void setRankId(String rankId) {this.rankId = rankId;}
}