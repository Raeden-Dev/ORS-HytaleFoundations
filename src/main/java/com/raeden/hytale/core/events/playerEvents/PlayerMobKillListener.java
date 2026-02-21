package com.raeden.hytale.core.events.playerEvents;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.player.PlayerStats;
import com.raeden.hytale.utils.FileManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.raeden.hytale.utils.FileManager.logError;

public class PlayerMobKillListener extends DeathSystems.OnDeathSystem {
    private final HytaleFoundations hytaleFoundations;
    public PlayerMobKillListener(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
    }
    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Query.any();
    }
    @Override
    public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent deathComponent, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        try {
            Universe universe = Universe.get();
            if(universe == null) return;
            ComponentType<EntityStore, PlayerRef> refType = universe.getPlayerRefComponentType();
            PlayerRef playerRef = store.getComponent(ref, refType);
            String playerUsername = playerRef.getUsername();
            PlayerStats stats = hytaleFoundations.getPlayerDataManager().getPlayerStats(playerUsername);
            if(stats != null) {
                stats.addDeath();
            }
        } catch (Exception e) {
            FileManager.logError("PlayerDeathListener", e);
        }
    }
}