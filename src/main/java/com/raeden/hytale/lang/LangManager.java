package com.raeden.hytale.lang;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.data.PlayerProfile;
import com.raeden.hytale.utils.ColorEngine;
import com.raeden.hytale.utils.DefaultColors;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.raeden.hytale.HytaleFoundations.*;
import static com.raeden.hytale.utils.FileManager.*;
import static com.raeden.hytale.utils.GeneralUtils.findPlayerByName;

public class LangManager {
    private final HytaleFoundations hytaleFoundations;
    private String CONFIG_LANGUAGE;
    private final String DEFAULT_LANGUAGE = "en-us";
    private final String FILE_EXTENSION = ".lang";
    private final Path langDir;
    private final HashMap<String, Map<String, String>> langCache;

    public LangManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        langDir = hytaleFoundations.getDataDirectory().resolve("lang");
        langCache = new HashMap<>();
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
        boolean isFirst = true;
        String parentKey = "";
        String header = "# ===== {TITLE} Messages =====";

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("# HytaleFoundations Language File - English (US)");
            writer.newLine();
            writer.write("# This file contains all texts/messages for the plugin");
            writer.newLine();
            writer.newLine();

            for (LangKey key : LangKey.values()) {
                String configKey = key.getKey();
                String defaultVal = key.getDefaultMessage().replace("\n", "\\n");

                if(isFirst) {
                    String[] parts = configKey.split("\\.");
                    String curParentKey = parts[0];
                    if(!parentKey.equals(curParentKey)) {
                        parentKey = curParentKey;
                        writer.newLine();
                        writer.write(header.replace("{TITLE}",parentKey));
                        writer.newLine();
                        writer.newLine();
                    }
                    isFirst = false;
                }

                String check = configKey.substring(0,3);
                String parentKeySub = parentKey.substring(0,3);
                if(!parentKeySub.equals(check)) {
                    String[] parts = configKey.split("\\.");
                    String curParentKey = parts[0];
                    if(!parentKey.equals(curParentKey)) {
                        parentKey = curParentKey;
                        writer.newLine();
                        writer.write(header.replace("{TITLE}",parentKey));
                        writer.newLine();
                        writer.newLine();
                    }
                }

                writer.write(configKey + " = " + defaultVal);
                writer.newLine();
            }

            myLogger.atInfo().log(getMessage(LangKey.CREATE_SUCCESS, path.getFileName().toString()).getAnsiMessage());

        } catch (IOException e) {
            logExceptionError(path, "saveDefaultLangFile", e);
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

        myLogger.atInfo().log(getMessage(LangKey.LOAD_SUCCESS, String.valueOf(langCache.size()), "languages").getAnsiMessage());
    }
    private Map<String, String> loadLangFile(Path path) {
        Map<String, String> map = new HashMap<>();
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
            logExceptionError(path, "loadLangFile", e);
        }
        return map;
    }

    public Message getMessage(LangKey key) {
        return getMessage(null, key, (String[]) null);
    }
    public Message getMessage(LangKey key, String... args) {
        return getMessage(null, key, args);
    }
    public Message getMessage(String username, LangKey key) {
        return getMessage(username, key, (String[]) null);
    }

    public Message getMessage(String username, LangKey key, String... args) {
        boolean isConsole = username == null;
        PlayerRef playerRef = username == null ? null : findPlayerByName(username);

        String prefixText = getLangString(username, LangKey.PREFIX);
        if (prefixText == null) prefixText = LangKey.PREFIX.getDefaultMessage();

        String finalText = getLangString(username, key);
        if (finalText == null) finalText = key.getDefaultMessage();

        if(username != null) {
            finalText = prefixText + finalText;
        }

        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                String val = args[i] != null ? args[i] : "null";
                finalText = finalText.replace("{" + i + "}", val);
            }
        }
        return formatMessage(playerRef, finalText, isConsole);
    }

    private Message formatMessage(PlayerRef playerRef, String text, boolean isConsole) {
        try {
            ColorEngine engine = hytaleFoundations.getChatManager().getColorEngine();
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
                PlayerProfile profile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(username);
                if (profile != null && profile.getLanguage() != null) {
                    setLanguage = profile.getLanguage();
                }
            } catch (Exception e) {
                logExceptionError("getLangString", e);
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
