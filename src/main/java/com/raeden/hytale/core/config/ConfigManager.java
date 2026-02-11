package com.raeden.hytale.core.config;

import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.lang.LangKey;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.raeden.hytale.HytaleFoundations.*;
import static com.raeden.hytale.utils.FileManager.loadJsonFile;
import static com.raeden.hytale.utils.FileManager.saveJsonFile;

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
        try {
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
                myLogger.atInfo().log(langManager.getMessage(null, LangKey.CREATE_DIRECTORY_W_LOC, "data directory", dataDir.toString()).getAnsiMessage());
            }

            defaultConfig = loadConfigData();

        } catch (IOException e) {
            myLogger.atSevere().log(langManager.getMessage(null, LangKey.CREATE_DIRECTORY_FAIL, "data directory").getAnsiMessage());
            this.defaultConfig = createDefaultConfig();
        }
    }

    private Config loadConfigData() {
        Path configFile = dataDir.resolve(CONFIG_FILE);
        Config config = loadJsonFile(CONFIG_FILE, configFile, Config.class, true);
        if(config !=  null) {
            return config;
        }
        Config defConfig = createDefaultConfig();
        saveJsonFile(CONFIG_FILE, configFile, defConfig, true);
        return defConfig;
    }

    private ChatConfig createDefaultChatConfig() {
        ChatConfig chatConfig = new ChatConfig();
        //chatConfig.

        return chatConfig;
    }

    private Config createDefaultConfig() {
        Config config = new Config();
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
        config.setPvtMsgClearInterval(5);
        config.setSaveChatLog(true);
        config.setChatLogSaveInterval(10);
        return config;
    }

    public Config getDefaultConfig() {
        return defaultConfig;
    }
    public void setDefaultConfig(Config config) {
        this.defaultConfig = config;
    }

}

