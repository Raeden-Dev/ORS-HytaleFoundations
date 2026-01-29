package com.raeden.hytale.core.events.playerEvents;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.raeden.hytale.HytaleFoundations.myLogger;

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
        myLogger.atInfo().log("Player just died!");
        Player playerComponent = (Player) store.getComponent(ref, Player.getComponentType());
        assert playerComponent != null;
    }


}
