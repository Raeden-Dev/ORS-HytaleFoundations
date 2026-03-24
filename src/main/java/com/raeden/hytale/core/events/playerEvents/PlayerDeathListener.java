package com.raeden.hytale.core.events.playerEvents;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.player.PlayerStats;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.raeden.hytale.utils.FileUtils.logError;

public class PlayerDeathListener extends DeathSystems.OnDeathSystem {
    private final HytaleFoundations hytaleFoundations;
    public PlayerDeathListener(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType());
    }

    @Override
    public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent deathComponent, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        try {
            PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
            if(playerRef == null) return;
            String playerUsername = playerRef.getUsername();
            PlayerStats stats = hytaleFoundations.getPlayerDataManager().getPlayerStats(playerUsername);
            if(stats != null && stats.isCollectStats()) {
                stats.addDeath();
            }
        } catch (Exception e) {
            logError("PlayerDeathListener", e);
        }
    }


}
