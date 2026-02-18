package com.raeden.hytale.modules.chat;

import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.config.ChatConfig;
import com.raeden.hytale.core.config.ConfigManager;
import com.raeden.hytale.core.data.PlayerProfile;
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
    private final PrefixManager prefixManager;
    private final SuffixManager suffixManager;
    private final Scheduler scheduler;
    private final ColorEngine colorEngine;
    private final ChatConfig chatConfig;

    private final String DEFAULT_CHAT_FORMAT = "{prefix}{player}{suffix} » {message}";

    private final LinkedHashMap<String, String> activeMessengers;
    private final LinkedHashMap<String, String> messageLog; // Time string + Message

    private final Path chatLogDir;

    public ChatManager(HytaleFoundations hytaleFoundations, Scheduler scheduler) {
        this.hytaleFoundations = hytaleFoundations;
        this.scheduler = scheduler;

        prefixManager = new PrefixManager(hytaleFoundations);
        suffixManager = new SuffixManager(hytaleFoundations);
        chatConfig = hytaleFoundations.getConfigManager().getDefaultChatConfig();

        chatLogDir = hytaleFoundations.getDataDirectory().resolve("logs").resolve("chat_logs");
        activeMessengers = new LinkedHashMap<>();
        messageLog = new LinkedHashMap<>();
        // Color Engine
        colorEngine = new ColorEngine(hytaleFoundations);



        createDirectory(chatLogDir, true);
        if(chatConfig.isSaveChatLog()) {
            createChatSaveScheduler();
        }
    }
    // Format Chat
    public String formatChat(PlayerProfile profile, String message) {
        if(profile == null || message.isEmpty()) return "";
        StringBuilder prefixBuilder = new StringBuilder();
        StringBuilder suffixBuilder = new StringBuilder();

        String chatFormat = langManager.getMessage(LangKey.CHAT_FORMAT).getAnsiMessage();
        if(!validateChatFormat(chatFormat)) {
          chatFormat = DEFAULT_CHAT_FORMAT;
        }

        prefixBuilder.ap

        chatFormat.replace("{prefix}", prefixBuilder.toString());
        chatFormat.replace("{suffix}", suffixBuilder.toString());
    }
    private boolean validateChatFormat(String format) {
        if(format.isEmpty()) {
            myLogger.atWarning().log(langManager.getMessage(LangKey.INVALID_CHAT_FORMAT).getAnsiMessage());
            return false;
        }
        if(!format.contains("{message}") ||
                !format.contains("{player}") ||
                !format.contains("{prefix}") ||
                !format.contains("{suffix}")) {
            myLogger.atWarning().log(langManager.getMessage(LangKey.INVALID_CHAT_FORMAT).getAnsiMessage());
            return false;
        }
        return true;
    }

    // Chat Logging
    private void createChatSaveScheduler() {
        scheduler.runTaskTimer("chatSaveScheduler", () -> {
                if(messageLog.isEmpty()) return;
                exportChatLog();
                },
                chatConfig.getChatLogSaveInterval(),
                chatConfig.getChatLogSaveInterval(),
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
    public void removeActiveMessenger(String username) {
        if(activeMessengers.isEmpty() || !activeMessengers.containsKey(username)) return;
        String receiver = activeMessengers.get(username);
        activeMessengers.remove(username);
        activeMessengers.remove(receiver);
    }
    private void clearActiveMessageCache() {
        scheduler.runTaskTimer("clearActiveMessageCache", activeMessengers::clear,
                chatConfig.getPvtMsgClearInterval(),
                chatConfig.getPvtMsgClearInterval(),
                TimeUnit.MINUTES);
    }

    public ColorEngine getColorEngine() {return colorEngine;}
    public PrefixManager getPrefixManager() {return prefixManager;}
    public SuffixManager getSuffixManager() {return suffixManager;}
}
