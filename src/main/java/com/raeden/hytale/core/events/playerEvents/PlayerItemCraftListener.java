package com.raeden.hytale.core.events.playerEvents;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.asset.type.item.config.CraftingRecipe;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.CraftRecipeEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.player.PlayerStats;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.event.ItemListener;

public class PlayerItemCraftListener extends EntityEventSystem<EntityStore, CraftRecipeEvent.Post> {
    private final HytaleFoundations hytaleFoundations;
    public PlayerItemCraftListener(HytaleFoundations hytaleFoundations) {
        super(CraftRecipeEvent.Post.class);
        this.hytaleFoundations = hytaleFoundations;
    }
    @Override
    public void handle(int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull CraftRecipeEvent.Post event) {
        Ref<EntityStore> reference = archetypeChunk.getReferenceTo(i);
        PlayerRef crafterRef = store.getComponent(reference, PlayerRef.getComponentType());
        if(crafterRef == null) return;
        PlayerStats crafterStats = hytaleFoundations.getPlayerDataManager().getPlayerStats(crafterRef.getUsername());
        if(crafterStats != null && crafterStats.isCollectStats()) {
            crafterStats.addItemCraft();
        }

    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.empty();
    }

}
