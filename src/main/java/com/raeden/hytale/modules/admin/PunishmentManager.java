package com.raeden.hytale.modules.admin;

import com.raeden.hytale.HytaleFoundations;

import static com.raeden.hytale.core.config.ConfigManager.PUNISHMENT_LOG_PATH;
import static com.raeden.hytale.core.config.ConfigManager.REPORT_LOG_PATH;
import static com.raeden.hytale.utils.FileUtils.createDirectory;

public class PunishmentManager {
    private final HytaleFoundations hytaleFoundations;

    public PunishmentManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;

        createDirectory(PUNISHMENT_LOG_PATH, true);
    }

    public enum PunishmentType {
        BAN,
        TEMP_BAN,
        PERM_BAN,
        STORE_BAN,
        MUTE,
        SILENT_MUTE;
    }

}
