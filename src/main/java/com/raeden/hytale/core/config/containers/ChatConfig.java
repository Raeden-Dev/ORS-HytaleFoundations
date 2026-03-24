package com.raeden.hytale.core.config.containers;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class ChatConfig {

    @SerializedName("version")
    private String version;
    @SerializedName("show_chat_msg_prefix")
    private boolean showChatMsgPrefix;

    @SerializedName("gradient_chunk_size")
    private int gradientChunkSize;
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

    @SerializedName("allow_player_chat_colors")
    private boolean allowPlayerChatColors;
    @SerializedName("toggle_censor_word_list")
    private boolean toggleCensorWordList;
    @SerializedName("censor_curse_words")
    private boolean censorCurseWords;
    @SerializedName("remove_links")
    private boolean removeLinks;
    @SerializedName("remove_ips")
    private boolean removeIps;
    @SerializedName("pvt_msg_cache_clear_interval")
    private int pvtMsgCacheClearInterval; // LOGOUT + 5M, 10M, 30M, 1H
    @SerializedName("chat_log_save_interval")
    private int chatLogSaveInterval; // On shutdown + 5M, 10M, 30M, 1H
    @SerializedName("curse_word_list")
    private List<String> curseWordList = new ArrayList<>();
    @SerializedName("censor_word_list")
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

    public int getGradientChunkSize() {return gradientChunkSize;}
    public void setGradientChunkSize(int gradientChunkSize) {this.gradientChunkSize = gradientChunkSize;}
}