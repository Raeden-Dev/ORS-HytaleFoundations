package com.raeden.hytale.modules.utility;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.lang.LangKey;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.raeden.hytale.HytaleFoundations.LM;
import static com.raeden.hytale.HytaleFoundations.myLogger;
import static com.raeden.hytale.utils.FileUtils.logError;

public class TeleportManager {
    public static final long TPA_REQUEST_LIFETIME_MS = 60_000L;
    public static final int RTP_DEFAULT_RADIUS = 2000;
    public static final int RTP_MAX_ATTEMPTS = 8;
    public static final double RTP_DEFAULT_HEIGHT = 256.0D;

    private final HytaleFoundations hytaleFoundations;
    private final Map<UUID, PendingTpaRequest> pendingByReceiver;
    private final Random rtpRandom;

    public TeleportManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        this.pendingByReceiver = new ConcurrentHashMap<>();
        this.rtpRandom = new Random();
    }

    // Core Teleport
    public boolean teleport(PlayerRef target, Vector3d position, Vector3f rotation) {
        if (target == null || position == null) return false;
        Ref<EntityStore> ref = target.getReference();
        if (ref == null || !ref.isValid()) return false;
        World world = getWorld(target);
        if (world == null) return false;
        Store<EntityStore> store = ref.getStore();
        Vector3f finalRotation = rotation != null ? rotation : new Vector3f(target.getHeadRotation());
        Teleport teleport = Teleport.createForPlayer(world, position, finalRotation);
        world.execute(() -> {
            try {
                if (!ref.isValid()) return;
                store.addComponent(ref, Teleport.getComponentType(), teleport);
            } catch (Exception e) {
                logError("TeleportManager.teleport", e);
            }
        });
        return true;
    }

    public boolean teleportToPlayer(PlayerRef source, PlayerRef destination) {
        if (source == null || destination == null) return false;
        Vector3d position = getPosition(destination);
        Vector3f rotation = new Vector3f(destination.getHeadRotation());
        if (position == null) return false;
        return teleport(source, position, rotation);
    }

    // Random Teleport
    public Vector3d randomTeleport(PlayerRef target) {
        return randomTeleport(target, RTP_DEFAULT_RADIUS);
    }

    public Vector3d randomTeleport(PlayerRef target, int radius) {
        if (target == null || radius <= 0) return null;
        Vector3d origin = getPosition(target);
        if (origin == null) return null;
        for (int attempt = 0; attempt < RTP_MAX_ATTEMPTS; attempt++) {
            double dx = (rtpRandom.nextDouble() * 2.0D - 1.0D) * radius;
            double dz = (rtpRandom.nextDouble() * 2.0D - 1.0D) * radius;
            Vector3d candidate = new Vector3d(origin.getX() + dx, RTP_DEFAULT_HEIGHT, origin.getZ() + dz);
            if (teleport(target, candidate, null)) return candidate;
        }
        return null;
    }

    // TPA Request System
    public boolean requestTpa(PlayerRef requester, PlayerRef destination, boolean summon) {
        if (requester == null || destination == null) return false;
        if (requester.getUuid().equals(destination.getUuid())) {
            requester.sendMessage(LM.getPlayerMessage(requester.getUsername(), LangKey.TP_SELF));
            return false;
        }
        PendingTpaRequest existing = pendingByReceiver.get(destination.getUuid());
        if (existing != null && !existing.isExpired() && existing.requesterUuid.equals(requester.getUuid())) {
            requester.sendMessage(LM.getPlayerMessage(requester.getUsername(),
                    LangKey.TPA_REQUEST_PENDING, destination.getUsername()));
            return false;
        }
        PendingTpaRequest request = new PendingTpaRequest(
                requester.getUuid(), destination.getUuid(), summon,
                System.currentTimeMillis() + TPA_REQUEST_LIFETIME_MS);
        pendingByReceiver.put(destination.getUuid(), request);

        long seconds = TPA_REQUEST_LIFETIME_MS / 1000L;
        if (summon) {
            requester.sendMessage(LM.getPlayerMessage(requester.getUsername(),
                    LangKey.TPAHERE_REQUEST_SENT, destination.getUsername(), String.valueOf(seconds)));
            destination.sendMessage(LM.getPlayerMessage(destination.getUsername(),
                    LangKey.TPAHERE_REQUEST_RECEIVED, requester.getUsername()));
        } else {
            requester.sendMessage(LM.getPlayerMessage(requester.getUsername(),
                    LangKey.TPA_REQUEST_SENT, destination.getUsername(), String.valueOf(seconds)));
            destination.sendMessage(LM.getPlayerMessage(destination.getUsername(),
                    LangKey.TPA_REQUEST_RECEIVED, requester.getUsername()));
        }
        return true;
    }

    public boolean acceptTpa(PlayerRef receiver) {
        if (receiver == null) return false;
        PendingTpaRequest request = pendingByReceiver.remove(receiver.getUuid());
        if (request == null || request.isExpired()) {
            receiver.sendMessage(LM.getPlayerMessage(receiver.getUsername(), LangKey.TPA_NO_PENDING));
            return false;
        }
        PlayerRef requester = lookupPlayer(request.requesterUuid);
        if (requester == null) {
            receiver.sendMessage(LM.getPlayerMessage(receiver.getUsername(), LangKey.TPA_NO_PENDING));
            return false;
        }
        boolean ok;
        if (request.summon) {
            ok = teleportToPlayer(receiver, requester);
        } else {
            ok = teleportToPlayer(requester, receiver);
        }
        if (ok) {
            receiver.sendMessage(LM.getPlayerMessage(receiver.getUsername(),
                    LangKey.TPA_ACCEPT, requester.getUsername()));
            requester.sendMessage(LM.getPlayerMessage(requester.getUsername(),
                    LangKey.TPA_ACCEPTED, receiver.getUsername()));
        }
        return ok;
    }

    public boolean denyTpa(PlayerRef receiver) {
        if (receiver == null) return false;
        PendingTpaRequest request = pendingByReceiver.remove(receiver.getUuid());
        if (request == null) {
            receiver.sendMessage(LM.getPlayerMessage(receiver.getUsername(), LangKey.TPA_NO_PENDING));
            return false;
        }
        PlayerRef requester = lookupPlayer(request.requesterUuid);
        receiver.sendMessage(LM.getPlayerMessage(receiver.getUsername(),
                LangKey.TPA_DENY, requester != null ? requester.getUsername() : "Unknown"));
        if (requester != null) {
            requester.sendMessage(LM.getPlayerMessage(requester.getUsername(),
                    LangKey.TPA_DENIED, receiver.getUsername()));
        }
        return true;
    }

    public void purgeExpiredRequests() {
        long now = System.currentTimeMillis();
        pendingByReceiver.entrySet().removeIf(entry -> entry.getValue().expiryMs <= now);
    }

    // Helpers
    public Vector3d getPosition(PlayerRef playerRef) {
        if (playerRef == null) return null;
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) return null;
        try {
            Store<EntityStore> store = ref.getStore();
            TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
            if (transform != null) return new Vector3d(transform.getPosition());
        } catch (Exception e) {
            logError("TeleportManager.getPosition", e);
        }
        if (playerRef.getTransform() != null) return new Vector3d(playerRef.getTransform().getPosition());
        return null;
    }

    public World getWorld(PlayerRef playerRef) {
        if (playerRef == null) return null;
        try {
            Universe universe = Universe.get();
            if (universe == null) return null;
            UUID worldUuid = playerRef.getWorldUuid();
            if (worldUuid == null) return null;
            return universe.getWorld(worldUuid);
        } catch (Exception e) {
            logError("TeleportManager.getWorld", e);
            return null;
        }
    }

    private PlayerRef lookupPlayer(UUID uuid) {
        try {
            Universe universe = Universe.get();
            if (universe == null) return null;
            return universe.getPlayer(uuid);
        } catch (Exception e) {
            logError("TeleportManager.lookupPlayer", e);
            return null;
        }
    }

    public Map<UUID, PendingTpaRequest> getPendingRequests() {
        return pendingByReceiver;
    }

    // Classes
    public static class PendingTpaRequest {
        public final UUID requesterUuid;
        public final UUID destinationUuid;
        public final boolean summon;
        public final long expiryMs;

        public PendingTpaRequest(UUID requesterUuid, UUID destinationUuid, boolean summon, long expiryMs) {
            this.requesterUuid = requesterUuid;
            this.destinationUuid = destinationUuid;
            this.summon = summon;
            this.expiryMs = expiryMs;
        }
        public boolean isExpired() {
            return System.currentTimeMillis() >= expiryMs;
        }
    }
}
