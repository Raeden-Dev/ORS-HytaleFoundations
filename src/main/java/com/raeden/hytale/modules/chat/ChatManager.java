package com.raeden.hytale.modules.chat;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.config.ChatConfig;
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
import java.util.Map;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.raeden.hytale.HytaleFoundations.langManager;
import static com.raeden.hytale.HytaleFoundations.myLogger;
import static com.raeden.hytale.utils.FileManager.createDirectory;
import static com.raeden.hytale.utils.FileManager.logExceptionError;

public class ChatManager {
    private final HytaleFoundations hytaleFoundations;
    private final AffixManager affixManager;
    private final Scheduler scheduler;
    private final ColorEngine colorEngine;
    private final ChatConfig chatConfig;

    private String CHAT_FORMAT;

    private final Map<String, String> activeMessengers;
    private final Map<String, String> messageLog; // Time string + Message

    private final Path chatLogDir;

    public ChatManager(HytaleFoundations hytaleFoundations, Scheduler scheduler) {
        this.hytaleFoundations = hytaleFoundations;
        this.scheduler = scheduler;

        affixManager = new AffixManager(hytaleFoundations);
        chatConfig = hytaleFoundations.getConfigManager().getDefaultChatConfig();

        chatLogDir = hytaleFoundations.getDataDirectory().resolve("logs").resolve("chat_logs");
        activeMessengers = new ConcurrentHashMap<>();
        messageLog = new ConcurrentHashMap<>();
        // Color Engine
        colorEngine = new ColorEngine(hytaleFoundations);

        setupChatFormat();

        createDirectory(chatLogDir, true);
        if(chatConfig.isSaveChatLog()) {
            createChatSaveScheduler();
        }
    }
    // Format Chat
    public String formatChat(PlayerProfile profile, String username, String message) {
        if(profile == null || message.isEmpty()) return "";
        String chatFormat = CHAT_FORMAT;
        String prefix = String.join("", profile.getActivePrefix().values());
        String suffix = String.join("", profile.getActiveSuffix().values());
        String displayName = username;
        if (chatConfig.isShowNickNames() && profile.isShowNickname() && !profile.getNickname().isEmpty()) {
            displayName = profile.getNickname();
        } else {
            if(colorEngine.isColorCodeAvailable(profile.getUsernameColorCode())) {
                displayName = profile.getUsernameColorCode() + username;
            }
        }
        return chatFormat
                .replace("{prefix}", prefix + "&r")
                .replace("{suffix}", suffix + "&r")
                .replace("{player}", displayName + "&r")
                .replace("{message}", message);
    }
    public void setupChatFormat() {
        if(langManager != null) {
            String chatFormat = langManager.getMessage(LangKey.CHAT_FORMAT).getAnsiMessage();
            if(validateChatFormat(chatFormat)) {
                CHAT_FORMAT = chatFormat;
            } else {
                CHAT_FORMAT = "{prefix} {player} {suffix} » {message}";
            }
        }
    }
    private boolean validateChatFormat(String format) {
        if (format.isEmpty()) return false;
        return format.contains("{message}") &&
                format.contains("{player}") &&
                format.contains("{prefix}") &&
                format.contains("{suffix}");
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
            myLogger.atInfo().log(langManager.getMessage(LangKey.LOG_CHAT_EXPORT_SUCCESS,true, fileName, chatLogDir.toString()).getAnsiMessage());
        } catch (IOException e) {
            logExceptionError("exportChatLog", e);
            myLogger.atSevere().log(langManager.getMessage(LangKey.LOG_CHAT_EXPORT_FAIL,true, fileName).getAnsiMessage());
        }
    }

    public void addMessageToLog(String message) {
        messageLog.put(TimeUtils.getTimeNow(), message);
    }

    // Nicknaming
    public boolean validateNickname(PlayerRef caller, String nickname) {
        if(nickname.length() <= 2) {
            caller.sendMessage(langManager.getMessage(LangKey.NICKNAME_LENGTH, false));
            return false;
        }
        return true;
    }

    // Private Messaging
    public Map<String, String> getActiveMessengers() {
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
    public AffixManager getAffixManager() {return affixManager;}
}
