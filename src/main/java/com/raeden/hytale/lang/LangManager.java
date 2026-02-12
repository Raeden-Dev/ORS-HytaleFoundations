package com.raeden.hytale.lang;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.data.PlayerProfile;
import com.raeden.hytale.utils.ColorEngine;
import com.raeden.hytale.utils.DefaultColors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

import static com.raeden.hytale.HytaleFoundations.*;
import static com.raeden.hytale.utils.FileManager.getJsonObject;
import static com.raeden.hytale.utils.FileManager.saveJsonFile;
import static com.raeden.hytale.utils.GeneralUtils.findPlayerByName;

public class LangManager {
    private final HytaleFoundations hytaleFoundations;
    private String CONFIG_LANGUAGE;
    private final String DEFAULT_LANGUAGE = "en-us";
    private final Path langDir;
    private final HashMap<String, JsonObject> langCache;

    public LangManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        langDir = hytaleFoundations.getDataDirectory().resolve("lang");
        langCache = new HashMap<>();
        verify();
    }

    private void verify() {
        if(!Files.exists(langDir)) {
            try {
                Files.createDirectories(langDir);
                myLogger.atInfo().log(getMessage(LangKey.CREATE_DIRECTORY_W_LOC, "language", langDir.toString()).getAnsiMessage());
            } catch (IOException e) {
                myLogger.atWarning().log(getMessage(LangKey.CREATE_DIRECTORY_FAIL_W_LOC, "language", langDir.toString()).getAnsiMessage() + e);
            }
        }

        Path defaultLanguage = langDir.resolve(DEFAULT_LANGUAGE + ".json");
        if(!Files.exists(defaultLanguage)) {
            saveDefaultLangFile(defaultLanguage);
        }

        reloadLanguages();
    }

    public void setDefaultLanguage() {
        CONFIG_LANGUAGE = hytaleFoundations.getConfigManager().getDefaultConfig().getLang();
    }

    private void saveDefaultLangFile(Path path) {
        LinkedHashMap<String, String> defaultMap = new LinkedHashMap<>();

        for(LangKey key : LangKey.values()) {
            defaultMap.put(key.getKey(), key.getDefaultMessage());
        }

        saveJsonFile(DEFAULT_LANGUAGE + ".json", path, defaultMap, true);
    }

    public void reloadLanguages() {
        langCache.clear();
        File langFolder = langDir.toFile();
        if(langFolder.listFiles() == null) return;

        for(File file : Objects.requireNonNull(langFolder.listFiles())) {
            if(!file.getName().endsWith(".json")) continue;
            String fileName = file.getName();
            String langKey = fileName.replace(".json", "").toLowerCase();
            JsonObject jsonObject = getJsonObject(fileName, file.toPath(), true);
            langCache.put(langKey, jsonObject);
        }
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

        String prefixText = getLangString(username, LangKey.PLAYER_MSG_PREFIX);
        if (prefixText == null) prefixText = LangKey.PLAYER_MSG_PREFIX.getDefaultMessage();

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
        String jsonKey = key.getKey();
        String setLanguage = CONFIG_LANGUAGE;
        if(setLanguage == null) setLanguage = DEFAULT_LANGUAGE;
        if(username != null) {
            PlayerProfile profile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(username);
            if(profile != null && profile.getLanguage() != null) {
                setLanguage = profile.getLanguage();
            }
        }
        String data = fetchStringFromCache(setLanguage, jsonKey);
        if(data == null) {
            data = fetchStringFromCache(DEFAULT_LANGUAGE, jsonKey);
        }
        if(data == null) {
            data = key.getDefaultMessage();
        }
        return data;
    }

    private String fetchStringFromCache(String language, String key) {
        JsonObject jsonObject = langCache.get(language.toLowerCase());
        if (jsonObject == null || !jsonObject.has(key)) return null;
        try {
            JsonElement element = jsonObject.get(key);
            if(element.isJsonPrimitive()) {
                return element.getAsString();
            }
            return null;
        } catch (Exception e) {
            myLogger.atWarning().log("Error reading lang key '" + key + "' in " + language);
            return null;
        }
    }

}
