package com.raeden.hytale.modules.analytics.pluginactions;

import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.utils.TimeUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.raeden.hytale.core.config.ConfigManager.ACTION_LOG_PATH;
import static com.raeden.hytale.utils.FileUtils.createDirectory;

public class PluginActionManager {
    private final HytaleFoundations hytaleFoundations;

    private String dateToday;
    private File logFile;
    private Map<String, String> actionLogs;

    public PluginActionManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        actionLogs = new ConcurrentHashMap<>();
        initializeActionManager();
    }

    private void initializeActionManager() {
        createDirectory(ACTION_LOG_PATH, true);
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
