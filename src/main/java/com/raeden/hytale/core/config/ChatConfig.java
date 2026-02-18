package com.raeden.hytale.core.config;

import java.util.ArrayList;
import java.util.List;

public class ChatConfig {
    private String VERSION;
    private boolean SHOW_NICKNAME;
    private boolean SHOW_PREFIX;
    private boolean SHOW_SUFFIX;
    private int MAX_PREFIX;
    private int MAX_SUFFIX;
    private boolean ALLOW_PLAYER_CHAT_COLORS;

    private boolean TOGGLE_CENSOR_WORD_LIST;
    private boolean CENSOR_CURSE_WORDS;
    private boolean REMOVE_LINKS;
    private boolean REMOVE_IPS;

    private int PVT_MSG_CACHE_CLEAR_INTERVAL; // LOGOUT + 5M, 10M, 30M, 1H
    private boolean SAVE_CHAT_LOG;
    private int CHAT_LOG_SAVE_INTERVAL; // On shutdown + 5M, 10M, 30M, 1H

    private List<String> CURSE_WORD_LIST = new ArrayList<>();
    private List<String> CENSOR_WORD_LIST = new ArrayList<>();


    public String getVersion() {return VERSION;}
    public void setVersion(String VERSION) {this.VERSION = VERSION;}
    public boolean isShowNickNames() {return SHOW_NICKNAME;}
    public void setShowNickNames(boolean SHOW_NICKNAME) {this.SHOW_NICKNAME = SHOW_NICKNAME;}
    public boolean isShowPrefix() {return SHOW_PREFIX;}
    public void setShowPrefix(boolean SHOW_PREFIX) {this.SHOW_PREFIX = SHOW_PREFIX;}
    public boolean isShowSuffix() {return SHOW_SUFFIX;}
    public void setShowSuffix(boolean SHOW_SUFFIX) {this.SHOW_SUFFIX = SHOW_SUFFIX;}
    public int getMaxSuffix() {return MAX_SUFFIX;}
    public void setMaxSuffix(int MAX_TAGS) {this.MAX_SUFFIX = MAX_TAGS;}
    public int getMaxPrefix() {return MAX_PREFIX;}
    public void setMaxPrefix(int MAX_PREFIX) {this.MAX_PREFIX = MAX_PREFIX;}

    public boolean isAllowPlayerChatColors() {return ALLOW_PLAYER_CHAT_COLORS;}
    public void setAllowPlayerChatColors(boolean ALLOW_PLAYER_CHAT_COLORS) {this.ALLOW_PLAYER_CHAT_COLORS = ALLOW_PLAYER_CHAT_COLORS;}
    public boolean isToggleCensorWordList() {return TOGGLE_CENSOR_WORD_LIST;}
    public void setToggleCensorWordList(boolean TOGGLE_CENSOR_WORD_LIST) {this.TOGGLE_CENSOR_WORD_LIST = TOGGLE_CENSOR_WORD_LIST;}
    public boolean isCensorCurseWords() {return CENSOR_CURSE_WORDS;}
    public void setCensorCurseWords(boolean CENSOR_CURSE_WORDS) {this.CENSOR_CURSE_WORDS = CENSOR_CURSE_WORDS;}
    public boolean isRemoveLinks() {return REMOVE_LINKS;}
    public void setRemoveLinks(boolean REMOVE_LINKS) {this.REMOVE_LINKS = REMOVE_LINKS;}
    public boolean isRemoveIps() {return REMOVE_IPS;}
    public void setRemoveIps(boolean REMOVE_IPS) {this.REMOVE_IPS = REMOVE_IPS;}

    public boolean isSaveChatLog() {return SAVE_CHAT_LOG;}
    public void setSaveChatLog(boolean saveChatLog) {this.SAVE_CHAT_LOG = saveChatLog;}
    public int getChatLogSaveInterval() {return CHAT_LOG_SAVE_INTERVAL;}
    public void setChatLogSaveInterval(int ChatLogSaveInterval) {this.CHAT_LOG_SAVE_INTERVAL = ChatLogSaveInterval;}
    public int getPvtMsgClearInterval() {return PVT_MSG_CACHE_CLEAR_INTERVAL;}
    public void setPvtMsgClearInterval(int PvtMsgClearInterval) {this.PVT_MSG_CACHE_CLEAR_INTERVAL = PvtMsgClearInterval;}

    public List<String> getCurseWordList() {return CURSE_WORD_LIST;}
    public void setCurseWordList(List<String> CURSE_WORD_LIST) {this.CURSE_WORD_LIST = CURSE_WORD_LIST;}
    public List<String> getCensorWordList() {return CENSOR_WORD_LIST;}
    public void setCensorWordList(List<String> CENSOR_WORD_LIST) {this.CENSOR_WORD_LIST = CENSOR_WORD_LIST;}
}
