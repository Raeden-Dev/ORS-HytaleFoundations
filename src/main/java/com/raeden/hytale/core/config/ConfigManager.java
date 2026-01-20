package com.raeden.hytale.core.config;

import com.raeden.hytale.HytaleEssentials;
import com.raeden.hytale.lang.LangKey;
import com.raeden.hytale.lang.LangManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.raeden.hytale.HytaleEssentials.GSON;
import static com.raeden.hytale.HytaleEssentials.myLogger;

public class ConfigManager {
    private final HytaleEssentials hytaleEssentials;
    private final LangManager langManager;

    private final String CONFIG_FILE = "config.json";
    private final String LANG_FILE = "en-us.json";
    private final String RANK_CONFIG = "rank.json";
    private final String PARTY_CONFIG = "party.json";
    private final String ECONOMY_CONFIG = "economy.json";
    private final String CHAT_CONFIG = "chat.json";

    private DefaultConfig defaultConfig;

    private final Path dataDir;

    public ConfigManager(HytaleEssentials hytaleEssentials) {
        this.hytaleEssentials = hytaleEssentials;
        this.langManager = hytaleEssentials.getLangManager();
        dataDir = hytaleEssentials.getDataDirectory();
    }

    public void loadConfigs() {
        try {
            if(!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
                myLogger.atInfo().log(langManager.getMessage(null, LangKey.CREATE_DIRECTORY_W_LOC, "data directory", dataDir.toString()).toString());
            }

            this.defaultConfig = loadConfigData();

        } catch (IOException e) {
            myLogger.atSevere().log(langManager.getMessage(null, LangKey.CREATE_DIRECTORY_FAIL, "data directory").toString());
            this.defaultConfig = createDefaultConfig();
        }
    }

    private DefaultConfig loadConfigData() {
        Path configFile = dataDir.resolve(CONFIG_FILE);
        if(Files.exists(configFile)) {
            try {
                String readConfig = Files.readString(configFile, StandardCharsets.UTF_8);
                DefaultConfig config = GSON.fromJson(readConfig, DefaultConfig.class);

                if(config == null) {
                    myLogger.atSevere().log(langManager.getMessage(null, LangKey.READ_FAILURE_W_LOC, "config.json", "data directory").toString());
                } else {
                    return config;
                }
            } catch (IOException e) {
                myLogger.atSevere().log(langManager.getMessage(null, LangKey.READ_FAILURE_W_LOC, "config.json", dataDir.toString()).toString());
            }
        }

        DefaultConfig defConfig = createDefaultConfig();
        saveConfigFile(defConfig);
        return defConfig;
    }

    private void saveConfigFile(DefaultConfig config) {
        Path savePath = dataDir.resolve("config.json");
        String toJson = GSON.toJson(config);
        try {
            Files.writeString(savePath, toJson, StandardCharsets.UTF_8);
        } catch (IOException e) {
            myLogger.atSevere().log("Failed to save config.json to data directory!");
        }
    }

    private DefaultConfig createDefaultConfig() {
        DefaultConfig config = new DefaultConfig();

        config.LANG = "en-us.json";
        config.DATA_STORAGE_TYPE = "json";
        config.TOGGLE_DEBUG = true;
        config.TOGGLE_ADMIN_MODULE = true;
        config.TOGGLE_CHAT_MODULE = true;
        config.TOGGLE_PARTY_MODULE = true;
        config.TOGGLE_ECONOMY_MODULE = true;
        config.TOGGLE_ANALYTICS_MODULE = true;
        config.TOGGLE_DISCORD_MODULE = true;
        config.PLAYER_DATA_SAVE_INTERVAL = "LOGOUT";

        return config;
    }

    public DefaultConfig getDefaultConfig() {return defaultConfig;}
    public void setDefaultConfig(DefaultConfig defaultConfig) {this.defaultConfig = defaultConfig;}

    public static class DefaultConfig {
        private String LANG;
        private String DATA_STORAGE_TYPE;
        private String PLAYER_DATA_SAVE_INTERVAL; // LOGOUT, 5M, 10M, 30M, 1H
        private boolean TOGGLE_DEBUG;
        private boolean TOGGLE_ADMIN_MODULE;
        private boolean TOGGLE_CHAT_MODULE;
        private boolean TOGGLE_PARTY_MODULE;
        private boolean TOGGLE_ECONOMY_MODULE;
        private boolean TOGGLE_ANALYTICS_MODULE;
        private boolean TOGGLE_DISCORD_MODULE;

        public String getLANG() {return LANG;}
        public void setLANG(String LANG) {this.LANG = LANG;}

        public String getDATA_STORAGE_TYPE() {return DATA_STORAGE_TYPE;}
        public void setDATA_STORAGE_TYPE(String DATA_STORAGE_TYPE) {this.DATA_STORAGE_TYPE = DATA_STORAGE_TYPE;}

        public String getPLAYER_DATA_SAVE_INTERVAL() {return PLAYER_DATA_SAVE_INTERVAL;}
        public void setPLAYER_DATA_SAVE_INTERVAL(String PLAYER_DATA_SAVE_INTERVAL) {this.PLAYER_DATA_SAVE_INTERVAL = PLAYER_DATA_SAVE_INTERVAL;}

        public boolean isTOGGLE_DEBUG() {return TOGGLE_DEBUG;}
        public void setTOGGLE_DEBUG(boolean TOGGLE_DEBUG) {this.TOGGLE_DEBUG = TOGGLE_DEBUG;}

        public boolean isTOGGLE_ADMIN_MODULE() {return TOGGLE_ADMIN_MODULE;}
        public void setTOGGLE_ADMIN_MODULE(boolean TOGGLE_ADMIN_MODULE) {this.TOGGLE_ADMIN_MODULE = TOGGLE_ADMIN_MODULE;}

        public boolean isTOGGLE_CHAT_MODULE() {return TOGGLE_CHAT_MODULE;}
        public void setTOGGLE_CHAT_MODULE(boolean TOGGLE_CHAT_MODULE) {this.TOGGLE_CHAT_MODULE = TOGGLE_CHAT_MODULE;}

        public boolean isTOGGLE_PARTY_MODULE() {return TOGGLE_PARTY_MODULE;}
        public void setTOGGLE_PARTY_MODULE(boolean TOGGLE_PARTY_MODULE) {this.TOGGLE_PARTY_MODULE = TOGGLE_PARTY_MODULE;}

        public boolean isTOGGLE_ECONOMY_MODULE() {return TOGGLE_ECONOMY_MODULE;}
        public void setTOGGLE_ECONOMY_MODULE(boolean TOGGLE_ECONOMY_MODULE) {this.TOGGLE_ECONOMY_MODULE = TOGGLE_ECONOMY_MODULE;}

        public boolean isTOGGLE_ANALYTICS_MODULE() {return TOGGLE_ANALYTICS_MODULE;}
        public void setTOGGLE_ANALYTICS_MODULE(boolean TOGGLE_ANALYTICS_MODULE) {this.TOGGLE_ANALYTICS_MODULE = TOGGLE_ANALYTICS_MODULE;}

        public boolean isTOGGLE_DISCORD_MODULE() {return TOGGLE_DISCORD_MODULE;}
        public void setTOGGLE_DISCORD_MODULE(boolean TOGGLE_DISCORD_MODULE) {this.TOGGLE_DISCORD_MODULE = TOGGLE_DISCORD_MODULE;}
    }
}
