package com.raeden.hytale.core.config;

import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.config.containers.ChatConfig;
import com.raeden.hytale.core.config.containers.Config;
import com.raeden.hytale.core.config.containers.MailConfig;
import com.raeden.hytale.utils.ColorEngine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.raeden.hytale.HytaleFoundations.*;
import static com.raeden.hytale.utils.FileManager.*;

public class ConfigManager {
    private final HytaleFoundations hytaleFoundations;

    private final String CONFIG_FILE = "config.json";
    private final String MAIL_CONFIG = "mail.json";
    private final String RANK_CONFIG = "rank.json";
    private final String PARTY_CONFIG = "party.json";
    private final String ECONOMY_CONFIG = "economy.json";
    private final String CHAT_CONFIG = "chat.json";

    private Config defaultConfig;
    private ChatConfig defaultChatConfig;
    private MailConfig defaultMailConfig;

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
        this.defaultMailConfig = loadMailConfigData();
        createErrorLogDir();
    }

    public void reloadPlugin() {
        if(!Files.exists(dataDir)) loadConfigs();
        this.defaultConfig = loadConfigData();
        this.defaultChatConfig = loadChatConfigData();
        this.defaultMailConfig = loadMailConfigData();
        hytaleFoundations.getChatManager().getAffixManager().loadAffixes();
        hytaleFoundations.getChatManager().getColorEngine().loadColors();
    }

    public void updateConfigs() {
        updateJsonFile(CONFIG_FILE, dataDir.resolve(CONFIG_FILE), new Config(), true);
        updateJsonFile(CHAT_CONFIG, dataDir.resolve(CHAT_CONFIG), new ChatConfig(), true);
        updateJsonFile(MAIL_CONFIG, dataDir.resolve(MAIL_CONFIG),new MailConfig(), true);

        // Color file
        ColorEngine engine = hytaleFoundations.getChatManager().getColorEngine();
        updateJsonFile(engine.getColorFile(), engine.getColorFilePath(), new ColorEngine.ColormapHolder(), true);
    }

    private void createErrorLogDir() {
        createDirectory(ERROR_LOG_DIRECTORY, true);
    }

    // Load Configs
    private Config loadConfigData() {
        Path configPath = dataDir.resolve(CONFIG_FILE);
        Config config = loadJsonFile(CONFIG_FILE, configPath, Config.class, true);
        if(config !=  null) {
            updateJsonFile(configPath, config, true);
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
            updateJsonFile(chatConfigPath, chatConfig, true);
            return chatConfig;
        }
        ChatConfig defaultChatConfig = createDefaultChatConfig();
        saveJsonFile(CHAT_CONFIG, chatConfigPath, defaultChatConfig, true);
        return defaultChatConfig;
    }

    private MailConfig loadMailConfigData() {
        Path mailConfigPath = dataDir.resolve(MAIL_CONFIG);
        MailConfig mailConfig = loadJsonFile(MAIL_CONFIG, mailConfigPath, MailConfig.class, true);
        if(mailConfig != null) {
            updateJsonFile(mailConfigPath, mailConfig, true);
            return mailConfig;
        }
        MailConfig defaultMailConfig = createDefaultMailConfig();
        saveJsonFile(MAIL_CONFIG, mailConfigPath, defaultMailConfig, true);
        return defaultMailConfig;
    }

    // Create Default Configs
    private MailConfig createDefaultMailConfig() {
        MailConfig mailConfig = new MailConfig();
        mailConfig.setVersion(MAIL_CONFIG_VERSION);
        mailConfig.setAllowGifting(true);
        mailConfig.setMaxGiftPerMail(5);
        mailConfig.setMaxMailLines(32);
        mailConfig.setMaxMailPerDay(10);
        mailConfig.setMaxInboxSize(50);
        return mailConfig;
    }

    private ChatConfig createDefaultChatConfig() {
        ChatConfig chatConfig = new ChatConfig();
        chatConfig.setVersion(CHAT_CONFIG_VERSION);
        chatConfig.setShowNickNames(true);
        chatConfig.setShowPrefix(true);
        chatConfig.setShowSuffix(true);
        chatConfig.setMaxSuffix(2);
        chatConfig.setMaxPrefix(2);
        chatConfig.setRemoveIps(true);
        chatConfig.setAllowPlayerChatColors(true);
        chatConfig.setRemoveLinks(true);
        chatConfig.setCensorCurseWords(true);
        chatConfig.setToggleCensorWordList(true);
        chatConfig.setPvtMsgClearInterval(5);
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
        config.setToggleMailModule(true);
        config.setToggleRankModule(true);
        config.setToggleHomesModule(true);
        config.setTogglePartyModule(true);
        config.setToggleEconomyModule(true);
        config.setToggleAnalyticsModule(true);
        config.setToggleDiscordModule(true);
        return config;
    }

    public Config getDefaultConfig() {return defaultConfig;}
    public ChatConfig getDefaultChatConfig() {return defaultChatConfig;}
    public MailConfig getDefaultMailConfig() {return defaultMailConfig;}
}

