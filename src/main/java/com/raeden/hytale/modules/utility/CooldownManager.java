package com.raeden.hytale.modules.utility;

import com.raeden.hytale.HytaleFoundations;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {
    private final HytaleFoundations hytaleFoundations;
    private final Map<String, Map<UUID, Long>> activeCooldowns;

    public CooldownManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        this.activeCooldowns = new ConcurrentHashMap<>();
    }

    public long getRemainingCooldown(String action, UUID playerUuid) {
        if(activeCooldowns.isEmpty()) return 0L;
        Map<UUID, Long> activePlayerCooldowns = activeCooldowns.get(action);
        if(activePlayerCooldowns == null || activePlayerCooldowns.isEmpty()) return 0L;
        if(activePlayerCooldowns.containsKey(playerUuid)) {
            Long cooldownTime = activePlayerCooldowns.get(playerUuid);
            if(cooldownTime == null) {
                return 0L;
            } else {
                long remainingTime = cooldownTime - System.currentTimeMillis();
                if(remainingTime <= 0L) {
                    activePlayerCooldowns.remove(playerUuid);
                    return 0L;
                } else {
                    return (int)Math.ceil((double)remainingTime / (double)1000.0F);
                }
            }
        }
        return 0L;
    }

    public boolean hasCooldown(String action, UUID playerUuid) { return this.getRemainingCooldown(action, playerUuid) == 0;}

    public void addCooldown(String action, UUID playerUuid, int seconds) {
        if(seconds > 0) {
            this.activeCooldowns.computeIfAbsent(action, (k) -> new ConcurrentHashMap<>())
                    .put(playerUuid, System.currentTimeMillis() + (long)seconds * 1000L);
        }
    }

    public void clearCooldown(String action, UUID playerUuid) {
        Map<UUID, Long> actionCooldowns = this.activeCooldowns.get(action);
        if (actionCooldowns != null) {
            actionCooldowns.remove(playerUuid);
        }
    }

    public void clearAllActiveCooldowns(UUID playerUuid) {
        for(Map<UUID, Long> actionCooldowns : this.activeCooldowns.values()) {
            actionCooldowns.remove(playerUuid);
        }
    }


    public Map<String, Map<UUID, Long>> getActiveCooldowns() {
        return activeCooldowns;
    }
}
