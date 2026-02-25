package com.raeden.hytale.core.config.containers;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class ChatConfig {

    @SerializedName("VERSION")
    private String version;
    @SerializedName("SHOW_CHAT_MSG_PREFIX")
    private boolean showChatMsgPrefix;

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

    @SerializedName("ALLOW_PLAYER_CHAT_COLORS")
    private boolean allowPlayerChatColors;
    @SerializedName("TOGGLE_CENSOR_WORD_LIST")
    private boolean toggleCensorWordList;
    @SerializedName("CENSOR_CURSE_WORDS")
    private boolean censorCurseWords;
    @SerializedName("REMOVE_LINKS")
    private boolean removeLinks;
    @SerializedName("REMOVE_IPS")
    private boolean removeIps;
    @SerializedName("PVT_MSG_CACHE_CLEAR_INTERVAL")
    private int pvtMsgCacheClearInterval; // LOGOUT + 5M, 10M, 30M, 1H
    @SerializedName("CHAT_LOG_SAVE_INTERVAL")
    private int chatLogSaveInterval; // On shutdown + 5M, 10M, 30M, 1H
    @SerializedName("CURSE_WORD_LIST")
    private List<String> curseWordList = new ArrayList<>();
    @SerializedName("CENSOR_WORD_LIST")
    private List<String> censorWordList = new ArrayList<>();

    // --- Getters and Setters ---

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public boolean isShowNickname() { return showNickname; }
    public void setShowNickname(boolean showNickname) { this.showNickname = showNickname; }

    public boolean isShowPrefix() { return showPrefix; }
    public void setShowPrefix(boolean showPrefix) { this.showPrefix = showPrefix; }

    public boolean isShowSuffix() { return showSuffix; }
    public void setShowSuffix(boolean showSuffix) { this.showSuffix = showSuffix; }

    public int getMaxSuffix() { return maxSuffix; }
    public void setMaxSuffix(int maxSuffix) { this.maxSuffix = maxSuffix; }

    public int getMaxPrefix() { return maxPrefix; }
    public void setMaxPrefix(int maxPrefix) { this.maxPrefix = maxPrefix; }

    public boolean isAllowPlayerChatColors() { return allowPlayerChatColors; }
    public void setAllowPlayerChatColors(boolean allowPlayerChatColors) { this.allowPlayerChatColors = allowPlayerChatColors; }

    public boolean isToggleCensorWordList() { return toggleCensorWordList; }
    public void setToggleCensorWordList(boolean toggleCensorWordList) { this.toggleCensorWordList = toggleCensorWordList; }

    public boolean isCensorCurseWords() { return censorCurseWords; }
    public void setCensorCurseWords(boolean censorCurseWords) { this.censorCurseWords = censorCurseWords; }

    public boolean isRemoveLinks() { return removeLinks; }
    public void setRemoveLinks(boolean removeLinks) { this.removeLinks = removeLinks; }

    public boolean isRemoveIps() { return removeIps; }
    public void setRemoveIps(boolean removeIps) { this.removeIps = removeIps; }

    public int getChatLogSaveInterval() { return chatLogSaveInterval; }
    public void setChatLogSaveInterval(int chatLogSaveInterval) { this.chatLogSaveInterval = chatLogSaveInterval; }

    public int getPvtMsgClearInterval() { return pvtMsgCacheClearInterval; }
    public void setPvtMsgClearInterval(int pvtMsgClearInterval) { this.pvtMsgCacheClearInterval = pvtMsgClearInterval; }

    public List<String> getCurseWordList() { return curseWordList; }
    public void setCurseWordList(List<String> curseWordList) { this.curseWordList = curseWordList; }

    public List<String> getCensorWordList() { return censorWordList; }
    public void setCensorWordList(List<String> censorWordList) { this.censorWordList = censorWordList; }

    public boolean isShowChatMsgPrefix() { return showChatMsgPrefix; }
    public void setShowChatMsgPrefix(boolean showChatMsgPrefix) { this.showChatMsgPrefix = showChatMsgPrefix; }
}