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
    @SerializedName("data_storage_type")
    private String dataStorageType;
    @SerializedName("player_data_save_interval")
    private int playerDataSaveInterval; // LOGOUT, 5M, 10M, 30M, 1H
    @SerializedName("generate_default_data")
    private boolean generateDefaultData;

    @SerializedName("debug_mode")
    private boolean debugMode;
    @SerializedName("log_actions")
    private boolean logActions;

    @SerializedName("module_admin")
    private boolean toggleAdminModule;
    @SerializedName("module_chat")
    private boolean toggleChatModule;
    @SerializedName("module_rank")
    private boolean toggleRankModule;
    @SerializedName("module_homes")
    private boolean toggleHomesModule;
    @SerializedName("module_mail")
    private boolean toggleMailModule;
    @SerializedName("module_party")
    private boolean togglePartyModule;
    @SerializedName("module_economy")
    private boolean toggleEconomyModule;
    @SerializedName("module_analytics")
    private boolean toggleAnalyticsModule;
    @SerializedName("module_discord")
    private boolean toggleDiscordModule;

    @SerializedName("data_clusters")
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

    public boolean isDebugMode() { return debugMode; }
    public void setDebugMode(boolean debugMode) { this.debugMode = debugMode; }

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

    public boolean isGenerateDefaultData() {return generateDefaultData;}
    public void setGenerateDefaultData(boolean generateDefaultData) {this.generateDefaultData = generateDefaultData;}
}