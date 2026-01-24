package com.raeden.hytale.core.config;

public class Config {
    private String LANG;
    private String DATA_STORAGE_TYPE;
    private int PLAYER_DATA_SAVE_INTERVAL; // LOGOUT, 5M, 10M, 30M, 1H
    private boolean TOGGLE_DEBUG;
    private boolean TOGGLE_ADMIN_MODULE;
    private boolean TOGGLE_CHAT_MODULE;
    private boolean TOGGLE_PARTY_MODULE;
    private boolean TOGGLE_ECONOMY_MODULE;
    private boolean TOGGLE_ANALYTICS_MODULE;
    private boolean TOGGLE_DISCORD_MODULE;

    private int PVT_MSG_CACHE_CLEAR_INTERVAL; // LOGOUT + 5M, 10M, 30M, 1H
    private boolean SAVE_CHAT_LOG;
    private int CHAT_LOG_SAVE_INTERVAL; // On shutdown + 5M, 10M, 30M, 1H

    public String getLang() {return LANG;}
    public void setLang(String lang) {this.LANG = lang;}

    public String getDataStorageType() {return DATA_STORAGE_TYPE;}
    public void setDataStorageType(String dataStorageType) {this.DATA_STORAGE_TYPE = dataStorageType;}

    public int getPlayerDataSaveInterval() {return PLAYER_DATA_SAVE_INTERVAL;}
    public void setPlayerDataSaveInterval(int playerDataSaveInterval) {this.PLAYER_DATA_SAVE_INTERVAL = playerDataSaveInterval;}

    public boolean isToggleDebug() {return TOGGLE_DEBUG;}
    public void setToggleDebug(boolean toggleDebug) {this.TOGGLE_DEBUG = toggleDebug;}

    public boolean isToggleAdminModule() {return TOGGLE_ADMIN_MODULE;}
    public void setToggleAdminModule(boolean toggleAdminModule) {this.TOGGLE_ADMIN_MODULE = toggleAdminModule;}

    public boolean isToggleChatModule() {return TOGGLE_CHAT_MODULE;}
    public void setToggleChatModule(boolean toggleChatModule) {this.TOGGLE_CHAT_MODULE = toggleChatModule;}

    public boolean isTogglePartyModule() {return TOGGLE_PARTY_MODULE;}
    public void setTogglePartyModule(boolean togglePartyModule) {this.TOGGLE_PARTY_MODULE = togglePartyModule;}

    public boolean isToggleEconomyModule() {return TOGGLE_ECONOMY_MODULE;}
    public void setToggleEconomyModule(boolean toggleEconomyModule) {this.TOGGLE_ECONOMY_MODULE = toggleEconomyModule;}

    public boolean isToggleAnalyticsModule() {return TOGGLE_ANALYTICS_MODULE;}
    public void setToggleAnalyticsModule(boolean toggleAnalyticsModule) {this.TOGGLE_ANALYTICS_MODULE = toggleAnalyticsModule;}

    public boolean isToggleDiscordModule() {return TOGGLE_DISCORD_MODULE;}
    public void setToggleDiscordModule(boolean toggleDiscordModule) {this.TOGGLE_DISCORD_MODULE = toggleDiscordModule;}

    public boolean isSaveChatLog() {return SAVE_CHAT_LOG;}
    public void setSaveChatLog(boolean saveChatLog) {this.SAVE_CHAT_LOG = saveChatLog;}

    public int getChatLogSaveInterval() {return CHAT_LOG_SAVE_INTERVAL;}
    public void setChatLogSaveInterval(int ChatLogSaveInterval) {this.CHAT_LOG_SAVE_INTERVAL = ChatLogSaveInterval;}

    public int getPvtMsgClearInterval() {return PVT_MSG_CACHE_CLEAR_INTERVAL;}
    public void setPvtMsgClearInterval(int PvtMsgClearInterval) {this.PVT_MSG_CACHE_CLEAR_INTERVAL = PvtMsgClearInterval;}

   private int titleDefaultFadeIn;
   private int titleDefaultStay;
   private int titleDefaultFadeOut;

  public int getTitleDefaultFadeIn() { return titleDefaultFadeIn; }
  public void setTitleDefaultFadeIn(int titleDefaultFadeIn) { this.titleDefaultFadeIn = titleDefaultFadeIn; }

  public int getTitleDefaultStay() { return titleDefaultStay; }
  public void setTitleDefaultStay(int titleDefaultStay) { this.titleDefaultStay = titleDefaultStay; }

  public int getTitleDefaultFadeOut() { return titleDefaultFadeOut; }
    public void setTitleDefaultFadeOut(int titleDefaultFadeOut) { this.titleDefaultFadeOut = titleDefaultFadeOut; }
}