package com.raeden.hytale.modules.chat;

import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.lang.LangKey;
import com.raeden.hytale.utils.ColorEngine;
import com.raeden.hytale.utils.Scheduler;
import com.raeden.hytale.utils.TimeUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.raeden.hytale.HytaleFoundations.langManager;
import static com.raeden.hytale.HytaleFoundations.myLogger;
import static com.raeden.hytale.utils.FileManager.createDirectory;
import static com.raeden.hytale.utils.FileManager.logExceptionError;

public class ChatManager {
    private final HytaleFoundations hytaleFoundations;
    private final Scheduler scheduler;
    private final ColorEngine colorEngine;

    private final LinkedHashMap<String, String> activeMessengers;
    private final LinkedHashMap<String, String> messageLog; // Time string + Message

    private final Path chatLogDir;

    public ChatManager(HytaleFoundations hytaleFoundations, Scheduler scheduler) {
        this.hytaleFoundations = hytaleFoundations;
        this.scheduler = scheduler;

        // Color Engine
        colorEngine = new ColorEngine(hytaleFoundations);

        chatLogDir = hytaleFoundations.getDataDirectory().resolve("logs").resolve("chat_logs");
        activeMessengers = new LinkedHashMap<>();
        messageLog = new LinkedHashMap<>();

        verifyChatManager();
        if(hytaleFoundations.getConfigManager().getDefaultConfig().isSaveChatLog()) {
            createChatSaveScheduler();
        }
    }

    private void verifyChatManager() {
        createDirectory(chatLogDir, true);
    }

    private void createChatSaveScheduler() {
        scheduler.runTaskTimer("chatSaveScheduler", () -> {
                if(messageLog.isEmpty()) return;
                exportChatLog();
                },
                hytaleFoundations.getConfigManager().getDefaultConfig().getChatLogSaveInterval(),
                hytaleFoundations.getConfigManager().getDefaultConfig().getChatLogSaveInterval(),
                TimeUnit.MINUTES);
    }

    private void exportChatLog() {
        if(messageLog.isEmpty()) return;

        String fileName = TimeUtils.getFileSafeTime() + "_chatlog" + ".txt";
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
            myLogger.atInfo().log(langManager.getMessage(LangKey.LOG_CHAT_EXPORT_SUCCESS, fileName, chatLogDir.toString()).getAnsiMessage());
        } catch (IOException e) {
            logExceptionError("exportChatLog", e);
            myLogger.atSevere().log(langManager.getMessage(LangKey.LOG_CHAT_EXPORT_FAIL, fileName).getAnsiMessage());
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
                hytaleFoundations.getConfigManager().getDefaultConfig().getPvtMsgClearInterval(),
                hytaleFoundations.getConfigManager().getDefaultConfig().getPvtMsgClearInterval(),
                TimeUnit.MINUTES);
    }

    public ColorEngine getColorEngine() {return colorEngine;}
}
