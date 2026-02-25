package com.raeden.hytale.core.config.containers;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Config {
    @SerializedName("VERSION")
    private String version;
    @SerializedName("LANG")
    private String lang;
    @SerializedName("DATA_STORAGE_TYPE")
    private String dataStorageType;
    @SerializedName("PLAYER_DATA_SAVE_INTERVAL")
    private int playerDataSaveInterval; // LOGOUT, 5M, 10M, 30M, 1H

    @SerializedName("DEBUG_MODE")
    private boolean debugMode;
    @SerializedName("LOG_ACTIONS")
    private boolean logActions;

    @SerializedName("TOGGLE_ADMIN_MODULE")
    private boolean toggleAdminModule;
    @SerializedName("TOGGLE_CHAT_MODULE")
    private boolean toggleChatModule;
    @SerializedName("TOGGLE_RANK_MODULE")
    private boolean toggleRankModule;
    @SerializedName("TOGGLE_HOMES_MODULE")
    private boolean toggleHomesModule;
    @SerializedName("TOGGLE_MAIL_MODULE")
    private boolean toggleMailModule;
    @SerializedName("TOGGLE_PARTY_MODULE")
    private boolean togglePartyModule;
    @SerializedName("TOGGLE_ECONOMY_MODULE")
    private boolean toggleEconomyModule;
    @SerializedName("TOGGLE_ANALYTICS_MODULE")
    private boolean toggleAnalyticsModule;
    @SerializedName("TOGGLE_DISCORD_MODULE")
    private boolean toggleDiscordModule;

    @SerializedName("DATA_CLUSTERS")
    private final Map<String, List<String>> dataClusters = new ConcurrentHashMap<>();

    // --- Getters and Setters ---

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getLang() { return lang; }
    public void setLang(String lang) { this.lang = lang; }

    public String getDataStorageType() { return dataStorageType; }
    public void setDataStorageType(String dataStorageType) { this.dataStorageType = dataStorageType; }

    public int getPlayerDataSaveInterval() { return playerDataSaveInterval; }
    public void setPlayerDataSaveInterval(int playerDataSaveInterval) { this.playerDataSaveInterval = playerDataSaveInterval; }

    public boolean isToggleDebug() { return debugMode; }
    public void setToggleDebug(boolean debugMode) { this.debugMode = debugMode; }

    public boolean isLogActions() { return logActions; }
    public void setLogActions(boolean logActions) { this.logActions = logActions; }

    public boolean isToggleAdminModule() { return toggleAdminModule; }
    public void setToggleAdminModule(boolean toggleAdminModule) { this.toggleAdminModule = toggleAdminModule; }

    public boolean isToggleChatModule() { return toggleChatModule; }
    public void setToggleChatModule(boolean toggleChatModule) { this.toggleChatModule = toggleChatModule; }

    public boolean isToggleMailModule() { return toggleMailModule; }
    public void setToggleMailModule(boolean toggleMailModule) { this.toggleMailModule = toggleMailModule; }

    public boolean isToggleRankModule() { return toggleRankModule; }
    public void setToggleRankModule(boolean toggleRankModule) { this.toggleRankModule = toggleRankModule; }

    public boolean isToggleHomesModule() { return toggleHomesModule; }
    public void setToggleHomesModule(boolean toggleHomesModule) { this.toggleHomesModule = toggleHomesModule; }

    public boolean isTogglePartyModule() { return togglePartyModule; }
    public void setTogglePartyModule(boolean togglePartyModule) { this.togglePartyModule = togglePartyModule; }

    public boolean isToggleEconomyModule() { return toggleEconomyModule; }
    public void setToggleEconomyModule(boolean toggleEconomyModule) { this.toggleEconomyModule = toggleEconomyModule; }

    public boolean isToggleAnalyticsModule() { return toggleAnalyticsModule; }
    public void setToggleAnalyticsModule(boolean toggleAnalyticsModule) { this.toggleAnalyticsModule = toggleAnalyticsModule; }

    public boolean isToggleDiscordModule() { return toggleDiscordModule; }
    public void setToggleDiscordModule(boolean toggleDiscordModule) { this.toggleDiscordModule = toggleDiscordModule; }

    public void addDataCluster(String name, List<String> worlds) { dataClusters.put(name, worlds); }
    public Map<String, List<String>> getDataClusters() { return dataClusters; }
}