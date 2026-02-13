package com.raeden.hytale.modules.analytics.pluginactions;

import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.lang.LangKey;
import com.raeden.hytale.utils.TimeUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

import static com.raeden.hytale.HytaleFoundations.langManager;
import static com.raeden.hytale.HytaleFoundations.myLogger;
import static com.raeden.hytale.utils.FileManager.createDirectory;

public class PluginActionManager {
    private final HytaleFoundations hytaleFoundations;
    private final Path pluginActionPath;

    private String dateToday;
    private File logFile;
    private LinkedHashMap<String, String> actionLogs;

    public PluginActionManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        pluginActionPath = hytaleFoundations.getDataDirectory().resolve("logs").resolve("action_logs");
        actionLogs = new LinkedHashMap<>();
        verify();
    }

    private void verify() {
        createDirectory(pluginActionPath, true);
        fixDate();
    }

    private void fixDate() {
        if(dateToday == null) {
            dateToday = TimeUtils.getCurrentDate();
        }
        else if(!dateToday.equals(TimeUtils.getCurrentDate())) {
            dateToday = TimeUtils.getCurrentDate();
            createLogFile();
        }
    }

    private void createLogFile() {

    }


}
