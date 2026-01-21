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

    private DefaultConfig defaultConfig;

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

            this.defaultConfig = loadConfigData();

        } catch (IOException e) {
            myLogger.atSevere().log(langManager.getMessage(null, LangKey.CREATE_DIRECTORY_FAIL, "data directory").toString());
            this.defaultConfig = createDefaultConfig();
        }
    }

    private DefaultConfig loadConfigData() {
        Path configFile = dataDir.resolve(CONFIG_FILE);
        if (Files.exists(configFile)) {
            try {
                String readConfig = Files.readString(configFile, StandardCharsets.UTF_8);
                DefaultConfig config = GSON.fromJson(readConfig, DefaultConfig.class);

                if (config == null) {
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
            myLogger.atSevere().log(langManager.getMessage(null, LangKey.SAVE_FAILURE_W_LOC, "config.json", "data directory").toString());
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

    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(DefaultConfig defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

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

        private boolean SAVE_CHAT_LOG;

        public String getLang() {return LANG;}
        public void setLang(String lang) {this.LANG = lang;}

        public String getDataStorageType() {return DATA_STORAGE_TYPE;}
        public void setDataStorageType(String dataStorageType) {this.DATA_STORAGE_TYPE = dataStorageType;}

        public String getPlayerDataSaveInterval() {return PLAYER_DATA_SAVE_INTERVAL;}
        public void setPlayerDataSaveInterval(String playerDataSaveInterval) {this.PLAYER_DATA_SAVE_INTERVAL = playerDataSaveInterval;}

        public boolean isToggleDebug() {return TOGGLE_DEBUG;}
        public void setToggleDebug(boolean toggleDebug) {this.TOGGLE_DEBUG = toggleDebug;}

        public boolean isToggleAdminModule() {return TOGGLE_ADMIN_MODULE;}
        public void setToggleAdminModule(boolean toggleAdminModule) {this.TOGGLE_ADMIN_MODULE = toggleAdminModule;}

        public boolean isToggleChatModule() {return TOGGLE_CHAT_MODULE;}
        public void setToggleChatModule(boolean toggleChatModule) {this.TOGGLE_CHAT_MODULE = toggleChatModule;}

        public boolean isTogglePartyModule() {return TOGGLE_PARTY_MODULE;}
        public void setTogglePartyModule(boolean togglePartyModule) {this.TOGGLE_PARTY_MODULE = togglePartyModule;}

        public boolean isToggleEconomyModule() {return TOGGLE_ECONOMY_MODULE;}
        public void setToggleEconomyModule(boolean toggleEconomyModule) {this.TOGGLE_ECONOMY_MODULE = toggleEconomyModule;}

        public boolean isToggleAnalyticsModule() {return TOGGLE_ANALYTICS_MODULE;}
        public void setToggleAnalyticsModule(boolean toggleAnalyticsModule) {this.TOGGLE_ANALYTICS_MODULE = toggleAnalyticsModule;}

        public boolean isToggleDiscordModule() {return TOGGLE_DISCORD_MODULE;}
        public void setToggleDiscordModule(boolean toggleDiscordModule) {this.TOGGLE_DISCORD_MODULE = toggleDiscordModule;}

        public boolean isSAVE_CHAT_LOG() {return SAVE_CHAT_LOG;}
        public void setSAVE_CHAT_LOG(boolean SAVE_CHAT_LOG) {this.SAVE_CHAT_LOG = SAVE_CHAT_LOG;}
    }
}

