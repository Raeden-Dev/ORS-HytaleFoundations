package com.raeden.hytale.modules.chat;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.config.containers.ChatConfig;
import com.raeden.hytale.core.player.PlayerProfile;
import com.raeden.hytale.core.lang.LangKey;
import com.raeden.hytale.utils.FileUtils;
import com.raeden.hytale.utils.SchedulerUtils;
import com.raeden.hytale.utils.TimeUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.raeden.hytale.HytaleFoundations.LM;
import static com.raeden.hytale.HytaleFoundations.myLogger;
import static com.raeden.hytale.core.config.ConfigManager.CHAT_LOG_FILE_NAME;
import static com.raeden.hytale.core.config.ConfigManager.CHAT_LOG_PATH;
import static com.raeden.hytale.utils.FileUtils.createDirectory;
import static com.raeden.hytale.utils.FileUtils.logError;

public class ChatManager {
    private final AffixManager affixManager;
    private final SchedulerUtils schedulerUtils;
    private final ColorManager colorManager;
    private final ChatConfig chatConfig;
    private String chatFormat;
    private final Map<String, String> activeMessengers;
    private final Map<String, String> messageLog; // Time string + Message

    public ChatManager(HytaleFoundations hytaleFoundations, SchedulerUtils schedulerUtils) {
        this.schedulerUtils = schedulerUtils;

        affixManager = new AffixManager(hytaleFoundations);
        chatConfig = hytaleFoundations.getConfigManager().getDefaultChatConfig();
        activeMessengers = new ConcurrentHashMap<>();
        messageLog = new ConcurrentHashMap<>();
        // Color Engine
        colorManager = new ColorManager(hytaleFoundations);

        setupChatFormat();

        createDirectory(CHAT_LOG_PATH, true);
        if(chatConfig.getChatLogSaveInterval() != 0) {
            createChatSaveScheduler();
        }
    }
    // Format Chat
    public String formatChat(PlayerProfile profile, String username, String message) {
        if(profile == null || message.isEmpty()) return "";
        String chatFormat = this.chatFormat;
        String prefix = String.join("", profile.getActivePrefix().values());
        String suffix = String.join("", profile.getActiveSuffix().values());
        String displayName = username;
        if (chatConfig.isShowNickname() && profile.isShowNickname() && !profile.getNickname().isEmpty()) {
            displayName = profile.getNickname();
        } else {
            if(colorManager.validateUsernameDisplayColor(profile.getUsername().getLast(), profile.getUsernameColorCode())) {
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
        if(LM != null) {
            String chatFormat = LM.getMessage(LangKey.CHAT_FORMAT).getAnsiMessage();
            if(validateChatFormat(chatFormat)) {
                this.chatFormat = chatFormat;
            } else {
                this.chatFormat = "{prefix} {player} {suffix} » {message}";
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
        schedulerUtils.runTaskTimer("chatSaveScheduler", () -> {
                if(messageLog.isEmpty()) return;
                exportChatLog();
                },
                chatConfig.getChatLogSaveInterval(),
                chatConfig.getChatLogSaveInterval(),
                TimeUnit.MINUTES);
    }

    private void exportChatLog() {
        if(messageLog.isEmpty()) return;

        String fileName = CHAT_LOG_FILE_NAME;
        File logFile = new File(CHAT_LOG_PATH.toString(), fileName);

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
            writer.write("---- x ----");
            myLogger.atInfo().log(LM.getConsoleMessage(LangKey.LOG_EXPORT_SUCCESS,fileName, "chat", fileName).getAnsiMessage());
        } catch (IOException e) {
            FileUtils.logError("exportChatLog", e);
            myLogger.atSevere().log(LM.getConsoleMessage(LangKey.LOG_EXPORT_FAIL, "chat", fileName).getAnsiMessage());
        }
    }

    public void addMessageToLog(String message) {
        messageLog.put(TimeUtils.getTimeNow(), message);
    }

    // Nicknaming
    public boolean validateNickname(PlayerRef caller, String nickname) {
        if(nickname.length() <= 2) {
            caller.sendMessage(LM.getPlayerMessage(caller.getUsername(), LangKey.NICKNAME_LENGTH));
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
        schedulerUtils.runTaskTimer("clearActiveMessageCache", activeMessengers::clear,
                chatConfig.getPvtMsgClearInterval(),
                chatConfig.getPvtMsgClearInterval(),
                TimeUnit.MINUTES);
    }

    public ColorManager getColorManager() {return colorManager;}
    public AffixManager getAffixManager() {return affixManager;}
}
