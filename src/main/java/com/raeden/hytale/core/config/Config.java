package com.raeden.hytale.core.config;

public class Config {
    private String VERSION;
    private String LANG;
    private String DATA_STORAGE_TYPE;
    private int PLAYER_DATA_SAVE_INTERVAL; // LOGOUT, 5M, 10M, 30M, 1H
    private boolean DEBUG_MODE;
    private boolean TOGGLE_ADMIN_MODULE;
    private boolean TOGGLE_CHAT_MODULE;
    private boolean TOGGLE_RANK_MODULE;
    private boolean TOGGLE_PARTY_MODULE;
    private boolean TOGGLE_ECONOMY_MODULE;
    private boolean TOGGLE_ANALYTICS_MODULE;
    private boolean TOGGLE_DISCORD_MODULE;

    public String getLang() {return LANG;}
    public void setLang(String lang) {this.LANG = lang;}

    public String getDataStorageType() {return DATA_STORAGE_TYPE;}
    public void setDataStorageType(String dataStorageType) {this.DATA_STORAGE_TYPE = dataStorageType;}

    public int getPlayerDataSaveInterval() {return PLAYER_DATA_SAVE_INTERVAL;}
    public void setPlayerDataSaveInterval(int playerDataSaveInterval) {this.PLAYER_DATA_SAVE_INTERVAL = playerDataSaveInterval;}

    public boolean isToggleDebug() {return DEBUG_MODE;}
    public void setToggleDebug(boolean toggleDebug) {this.DEBUG_MODE = toggleDebug;}

    public boolean isToggleAdminModule() {return TOGGLE_ADMIN_MODULE;}
    public void setToggleAdminModule(boolean toggleAdminModule) {this.TOGGLE_ADMIN_MODULE = toggleAdminModule;}

    public boolean isToggleChatModule() {return TOGGLE_CHAT_MODULE;}
    public void setToggleChatModule(boolean toggleChatModule) {this.TOGGLE_CHAT_MODULE = toggleChatModule;}

    public boolean isToggleRankModule() {return TOGGLE_RANK_MODULE;}
    public void setToggleRankModule(boolean TOGGLE_RANK_MODULE) {this.TOGGLE_RANK_MODULE = TOGGLE_RANK_MODULE;}

    public boolean isTogglePartyModule() {return TOGGLE_PARTY_MODULE;}
    public void setTogglePartyModule(boolean togglePartyModule) {this.TOGGLE_PARTY_MODULE = togglePartyModule;}

    public boolean isToggleEconomyModule() {return TOGGLE_ECONOMY_MODULE;}
    public void setToggleEconomyModule(boolean toggleEconomyModule) {this.TOGGLE_ECONOMY_MODULE = toggleEconomyModule;}

    public boolean isToggleAnalyticsModule() {return TOGGLE_ANALYTICS_MODULE;}
    public void setToggleAnalyticsModule(boolean toggleAnalyticsModule) {this.TOGGLE_ANALYTICS_MODULE = toggleAnalyticsModule;}

    public boolean isToggleDiscordModule() {return TOGGLE_DISCORD_MODULE;}
    public void setToggleDiscordModule(boolean toggleDiscordModule) {this.TOGGLE_DISCORD_MODULE = toggleDiscordModule;}

    public String getVersion() {return VERSION;}
    public void setVersion(String VERSION) {this.VERSION = VERSION;}


}