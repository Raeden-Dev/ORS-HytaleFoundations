package com.raeden.hytale.core.config;

import java.util.LinkedList;
import java.util.List;

public class ChatConfig {
    private boolean ALLOW_PLAYER_CHAT_COLORS;
    private boolean TOGGLE_CENSOR_WORD_LIST;
    private boolean CENSOR_CURSE_WORDS;
    private boolean REMOVE_LINKS;
    private boolean REMOVE_IPS;
    private List<String> CURSE_WORD_LIST = new LinkedList<>();
    private List<String> CENSOR_WORD_LIST = new LinkedList<>();


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

    public List<String> getCurseWordList() {return CURSE_WORD_LIST;}
    public void setCurseWordList(List<String> CURSE_WORD_LIST) {this.CURSE_WORD_LIST = CURSE_WORD_LIST;}
    public List<String> getCensorWordList() {return CENSOR_WORD_LIST;}
    public void setCensorWordList(List<String> CENSOR_WORD_LIST) {this.CENSOR_WORD_LIST = CENSOR_WORD_LIST;}
}
