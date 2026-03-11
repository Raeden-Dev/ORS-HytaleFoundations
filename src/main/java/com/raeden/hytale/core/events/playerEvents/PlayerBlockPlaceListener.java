package com.raeden.hytale.core.events.playerEvents;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.player.PlayerProfile;
import com.raeden.hytale.core.player.PlayerStats;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerBlockPlaceListener extends EntityEventSystem<EntityStore, PlaceBlockEvent> {
    private final HytaleFoundations hytaleFoundations;
    public PlayerBlockPlaceListener(HytaleFoundations hytaleFoundations) {
        super(PlaceBlockEvent.class);
        this.hytaleFoundations = hytaleFoundations;
    }
    @Override
    public void handle(int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull PlaceBlockEvent placeBlockEvent) {
        ItemStack block = placeBlockEvent.getItemInHand();
        if(block == null) return;
        String blockID = block.getItemId();
        if(blockID.equals("Empty")) return;
        Ref<EntityStore> reference = archetypeChunk.getReferenceTo(i);
        PlayerRef player = store.getComponent(reference, PlayerRef.getComponentType());
        if(player == null) return;
        PlayerStats stats = hytaleFoundations.getPlayerDataManager().getPlayerStats(player.getUsername());
        if(stats != null && stats.isCollectStats()) {
            stats.addBlockPlace();
        }
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return PlayerRef.getComponentType();
    }
}
