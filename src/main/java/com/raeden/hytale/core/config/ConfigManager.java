package com.raeden.hytale.core.config;

import com.raeden.hytale.HytaleEssentials;
import com.raeden.hytale.lang.LangKey;
import com.raeden.hytale.lang.LangManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.raeden.hytale.HytaleEssentials.*;

public class ConfigManager {
    private final HytaleEssentials hytaleEssentials;

    private final String CONFIG_FILE = "config.json";
    private final String LANG_FILE = "en-us.json";
    private final String RANK_CONFIG = "rank.json";
    private final String PARTY_CONFIG = "party.json";
    private final String ECONOMY_CONFIG = "economy.json";
    private final String CHAT_CONFIG = "chat.json";

    private Config defaultConfig;

    private final Path dataDir;

    public ConfigManager(HytaleEssentials hytaleEssentials) {
        this.hytaleEssentials = hytaleEssentials;
        dataDir = hytaleEssentials.getDataDirectory();
    }

    public void loadConfigs() {
        try {
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
                myLogger.atInfo().log(langManager.getMessage(null, LangKey.CREATE_DIRECTORY_W_LOC, "data directory", dataDir.toString()).toString());
            }

            defaultConfig = loadConfigData();

        } catch (IOException e) {
            myLogger.atSevere().log(langManager.getMessage(null, LangKey.CREATE_DIRECTORY_FAIL, "data directory").toString());
            this.defaultConfig = createDefaultConfig();
        }
    }

    private Config loadConfigData() {
        Path configFile = dataDir.resolve(CONFIG_FILE);
        if (Files.exists(configFile)) {
            try {
                String readConfig = Files.readString(configFile, StandardCharsets.UTF_8);
                Config config = GSON.fromJson(readConfig, Config.class);

                if (config == null) {
                    myLogger.atSevere().log(langManager.getMessage(null, LangKey.READ_FAILURE_W_LOC, "config.json", "data directory").toString());
                } else {
                    return config;
                }
            } catch (IOException e) {
                myLogger.atSevere().log(langManager.getMessage(null, LangKey.READ_FAILURE_W_LOC, "config.json", dataDir.toString()).toString());
            }
        }

        Config defConfig = createDefaultConfig();
        saveConfigFile(defConfig);
        return defConfig;
    }

    private void saveConfigFile(Config config) {
        Path savePath = dataDir.resolve("config.json");
        String toJson = GSON.toJson(config);
        try {
            Files.writeString(savePath, toJson, StandardCharsets.UTF_8);
        } catch (IOException e) {
            myLogger.atSevere().log(langManager.getMessage(null, LangKey.SAVE_FAILURE_W_LOC, "config.json", "data directory").toString());
        }
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

