package com.raeden.hytale.core.events.playerEvents;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.player.PlayerStats;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerDamageListener extends DamageEventSystem {
    private final HytaleFoundations hytaleFoundations;
    public PlayerDamageListener(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
    }
    @Override
    public void handle(int i, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage damage) {
        if(damage.isCancelled() || damage.getAmount() <= 0.0F) return;
        Ref<EntityStore> reference = archetypeChunk.getReferenceTo(i);
        PlayerRef targetPlayer = store.getComponent(reference, PlayerRef.getComponentType());

        Damage.Source source = damage.getSource();
        Ref<EntityStore> attackerRef = getAttackerReference(source);
        if(attackerRef == null) return;
        PlayerRef attackerPlayer = store.getComponent(attackerRef, PlayerRef.getComponentType());

        // Case PVP
        if(attackerPlayer != null && targetPlayer != null) {
            PlayerStats attackerStats = hytaleFoundations.getPlayerDataManager().getPlayerStats(attackerPlayer.getUsername());
            PlayerStats targetStats = hytaleFoundations.getPlayerDataManager().getPlayerStats(targetPlayer.getUsername());
            if(attackerStats.isCollectStats()) attackerStats.addDamageGivenPvp((int) damage.getAmount());
            if(targetStats.isCollectStats()) targetStats.addDamageTakenPvp((int) damage.getAmount());
        }
        // Case PVE (Player getting attacked)
        if(attackerPlayer == null && targetPlayer != null) {
            PlayerStats targetStats = hytaleFoundations.getPlayerDataManager().getPlayerStats(targetPlayer.getUsername());
            if(targetStats.isCollectStats()) targetStats.addDamageTakenPve((int) damage.getAmount());
        }
        // Case PVE (Player is attacking)
        if(attackerPlayer != null && targetPlayer == null) {
            PlayerStats attackerStats = hytaleFoundations.getPlayerDataManager().getPlayerStats(attackerPlayer.getUsername());
            if(attackerStats.isCollectStats()) attackerStats.addDamageGivenPve((int) damage.getAmount());
        }
    }

    private Ref<EntityStore> getAttackerReference(Damage.Source dmgSource) {
        if(dmgSource instanceof Damage.EntitySource entitySource) {
            return entitySource.getRef();
        }
        return null;
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Query.any();
    }
}
