package com.raeden.hytale.modules.chat;

import com.raeden.hytale.HytaleEssentials;
import com.raeden.hytale.lang.LangKey;
import com.raeden.hytale.utils.Scheduler;
import com.raeden.hytale.utils.TimeUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.raeden.hytale.HytaleEssentials.langManager;
import static com.raeden.hytale.HytaleEssentials.myLogger;

public class ChatManager {
    private final HytaleEssentials hytaleEssentials;
    private final Scheduler scheduler;

    private final LinkedHashMap<String, String> activeMessengers;
    private final LinkedHashMap<String, String> messageLog; // Time string + Message

    private final Path chatLogDir;

    public ChatManager(HytaleEssentials hytaleEssentials, Scheduler scheduler) {
        this.hytaleEssentials = hytaleEssentials;
        this.scheduler = scheduler;

        chatLogDir = hytaleEssentials.getDataDirectory().resolve("logs").resolve("chat");
        activeMessengers = new LinkedHashMap<>();
        messageLog = new LinkedHashMap<>();

        verify();
        if(hytaleEssentials.getConfigManager().getDefaultConfig().isSaveChatLog()) {
            createChatSaveScheduler();
        }
    }

    private void verify() {
        if(!Files.exists(chatLogDir)) {
            try {
                Files.createDirectories(chatLogDir);
                myLogger.atInfo().log(langManager.getMessage(LangKey.CREATE_DIRECTORY_W_LOC, "chat", chatLogDir.toString()).getAnsiMessage());
            } catch (IOException e) {
                myLogger.atWarning().log(langManager.getMessage(LangKey.CREATE_DIRECTORY_FAIL_W_LOC,"chat", chatLogDir.toString()).getAnsiMessage());
            }
        }
    }

    private void createChatSaveScheduler() {
        scheduler.runTaskTimer("chatSaveScheduler", () -> {
                if(messageLog.isEmpty()) return;
                exportChatLog();
                },
                hytaleEssentials.getConfigManager().getDefaultConfig().getChatLogSaveInterval(),
                hytaleEssentials.getConfigManager().getDefaultConfig().getChatLogSaveInterval(),
                TimeUnit.MINUTES);
    }

    private void exportChatLog() {
        if(messageLog.isEmpty()) return;

        String fileName = "chatlog_" + TimeUtils.getTimeNow() + ".txt";
        File logFile = new File(chatLogDir.toString(), fileName);

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(logFile))) {
            writer.write("---- CHAT LOG ----");
            writer.newLine();
            writer.write("Total messages: " + messageLog.size());
            writer.newLine();
            writer.newLine();
            for(Map.Entry<String, String> entry : messageLog.entrySet()) {
                writer.write("["+entry.getKey()+"] " + entry.getValue());
                writer.newLine();
            }
            writer.write("---- END ----");
            myLogger.atInfo().log(langManager.getMessage(LangKey.CHAT_LOG_EXPORTED, fileName, chatLogDir.toString()).getAnsiMessage());
        } catch (IOException e) {
            myLogger.atSevere().log(langManager.getMessage(LangKey.CHAT_LOG_EXPORT_FAIL, fileName).getAnsiMessage());
        }
    }

    public void addMessageToLog(String message) {
        messageLog.put(TimeUtils.getTimeNow(), message);
    }

    // Private Messaging
    public LinkedHashMap<String, String> getActiveMessengers() {
        return activeMessengers;
    }
    public String getReceiver(String senderUsername) {
        return activeMessengers.get(senderUsername);
    }
    public void addActiveMessengers(String senderUsername, String receiverUsername) {
        activeMessengers.put(senderUsername, receiverUsername);
        activeMessengers.put(receiverUsername, senderUsername);
    }
    private void clearActiveMessageCache() {
        scheduler.runTaskTimer("clearActiveMessageCache", activeMessengers::clear,
                hytaleEssentials.getConfigManager().getDefaultConfig().getPvtMsgClearInterval(),
                hytaleEssentials.getConfigManager().getDefaultConfig().getPvtMsgClearInterval(),
                TimeUnit.MINUTES);
    }

}
