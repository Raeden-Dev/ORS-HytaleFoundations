package com.raeden.hytale.core.events.playerEvents;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.player.PlayerStats;
import com.raeden.hytale.utils.Scheduler;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class PlayerMovementListener {
    private final HytaleFoundations hytaleFoundations;
    private final Scheduler scheduler;

    private final Map<UUID, Vector3d> lastPositions;

    public PlayerMovementListener(HytaleFoundations hytaleFoundations, Scheduler scheduler) {
        this.hytaleFoundations = hytaleFoundations;
        this.scheduler = scheduler;
        lastPositions = new ConcurrentHashMap<>();
        createMovementScheduler();
    }

    private void createMovementScheduler() {
        final double minimumDistance = 1.0;
        final double maximumDistance = 2500.0;
        scheduler.runTaskTimer("MovementTracker", () -> {
            Universe universe = Universe.get();
            if(universe == null) return;
            for(PlayerRef player : universe.getPlayers()) {
                UUID id = player.getUuid();
                PlayerStats stats = hytaleFoundations.getPlayerDataManager().getOnlinePlayerStats(player.getUsername());
                if(stats == null) continue;
                Vector3d currentPos = player.getTransform().getPosition();
                Vector3d lastPos = lastPositions.get(id);
                if(lastPos != null) {
                    double dx = currentPos.x - lastPos.x;
                    double dy = currentPos.y - lastPos.y;
                    double dz = currentPos.z - lastPos.z;
                    double distSq = (dx * dx) + (dy * dy) + (dz * dz);
                    if(distSq > minimumDistance && distSq < maximumDistance) {
                        double distanceMoved = Math.sqrt(distSq);
                        if(stats.isCollectStats()) stats.addDistanceWalked(distanceMoved);
                    }
                    lastPos.x = currentPos.x;
                    lastPos.y = currentPos.y;
                    lastPos.z = currentPos.z;
                } else {
                    lastPositions.put(id, new Vector3d(currentPos.x, currentPos.y, currentPos.z));
                }
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    public void removePlayerFromCache(UUID id) {
        lastPositions.remove(id);
    }
}
