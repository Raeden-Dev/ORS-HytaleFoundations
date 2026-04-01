package com.raeden.hytale.modules.utility;

import com.raeden.hytale.HytaleFoundations;

public class UtilityManager {
    private final HytaleFoundations hytaleFoundations;
    private final TeleportManager teleportManager;
    private final WarmupManager warmupManager;
    private final CooldownManager cooldownManager;

    public UtilityManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        this.teleportManager = new TeleportManager(hytaleFoundations);
        this.cooldownManager = new CooldownManager(hytaleFoundations);
        this.warmupManager = new WarmupManager(hytaleFoundations);
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public WarmupManager getWarmupManager() {
        return warmupManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
}
