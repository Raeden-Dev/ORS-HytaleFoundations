package com.raeden.hytale.core.config;

import com.raeden.hytale.HytaleFoundations;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.raeden.hytale.HytaleFoundations.*;
import static com.raeden.hytale.utils.FileManager.*;

public class ConfigManager {
    private final HytaleFoundations hytaleFoundations;

    private final String CONFIG_FILE = "config.json";
    private final String LANG_FILE = "en-us.json";
    private final String RANK_CONFIG = "rank.json";
    private final String PARTY_CONFIG = "party.json";
    private final String ECONOMY_CONFIG = "economy.json";
    private final String CHAT_CONFIG = "chat.json";

    private Config defaultConfig;
    private ChatConfig defaultChatConfig;

    private final Path dataDir;

    public ConfigManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        dataDir = hytaleFoundations.getDataDirectory();
        loadConfigs();
    }

    public void loadConfigs() {
        createDirectory(dataDir, true);
        this.defaultConfig = loadConfigData();
        this.defaultChatConfig = loadChatConfigData();
        createErrorLogDir();
    }

    private void createErrorLogDir() {
        createDirectory(errorLogDirectory, true);
    }

    private Config loadConfigData() {
        Path configPath = dataDir.resolve(CONFIG_FILE);
        Config config = loadJsonFile(CONFIG_FILE, configPath, Config.class, true);
        if(config !=  null) {
            return config;
        }
        Config defConfig = createDefaultConfig();
        saveJsonFile(CONFIG_FILE, configPath, defConfig, true);
        return defConfig;
    }

    private ChatConfig loadChatConfigData() {
        Path chatConfigPath = dataDir.resolve(CHAT_CONFIG);
        ChatConfig chatConfig = loadJsonFile(CHAT_CONFIG, chatConfigPath, ChatConfig.class, true);
        if(chatConfig != null) {
            return chatConfig;
        }
        ChatConfig defaultChatConfig = createDefaultChatConfig();
        saveJsonFile(CHAT_CONFIG, chatConfigPath, defaultChatConfig, true);
        return defaultChatConfig;
    }

    private ChatConfig createDefaultChatConfig() {
        ChatConfig chatConfig = new ChatConfig();
        chatConfig.setVersion(CHAT_CONFIG_VERSION);
        chatConfig.setShowNickNames(true);
        chatConfig.setShowPrefix(true);
        chatConfig.setShowSuffix(true);
        chatConfig.setMaxSuffix(2);
        chatConfig.setRemoveIps(true);
        chatConfig.setAllowPlayerChatColors(true);
        chatConfig.setRemoveLinks(true);
        chatConfig.setCensorCurseWords(true);
        chatConfig.setToggleCensorWordList(true);
        chatConfig.setPvtMsgClearInterval(5);
        chatConfig.setSaveChatLog(true);
        chatConfig.setChatLogSaveInterval(10);
        List<String> censorWordList = new ArrayList<>(List.of("lgbt", "LGBTQ+", "woke", "nigga", "nigger"));
        chatConfig.setCensorWordList(censorWordList);
        List<String> curseWordList = new ArrayList<>(List.of("fuck", "cunt", "faggot", "pussy", "cocksucker", "dumbass"));
        chatConfig.setCurseWordList(curseWordList);
        return chatConfig;
    }

    private Config createDefaultConfig() {
        Config config = new Config();
        config.setVersion(CONFIG_VERSION);
        config.setLang("en-us.json");
        config.setDataStorageType("json");
        config.setPlayerDataSaveInterval(15);
        config.setToggleDebug(true);
        config.setToggleAdminModule(true);
        config.setToggleChatModule(true);
        config.setTogglePartyModule(true);
        config.setToggleEconomyModule(true);
        config.setToggleAnalyticsModule(true);
        config.setToggleDiscordModule(true);
        config.setToggleRankModule(true);
        return config;
    }

    public Config getDefaultConfig() {return defaultConfig;}
    public ChatConfig getDefaultChatConfig() {return defaultChatConfig;}
}

