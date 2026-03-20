package com.raeden.hytale.core.lang;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.player.PlayerProfile;
import com.raeden.hytale.modules.chat.ColorManager;
import com.raeden.hytale.modules.chat.DefaultColors;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.raeden.hytale.HytaleFoundations.*;
import static com.raeden.hytale.utils.FileUtils.*;
import static com.raeden.hytale.utils.PlayerUtils.findPlayerByName;

public class LangManager {
    private final HytaleFoundations hytaleFoundations;
    private String CONFIG_LANGUAGE;
    private final String DEFAULT_LANGUAGE = "en-us";
    private final String FILE_EXTENSION = ".lang";
    private final Path langDir;
    private final Map<String, Map<String, String>> langCache;
    private final List<LangKey> prefixExclusionList = new ArrayList<>(List.of(LangKey.LIST_ITEM, LangKey.PM_FORMAT_SENDER, LangKey.PM_FORMAT_RECEIVER, LangKey.LIST_NUMBERED_ITEM,
            LangKey.PLAYER_INFO_STATS, LangKey.PLAYER_INFO_PROFILE));

    public LangManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        langDir = hytaleFoundations.getDataDirectory().resolve("lang");
        langCache = new ConcurrentHashMap<>();
        verifyLangManager();
    }

    private void verifyLangManager() {
        createDirectory(langDir, true);
        Path defaultLanguage = langDir.resolve(DEFAULT_LANGUAGE + FILE_EXTENSION);
        if(!Files.exists(defaultLanguage)) {
            saveDefaultLangFile(defaultLanguage);
        }
        reloadLanguages();
    }
    public void setDefaultLanguage() {CONFIG_LANGUAGE = hytaleFoundations.getConfigManager().getDefaultConfig().getLang();}

    private void saveDefaultLangFile(Path path) {
        String currentSection = null;
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("# HytaleFoundations Language File - English (US)");
            writer.newLine();
            writer.write("# This file contains all texts/messages for the plugin");
            writer.newLine();
            writer.newLine();

            for (LangKey key : LangKey.values()) {
                String configKey = key.getKey();
                String defaultVal = key.getDefaultMessage().replace("\n", "\\n");

                String group;
                int dotIndex = configKey.indexOf('.');

                if (dotIndex != -1) {
                    group = configKey.substring(0, dotIndex);
                } else {
                    group = "general";
                }
                if (!group.equalsIgnoreCase(currentSection)) {
                    currentSection = group;
                    String title = group.substring(0, 1).toUpperCase() + group.substring(1);

                    writer.newLine();
                    writer.write("# ===== " + title + " Messages =====");
                    writer.newLine();
                }

                writer.write(configKey + " = " + defaultVal);
                writer.newLine();
            }
        } catch (IOException e) {
            logError(path, "saveDefaultLangFile", e);
        }
    }
    public void reloadLanguages() {
        langCache.clear();
        File langFolder = langDir.toFile();
        if (langFolder.listFiles() == null) return;

        for (File file : Objects.requireNonNull(langFolder.listFiles())) {
            if (!file.getName().endsWith(FILE_EXTENSION)) continue;

            String fileName = file.getName();
            String langCode = fileName.replace(FILE_EXTENSION, "").toLowerCase();

            Map<String, String> langMap = loadLangFile(file.toPath());
            langCache.put(langCode, langMap);
        }

        myLogger.atInfo().log(getConsoleMessage(LangKey.LOAD_SUCCESS, langCache.size() + " languages").getAnsiMessage());
    }
    private Map<String, String> loadLangFile(Path path) {
        Map<String, String> map = new ConcurrentHashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int splitIndex = line.indexOf('=');
                if (splitIndex != -1) {
                    String key = line.substring(0, splitIndex).trim();
                    String value = line.substring(splitIndex + 1).trim();
                    value = value.replace("\\n", "\n");
                    map.put(key, value);
                }
            }
        } catch (IOException e) {
            logError(path, "loadLangFile", e);
        }
        return map;
    }

    public Message getMessage(LangKey key, String... args) {return getMessage(null, key, false, args);}
    public Message getPlayerMessage(String username, LangKey key, String... args) {
        return getMessage(username, key, false, args);
    }
    public Message getConsoleMessage(LangKey key, String... args) {
        return getMessage(null, key, true, args);
    }
    public Message getAbstractMessage(CommandContext ctx, LangKey key, String... args) {
        if(ctx.isPlayer()) {
            return getPlayerMessage(ctx.sender().getDisplayName(), key, args);
        } else {
            return getConsoleMessage(key, args);
        }
    }
    public Message getMessage(String username, LangKey key, boolean isConsole, String... args) {
        PlayerRef playerRef = username == null ? null : findPlayerByName(username);
        boolean isAdmin = hytaleFoundations.getPermissionManager().isPlayerAdmin(playerRef);
        String prefixText = cleanupPrefix(username, key, isAdmin);
        String finalText = getLangString(username, key);
        if (finalText == null) finalText = key.getDefaultMessage();
        if(username != null) finalText = prefixText + finalText;
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                String val = args[i] != null ? args[i] : "null";
                finalText = finalText.replace("{" + i + "}", val);
            }
        }
        return formatMessage(playerRef, finalText, isConsole);
    }

    private String cleanupPrefix(String username, LangKey key, boolean isAdmin) {
        String prefixText = ( isAdmin ? getLangString(username, LangKey.CHAT_MSG_PREFIX_ADMIN) : getLangString(username, LangKey.CHAT_MSG_PREFIX));
        if (prefixText == null)  prefixText = (isAdmin ? LangKey.CHAT_MSG_PREFIX_ADMIN.getDefaultMessage() + " " : LangKey.CHAT_MSG_PREFIX.getDefaultMessage() + " ");
        else prefixText = prefixText + " ";
        if(prefixExclusionList.contains(key) && isAdmin) prefixText = "";
        return prefixText;
    }

    private Message formatMessage(PlayerRef playerRef, String text, boolean isConsole) {
        try {
            ColorManager engine = hytaleFoundations.getChatManager().getColorEngine();
            return engine.parseText(playerRef, text, isConsole);
        } catch (NullPointerException e) {
            String cleanText = text.replaceAll("(?i)&[0-9a-z]", "");
            return Message.raw(cleanText).color(DefaultColors.WHITE.getHex());
        }
    }

    private String getLangString(String username, LangKey key) {
        String mapKey = key.getKey();
        String setLanguage = CONFIG_LANGUAGE;
        if(setLanguage == null) setLanguage = DEFAULT_LANGUAGE;
        if(username != null) {
            try {
                PlayerProfile profile = hytaleFoundations.getPlayerDataManager().getOnlinePlayerProfile(username);
                if (profile != null && profile.getLanguage() != null) {
                    setLanguage = profile.getLanguage();
                }
            } catch (Exception e) {
                logError("getLangString", e);
            }
        }
        String data = fetchStringFromCache(setLanguage, mapKey);
        if(data == null) {
            data = fetchStringFromCache(DEFAULT_LANGUAGE, mapKey);
        }
        if(data == null) {
            data = key.getDefaultMessage();
        }
        return data;
    }

    private String fetchStringFromCache(String language, String key) {
        Map<String, String> langMap = langCache.get(language.toLowerCase());
        if (langMap == null) return null;

        return langMap.get(key);
    }

}
