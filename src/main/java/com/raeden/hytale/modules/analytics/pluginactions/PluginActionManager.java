package com.raeden.hytale.modules.analytics.pluginactions;

import com.raeden.hytale.HytaleEssentials;
import com.raeden.hytale.lang.LangKey;
import com.raeden.hytale.utils.TimeUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

import static com.raeden.hytale.HytaleEssentials.langManager;
import static com.raeden.hytale.HytaleEssentials.myLogger;

public class PluginActionManager {
    private final HytaleEssentials hytaleEssentials;
    private final Path pluginActionPath;

    private String dateToday;
    private File logFile;
    private LinkedHashMap<String, String> actionLogs;

    public PluginActionManager(HytaleEssentials hytaleEssentials) {
        this.hytaleEssentials = hytaleEssentials;
        pluginActionPath = hytaleEssentials.getDataDirectory().resolve("logs").resolve("action_logs");
        actionLogs = new LinkedHashMap<>();
        verify();
    }

    private void verify() {
        if(!Files.exists(pluginActionPath)) {
            try {
                Files.createDirectories(pluginActionPath);
                myLogger.atInfo().log(langManager.getMessage(LangKey.CREATE_DIRECTORY_W_LOC, "action_logs", pluginActionPath.toString()).getAnsiMessage());
            } catch (IOException e) {
                myLogger.atWarning().log(langManager.getMessage(LangKey.CREATE_DIRECTORY_FAIL_W_LOC,"action_logs", pluginActionPath.toString()).getAnsiMessage());
            }
        }

        // Fix date
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
