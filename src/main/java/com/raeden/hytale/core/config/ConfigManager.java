package com.raeden.hytale.core.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypixel.hytale.logger.HytaleLogger;
import com.raeden.hytale.HytaleEssentials;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

public class ConfigManager {
    private final HytaleLogger logger;
    private final HytaleEssentials hytaleEssentials;

    private static final String CONFIG_FILE = "config.json";
    private static final String LANG_FILE = "en-us.json";
    private static final String RANK_CONFIG = "rank.json";
    private static final String PARTY_CONFIG = "party.json";
    private static final String ECONOMY_CONFIG = "economy.json";
    private static final String CHAT_CONFIG = "chat.json";

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
            if(!Files.exists(dataDir, LinkOption.valueOf(CONFIG_FILE))) {
                Files.createDirectories(dataDir);
                logger.atInfo().log("Created data folder: " + dataDir);
            }
        } catch (IOException e) {
            logger.atSevere().log("Failed to create config.json!");
            createDefaultConfig();
        }
    }

    private void createDefaultConfig() {
    }
}
