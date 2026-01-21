package com.raeden.hytale.modules.chat;

import com.raeden.hytale.HytaleEssentials;

import java.util.LinkedHashMap;

public class ChatManager {
    private final HytaleEssentials hytaleEssentials;

    private final LinkedHashMap<String, String> activeMessengers;
    private final LinkedHashMap<Long, String> messageLog;

    public ChatManager(HytaleEssentials hytaleEssentials) {
        this.hytaleEssentials = hytaleEssentials;

        activeMessengers = new LinkedHashMap<>();
        messageLog = new LinkedHashMap<>();
    }

    public LinkedHashMap<String, String> getActiveMessengers() {return activeMessengers;}
    public void addActiveMessengers(String senderUsername, String receiverUsername) {
        activeMessengers.put(senderUsername, receiverUsername);
        activeMessengers.put(receiverUsername, senderUsername);
    }
    public String getReceiver(String senderUsername) {
        return activeMessengers.get(senderUsername);
    }

    private void clearActiveMessageCache() {

    }
}
