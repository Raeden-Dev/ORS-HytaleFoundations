package com.raeden.hytale.core.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypixel.hytale.logger.HytaleLogger;
import com.raeden.hytale.HytaleEssentials;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private final HytaleLogger logger;
    private final HytaleEssentials hytaleEssentials;

    private final String CONFIG_FILE = "config.json";
    private final String LANG_FILE = "en-us.json";
    private final String RANK_CONFIG = "rank.json";
    private final String PARTY_CONFIG = "party.json";
    private final String ECONOMY_CONFIG = "economy.json";
    private final String CHAT_CONFIG = "chat.json";

    private defaultConfig defaultConfig;

    private final Path dataDir;
    private final Gson gson;

    public ConfigManager(HytaleEssentials hytaleEssentials, HytaleLogger logger) {
        this.hytaleEssentials = hytaleEssentials;
        dataDir = hytaleEssentials.getDataDirectory();
        gson = new GsonBuilder().setPrettyPrinting().create();
        this.logger = logger;
    }

    public void loadConfig() {
        try {
            if(!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
                logger.atInfo().log("Created data folder: " + dataDir);
            }

            this.defaultConfig = loadConfigData();

        } catch (IOException e) {
            logger.atSevere().log("Failed to create config.json in data directory!");
            this.defaultConfig = createDefaultConfig();
        }
    }

    private defaultConfig loadConfigData() {
        Path configFile = dataDir.resolve(CONFIG_FILE);
        if(Files.exists(configFile)) {
            try {
                String readConfig = Files.readString(configFile, StandardCharsets.UTF_8);
                defaultConfig config = gson.fromJson(readConfig, this.defaultConfig.getClass());

                if(config == null) {
                    logger.atSevere().log("Failed to read config.json from data directory!");
                } else {
                    return config;
                }
            } catch (IOException e) {
                logger.atSevere().log("Failed to read config.json from data directory!");
            }
        }

        defaultConfig defConfig = createDefaultConfig();
        saveConfigFile(defConfig);
        return defConfig;
    }

    private void saveConfigFile(defaultConfig config) {
        Path savePath = dataDir.resolve("config.json");
        String toJson = gson.toJson(config);
        try {
            Files.writeString(savePath, toJson, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.atSevere().log("Failed to save config.json to data directory!");
        }
    }

    private defaultConfig createDefaultConfig() {
        defaultConfig config = new defaultConfig();

        config.LANG = "en-us.json";
        config.TOGGLE_ADMIN_MODULE = true;
        config.TOGGLE_CHAT_MODULE = true;
        config.TOGGLE_PARTY_MODULE = true;
        config.TOOGLE_ECONOMY_MODULE = true;
        config.TOGGLE_ANALYTICS_MODULE = true;

        return config;
    }

    private static class defaultConfig {
        private boolean TOGGLE_ADMIN_MODULE;
        private boolean TOGGLE_CHAT_MODULE;
        private boolean TOGGLE_PARTY_MODULE;
        private boolean TOOGLE_ECONOMY_MODULE;
        private boolean TOGGLE_ANALYTICS_MODULE;
        private String LANG;
    }
}
