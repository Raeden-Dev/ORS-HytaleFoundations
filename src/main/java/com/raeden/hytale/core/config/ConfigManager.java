package com.raeden.hytale.core.config;

import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.config.containers.ChatConfig;
import com.raeden.hytale.core.config.containers.Config;
import com.raeden.hytale.core.config.containers.MailConfig;
import com.raeden.hytale.core.config.containers.RankConfig;
import com.raeden.hytale.modules.chat.ColorManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.raeden.hytale.HytaleFoundations.*;
import static com.raeden.hytale.utils.FileManager.*;

public class ConfigManager {
    private final HytaleFoundations hytaleFoundations;

    public static final String CONFIG_FILENAME = "config.json";
    public static final String MAIL_FILENAME = "mail.json";
    public static final String RANK_FILENAME = "rank.json";
    public static final String PARTY_FILENAME = "party.json";
    public static final String ECONOMY_FILENAME = "economy.json";
    public static final String CHAT_FILENAME = "chat.json";
    public static final String COLORMAP_FILENAME = "colormap.json";
    public static final String AFFIX_FILENAME = "affix.json";
    public static final String PERMISSION_FILENAME = "permissions.json";

    public static String CONFIG_VERSION = "v1.0";
    public static String CHAT_CONFIG_VERSION = "v1.0";
    public static String MAIL_CONFIG_VERSION = "v1.0";
    public static String COLORMAP_VERSION = "v1.0";
    public static String AFFIX_VERSION = "v1.0";
    public static String RANK_VERSION = "v1.0";

    private Config defaultConfig;
    private ChatConfig defaultChatConfig;
    private MailConfig defaultMailConfig;

    private final Path dataDirectory;

    public ConfigManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        dataDirectory = hytaleFoundations.getDataDirectory();
        loadConfigs();
    }

    public void loadConfigs() {
        createDirectory(dataDirectory, true);
        this.defaultConfig = loadConfigData();
        if(defaultConfig.isToggleChatModule()) this.defaultChatConfig = loadChatConfigData();
        if(defaultConfig.isToggleMailModule()) this.defaultMailConfig = loadMailConfigData();
        createErrorLogDir();
    }

    // Do not use without try/catch
    public void reloadPlugin() {
        if(!Files.exists(dataDirectory)) loadConfigs();
        this.defaultConfig = loadConfigData();
        if(defaultConfig.isToggleChatModule()) {
            this.defaultChatConfig = loadChatConfigData();
            hytaleFoundations.getChatManager().getAffixManager().loadAffixes();
            hytaleFoundations.getChatManager().getColorEngine().loadColors();
        }
        if(defaultConfig.isToggleMailModule()) this.defaultMailConfig = loadMailConfigData();
        if(defaultConfig.isToggleRankModule()) hytaleFoundations.getRankManager().loadRanks();
    }

    // Do not use without try/catch
    public void updateConfigs() {
        updateJsonFile(CONFIG_FILENAME, dataDirectory.resolve(CONFIG_FILENAME), new Config(), true);
        updateJsonFile(CHAT_FILENAME, dataDirectory.resolve(CHAT_FILENAME), new ChatConfig(), true);
        updateJsonFile(MAIL_FILENAME, dataDirectory.resolve(MAIL_FILENAME),new MailConfig(), true);

        // Color file
        ColorManager engine = hytaleFoundations.getChatManager().getColorEngine();
        updateJsonFile(engine.getColorFile(), engine.getColorFilePath(), new ColorManager.ColormapHolder(), true);
    }

    private void createErrorLogDir() {createDirectory(ERROR_LOG_DIRECTORY, true);}

    // Load Configs
    private Config loadConfigData() {
        Path configPath = dataDirectory.resolve(CONFIG_FILENAME);
        Config config = loadJsonFile(CONFIG_FILENAME, configPath, Config.class, true);
        if(config !=  null) {
            updateJsonFile(configPath, config, true);
            return config;
        }
        Config defConfig = createDefaultConfig();
        saveJsonFile(CONFIG_FILENAME, configPath, defConfig, true);
        return defConfig;
    }

    private ChatConfig loadChatConfigData() {
        Path chatConfigPath = dataDirectory.resolve(CHAT_FILENAME);
        ChatConfig chatConfig = loadJsonFile(CHAT_FILENAME, chatConfigPath, ChatConfig.class, true);
        if(chatConfig != null) {
            updateJsonFile(chatConfigPath, chatConfig, true);
            return chatConfig;
        }
        ChatConfig defaultChatConfig = createDefaultChatConfig();
        saveJsonFile(CHAT_FILENAME, chatConfigPath, defaultChatConfig, true);
        return defaultChatConfig;
    }

    private MailConfig loadMailConfigData() {
        Path mailConfigPath = dataDirectory.resolve(MAIL_FILENAME);
        MailConfig mailConfig = loadJsonFile(MAIL_FILENAME, mailConfigPath, MailConfig.class, true);
        if(mailConfig != null) {
            updateJsonFile(mailConfigPath, mailConfig, true);
            return mailConfig;
        }
        MailConfig defaultMailConfig = createDefaultMailConfig();
        saveJsonFile(MAIL_FILENAME, mailConfigPath, defaultMailConfig, true);
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
        chatConfig.setShowChatMsgPrefix(true);
        chatConfig.setShowNickname(true);
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
        config.addDataCluster("hub",List.of("world"));
        config.addDataCluster("gamemode_1",List.of("world_gm1_hub","world_gm1_arena"));
        return config;
    }

    public Config getDefaultConfig() {return defaultConfig;}
    public ChatConfig getDefaultChatConfig() {return defaultChatConfig;}
    public MailConfig getDefaultMailConfig() {return defaultMailConfig;}
}

