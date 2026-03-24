package com.raeden.hytale.core.config;

import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.config.containers.ChatConfig;
import com.raeden.hytale.core.config.containers.Config;
import com.raeden.hytale.core.config.containers.MailConfig;
import com.raeden.hytale.modules.chat.ColorManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.raeden.hytale.HytaleFoundations.*;
import static com.raeden.hytale.utils.FileUtils.*;

public class ConfigManager {
    private final HytaleFoundations hytaleFoundations;

    public static final String CONFIG_FILE_NAME = "config.json";
    public static final String PERMISSION_FILE_NAME = "permissions.json";
    public static final String COLORMAP_FILE_NAME = "colormap.json";
    public static final String COMMAND_FILE_NAME = "commands.json";
    public static final String MAIL_FILE_NAME = "mail.json";
    public static final String RANK_FILE_NAME = "rank.json";
    public static final String PARTY_FILE_NAME = "party.json";
    public static final String ECONOMY_FILE_NAME = "economy.json";
    public static final String HOME_FILE_NAME = "home.json";
    public static final String CHAT_FILE_NAME = "chat.json";
    public static final String AFFIX_FILE_NAME = "affix.json";

    public static final String CONFIG_VERSION = "v1.0";
    public static final String CHAT_CONFIG_VERSION = "v1.0";
    public static final String MAIL_CONFIG_VERSION = "v1.0";
    public static final String HOME_CONFIG_VERSION = "v1.0";
    public static final String COMMANDS_CONFIG_VERSION = "v1.0";
    public static final String COLORMAP_VERSION = "v1.0";
    public static final String AFFIX_VERSION = "v1.0";
    public static final String RANK_VERSION = "v1.0";
    public static final String PERMISSION_VERSION = "v1.0";

    private final Path dataDirectory;
    public static Path CONFIG_PATH;
    public static Path CHAT_CONFIG_PATH;
    public static Path MAIL_CONFIG_PATH;
    public static Path HOME_CONFIG_PATH;
    public static Path COMMAND_FILE_PATH;
    public static Path COLORMAP_FILE_PATH;
    public static Path AFFIX_FILE_PATH;
    public static Path RANK_FILE_PATH;
    public static Path PERMISSION_FILE_PATH;

    private Config defaultConfig;
    private ChatConfig defaultChatConfig;
    private MailConfig defaultMailConfig;

    public ConfigManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        dataDirectory = hytaleFoundations.getDataDirectory();

        CONFIG_PATH = dataDirectory.resolve(CONFIG_FILE_NAME);
        CHAT_CONFIG_PATH = dataDirectory.resolve(CHAT_FILE_NAME);
        MAIL_CONFIG_PATH = dataDirectory.resolve(MAIL_FILE_NAME);
        HOME_CONFIG_PATH = dataDirectory.resolve(HOME_FILE_NAME);
        COMMAND_FILE_PATH = dataDirectory.resolve(COMMAND_FILE_NAME);
        COLORMAP_FILE_PATH = dataDirectory.resolve(COLORMAP_FILE_NAME);
        AFFIX_FILE_PATH = dataDirectory.resolve(AFFIX_FILE_NAME);
        RANK_FILE_PATH = dataDirectory.resolve(RANK_FILE_NAME);
        PERMISSION_FILE_PATH = dataDirectory.resolve(PERMISSION_FILE_NAME);

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
        hytaleFoundations.getPermissionManager().loadPermissions();
        hytaleFoundations.getCommandManager().loadCommands();
        if(defaultConfig.isToggleChatModule()) {
            this.defaultChatConfig = loadChatConfigData();
            hytaleFoundations.getChatManager().getAffixManager().loadAffixes();
            hytaleFoundations.getChatManager().getColorManager().loadColors();
        }
        if(defaultConfig.isToggleMailModule()) this.defaultMailConfig = loadMailConfigData();
        if(defaultConfig.isToggleRankModule()) hytaleFoundations.getRankManager().loadRanks();
    }

    // Do not use without try/catch
    public void updateConfigs() {
        updateJsonFile(CONFIG_FILE_NAME, dataDirectory.resolve(CONFIG_FILE_NAME), new Config(), true);
        updateJsonFile(CHAT_FILE_NAME, dataDirectory.resolve(CHAT_FILE_NAME), new ChatConfig(), true);
        updateJsonFile(MAIL_FILE_NAME, dataDirectory.resolve(MAIL_FILE_NAME),new MailConfig(), true);

        // Color file
        ColorManager engine = hytaleFoundations.getChatManager().getColorManager();
        updateJsonFile(engine.getColorFile(), COLORMAP_FILE_PATH, new ColorManager.ColormapHolder(), true);
    }

    private void createErrorLogDir() {createDirectory(ERROR_LOG_DIRECTORY, true);}

    // Save Configs
    private void saveConfig(String... fileName) {
        List<String> filesToSave = Arrays.stream(fileName).toList();
        if(filesToSave.isEmpty() || filesToSave.contains(CONFIG_FILE_NAME)) saveJsonFile(CONFIG_FILE_NAME, CONFIG_PATH, defaultConfig, false);
        if(filesToSave.isEmpty() || filesToSave.contains(CHAT_FILE_NAME)) saveJsonFile(CHAT_FILE_NAME, CHAT_CONFIG_PATH, defaultConfig, false);
        if(filesToSave.isEmpty() || filesToSave.contains(MAIL_FILE_NAME)) saveJsonFile(MAIL_FILE_NAME, MAIL_CONFIG_PATH, defaultConfig, false);
        if(filesToSave.isEmpty() || filesToSave.contains(COMMAND_FILE_NAME)) hytaleFoundations.getCommandManager().saveCommandFile();
        if(filesToSave.isEmpty() || filesToSave.contains(COLORMAP_FILE_NAME)) hytaleFoundations.getChatManager().getColorManager().saveColorFile();
        if(filesToSave.isEmpty() || filesToSave.contains(AFFIX_FILE_NAME)) hytaleFoundations.getChatManager().getAffixManager().saveAffixFile();
        if(filesToSave.isEmpty() || filesToSave.contains(RANK_FILE_NAME)) hytaleFoundations.getRankManager().saveRankFile();
        if(filesToSave.isEmpty() || filesToSave.contains(PERMISSION_FILE_NAME)) hytaleFoundations.getPermissionManager().savePermissionFile();
    }

    // Load Configs
    private Config loadConfigData() {
        Config config = loadJsonFile(CONFIG_FILE_NAME, CONFIG_PATH, Config.class, false);
        if(config !=  null) {
            updateJsonFile(CONFIG_PATH, config, true);
            return config;
        }
        Config defConfig = createDefaultConfig();
        saveJsonFile(CONFIG_FILE_NAME, CONFIG_PATH, defConfig, true);
        return defConfig;
    }

    private ChatConfig loadChatConfigData() {
        ChatConfig chatConfig = loadJsonFile(CHAT_FILE_NAME, CHAT_CONFIG_PATH, ChatConfig.class, false);
        if(chatConfig != null) {
            updateJsonFile(CHAT_CONFIG_PATH, chatConfig, true);
            return chatConfig;
        }
        ChatConfig defaultChatConfig = createDefaultChatConfig();
        saveJsonFile(CHAT_FILE_NAME, CHAT_CONFIG_PATH, defaultChatConfig, true);
        return defaultChatConfig;
    }

    private MailConfig loadMailConfigData() {
        MailConfig mailConfig = loadJsonFile(MAIL_FILE_NAME, MAIL_CONFIG_PATH, MailConfig.class, false);
        if(mailConfig != null) {
            updateJsonFile(MAIL_CONFIG_PATH, mailConfig, true);
            return mailConfig;
        }
        MailConfig defaultMailConfig = createDefaultMailConfig();
        saveJsonFile(MAIL_FILE_NAME, MAIL_CONFIG_PATH, defaultMailConfig, true);
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
        chatConfig.setGradientChunkSize(2);
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
        config.setGenerateDefaultData(true);
        config.setDebugMode(true);
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

