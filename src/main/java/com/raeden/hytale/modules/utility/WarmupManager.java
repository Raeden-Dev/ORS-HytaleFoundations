package com.raeden.hytale.modules.utility;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.lang.LangKey;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

import static com.raeden.hytale.HytaleFoundations.LM;
import static com.raeden.hytale.HytaleFoundations.myLogger;

public class WarmupManager {
    private final HytaleFoundations hytaleFoundations;
    public static final Map<String, Double> DEFAULT_WARMUPS = Map.of(
            "teleport_home", 3.0,
            "teleport_location", 3.0,
            "teleport_player", 5.0,
            "teleport_wild", 3.0,
            "report_issue", 120.0,
            "friend_readd", 300.0,
            "unblock", 300.0,
            "mail_send", 60.0
    );
    private final double MAX_MOVEMENT = 1.0F;
    private final long POLL_INTERVAL_MS = 100L;

    private final ScheduledExecutorService poller;
    private final Map<UUID, PendingWarmup> pendingWarmups;
    private ScheduledFuture<?> pollTask;

    public WarmupManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        pendingWarmups = new ConcurrentHashMap<>();

        poller = Executors.newSingleThreadScheduledExecutor(
                runnable -> {
                    Thread thread = new Thread(runnable, "HytaleFoundations-WarmupThread");
                    thread.setDaemon(true);
                    return thread;
                }
        );
    }

    public void startWarmup(PlayerRef player, Vector3d startPosition, int warmupSeconds, Runnable onComplete, String actionName, World world, Store<EntityStore> store, Ref<EntityStore> ref, boolean silent) {
        UUID playerId = player.getUuid();
        cancelWarmup(playerId);
        if(warmupSeconds <= 0) {
            if(world != null) world.execute(onComplete);
            else onComplete.run();
            return;
        }
        if(startPosition == null || world == null || store == null || ref == null) {
            myLogger.atInfo().log(LM.getConsoleMessage(LangKey.WARMUP_MISSING_CONTEXT, actionName).getAnsiMessage());
            if (world != null) world.execute(onComplete);
            else onComplete.run();
            return;
        }
        long endTime = System.nanoTime() + TimeUnit.SECONDS.toNanos(warmupSeconds);
        PendingWarmup warmup = new PendingWarmup(
                actionName, playerId, ref, new Vector3d(startPosition), endTime,
                world, store, onComplete, silent);
        pendingWarmups.put(playerId, warmup);
        ensurePollerRunning();
    }

    private void ensurePollerRunning() {
        if (pollTask == null || pollTask.isCancelled()) {
            pollTask = poller.scheduleAtFixedRate(this::pollWarmups, POLL_INTERVAL_MS, POLL_INTERVAL_MS, TimeUnit.MILLISECONDS);
        }
    }

    private void pollWarmups() {
        if (pendingWarmups.isEmpty()) {
            if (pollTask != null) {
                pollTask.cancel(false);
                pollTask = null;
            }
            return;
        }
        for (PendingWarmup warmup : pendingWarmups.values()) {
            if (warmup.cancelled) {
                pendingWarmups.remove(warmup.playerUuid);
                continue;
            }
            World world = warmup.world;
            if (world == null) {
                pendingWarmups.remove(warmup.playerUuid);
            } else {
                world.execute(() -> tickWarmup(warmup));
            }
        }
    }

    private void tickWarmup(PendingWarmup warmup) {
        Store<EntityStore> store = warmup.store;
        Ref<EntityStore> ref = warmup.playerRef;
        if (ref == null || !ref.isValid()) {
            pendingWarmups.remove(warmup.playerUuid);
            return;
        }
        Player playerComponent;
        try {
            playerComponent = store.getComponent(ref, Player.getComponentType());
        } catch (Exception e) {
            pendingWarmups.remove(warmup.playerUuid);
            return;
        }
        if (playerComponent == null) {
            pendingWarmups.remove(warmup.playerUuid);
            return;
        }
        Vector3d currentPos = getPlayerPosition(ref, store);
        if (currentPos == null) {
            pendingWarmups.remove(warmup.playerUuid);
            return;
        }

        if (hasMoved(warmup.getStartPosition(), currentPos)) {
            pendingWarmups.remove(warmup.playerUuid);
            playerComponent.sendMessage(LM.getPlayerMessage(playerComponent.getDisplayName(), LangKey.WARMUP_CANCEL_ANNOUNCE));
            return;
        }

        long now = System.nanoTime();
        long remainingNanos = warmup.getEndTime() - now;

        if (remainingNanos <= 0L) {
            pendingWarmups.remove(warmup.playerUuid);
            if (ref.isValid()) {
                try {
                    warmup.onComplete.run();
                } catch (Exception e) {
                    myLogger.atWarning().log("[WarmupManager] Error executing post-warmup action: " + e.getMessage());
                }
            }
        } else {
            int remainingSeconds = (int) Math.ceil((double) remainingNanos / 1.0E9);
            if (!warmup.silent && remainingSeconds != warmup.getRemainingSeconds() && remainingSeconds > 0) {
                warmup.setRemainingSeconds(remainingSeconds);
                playerComponent.sendMessage(LM.getPlayerMessage(playerComponent.getDisplayName(), LangKey.WARMUP_ANNOUNCE, String.valueOf(remainingSeconds)));
            }
        }
    }

    private boolean hasMoved(Vector3d start, Vector3d current) {
        double dx = current.getX() - start.getX();
        double dy = current.getY() - start.getY();
        double dz = current.getZ() - start.getZ();
        double distanceSquared = (dx * dx) + (dy * dy) + (dz * dz);
        return distanceSquared > MAX_MOVEMENT;
    }

    private Vector3d getPlayerPosition(Ref<EntityStore> ref, Store<EntityStore> store) {
        try {
            if (ref != null && ref.isValid()) {
                TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
                return transform != null ? transform.getPosition() : null;
            }
        } catch (Exception ignored) {}
        return null;
    }

    public void cancelWarmup(UUID playerId) {
        PendingWarmup warmup = pendingWarmups.remove(playerId);
        if (warmup != null) {
            warmup.cancelled = true;
        }
    }

    public boolean hasActiveWarmup(UUID playerId) {
        return pendingWarmups.containsKey(playerId);
    }

    public void shutdown() {
        pendingWarmups.clear();
        if (pollTask != null) pollTask.cancel(false);
        poller.shutdown();
        try {
            if (!poller.awaitTermination(5L, TimeUnit.SECONDS)) {
                poller.shutdownNow();
            }
        } catch (InterruptedException e) {
            poller.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // classes
    private static class PendingWarmup {
        private final String actionName;
        private final UUID playerUuid;
        private final Ref<EntityStore> playerRef;
        private final Vector3d startPosition;
        private final long endTime;
        private final World world;
        private final Store<EntityStore> store;
        private final Runnable onComplete;
        private final boolean silent;

        private volatile boolean cancelled = false;
        private int remainingSeconds;

        public PendingWarmup(String actionName, UUID playerUuid, Ref<EntityStore> playerRef, Vector3d startPosition,
                             long endTime, World world, Store<EntityStore> store, Runnable onComplete, boolean silent) {
            this.actionName = actionName;
            this.playerUuid = playerUuid;
            this.playerRef = playerRef;
            this.startPosition = startPosition;
            this.endTime = endTime;
            this.world = world;
            this.store = store;
            this.onComplete = onComplete;
            this.silent = silent;
        }

        public String getActionName() {return actionName;}
        public UUID getPlayerUuid() {return playerUuid;}
        public Ref<EntityStore> getPlayerRef() {return playerRef;}
        public Vector3d getStartPosition() {return startPosition;}
        public long getEndTime() {return endTime;}
        public World getWorld() {return world;}
        public Store<EntityStore> getStore() {return store;}
        public Runnable getOnComplete() {return onComplete;}
        public boolean isSilent() {return silent;}

        public boolean isCancelled() { return cancelled;}
        public int getRemainingSeconds() {return remainingSeconds;}
        public void setRemainingSeconds(int remainingSeconds) {this.remainingSeconds = remainingSeconds;}
    }
}
