package com.raeden.hytale.lang;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hypixel.hytale.server.core.Message;
import com.raeden.hytale.HytaleEssentials;
import com.raeden.hytale.core.data.PlayerData;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;

import static com.raeden.hytale.HytaleEssentials.*;
import static com.raeden.hytale.utils.ColorUtils.color;
import static com.raeden.hytale.utils.ColorUtils.gradient;

public class LangManager {
    private final HytaleEssentials hytaleEssentials;
    private String CONFIG_LANGUAGE;
    private final String DEFAULT_LANGUAGE = "en-us";
    private final Path langDir;
    private final HashMap<String, JsonObject> langCache;

    public LangManager(HytaleEssentials hytaleEssentials) {
        this.hytaleEssentials = hytaleEssentials;
        langDir = hytaleEssentials.getDataDirectory().resolve("lang");
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
        CONFIG_LANGUAGE = hytaleEssentials.getConfigManager().getDefaultConfig().getLang();
    }

    private void saveDefaultLangFile(Path path) {
        LinkedHashMap<String, LangEntry> defaultMap = new LinkedHashMap<>();

        for(LangKey key : LangKey.values()) {
            String text = key.getDefaultMessage();
            String start = key.getStartColor().getHex();
            String end = key.isGradient() ? key.getEndColor().getHex() : null;
            boolean bold = key.isBold();
            LangEntry entry = new LangEntry(text, start, end, bold);
            defaultMap.put(key.getKey(), entry);
        }

        String jsonString = GSON.toJson(defaultMap);
        try {
            Files.writeString(path, jsonString, StandardCharsets.UTF_8);
            myLogger.atInfo().log(getMessage(LangKey.SAVE_W_LOC, "en-us.json", langDir.toString()).getAnsiMessage());
        } catch (IOException e) {
            myLogger.atSevere().log(getMessage(LangKey.SAVE_FAILURE_W_LOC, "en-us.json", langDir.toString()).getAnsiMessage() + e);
        }
    }

    public void reloadLanguages() {
        langCache.clear();
        File langFolder = langDir.toFile();
        if(langFolder.listFiles() == null) return;

        for(File file : Objects.requireNonNull(langFolder.listFiles())) {
            if(!file.getName().endsWith(".json")) continue;

            String fileName = file.getName();
            String langKey = fileName.replace(".json", "").toLowerCase();

            try {
                String jsonString = Files.readString(file.toPath());
                JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
                langCache.put(langKey, jsonObject);
                myLogger.atInfo().log(getMessage(LangKey.LOAD_FILE, "language: " + fileName).getAnsiMessage());
            } catch (IOException e) {
                myLogger.atWarning().log(getMessage(LangKey.LOAD_FAILURE, "language: " + fileName).getAnsiMessage());
            }
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
        LangEntry entry = getLangEntry(username, key);
        String finalText = entry.text != null ? entry.text : key.getDefaultMessage();

        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                String val = args[i] != null ? args[i] : "null";
                finalText = finalText.replace("{" + i + "}", val);
            }
        }
        return colorize(finalText, entry);
    }

    private Message colorize(String text, LangEntry entry) {
        if (entry.endColor != null) {
            return gradient(text, "#" + entry.startColor, "#" + entry.endColor, entry.bold);
        }
        return color(text, "#" + entry.startColor, entry.bold);
    }

    private LangEntry getLangEntry(String username, LangKey key) {
        String jsonKey = key.getKey();
        String setLanguage = CONFIG_LANGUAGE;

        if(setLanguage == null) setLanguage = DEFAULT_LANGUAGE;

        if(username != null) {
            PlayerData meta = hytaleEssentials.getPlayerDataManager().getPlayerData(username);
            if (meta != null && meta.getLanguage() != null) {
                setLanguage = meta.getLanguage();
            }
        }

        LangEntry data = fetchEntryFromCache(setLanguage, jsonKey);

        if (data == null) {
            data = fetchEntryFromCache(DEFAULT_LANGUAGE, jsonKey);
        }

        if (data == null) {
            String start = key.getStartColor().getHex();
            String end = key.isGradient() ? key.getEndColor().getHex() : null;
            data = new LangEntry(key.getDefaultMessage(), start, end, key.isBold());
        }

        return data;
    }

    private LangEntry fetchEntryFromCache(String language, String key) {
        JsonObject jsonObject = langCache.get(language.toLowerCase());
        if (jsonObject == null || !jsonObject.has(key)) return null;
        try {
            return GSON.fromJson(jsonObject.get(key), LangEntry.class);
        } catch (Exception e) {
            myLogger.atWarning().log(getMessage(LangKey.READ_FAILURE, "key '" + key + "' in " + language).getAnsiMessage());
            return null;
        }
    }

    private static class LangEntry {
        String text;
        String startColor;
        String endColor;
        boolean bold;

        public LangEntry() {}

        public LangEntry(String text, String startColor, String endColor, boolean bold) {
            this.text = text;
            this.startColor = startColor;
            this.endColor = endColor;
            this.bold = bold;
        }
    }
}
