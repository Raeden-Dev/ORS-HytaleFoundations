package com.raeden.hytale.core.events.playerEvents;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.player.PlayerStats;
import com.raeden.hytale.utils.FileUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.raeden.hytale.utils.FileUtils.logError;

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
            if(store.getComponent(ref, Player.getComponentType()) != null) return;

            Damage deathInfo = deathComponent.getDeathInfo();
            if(deathInfo == null) return;
            Ref<EntityStore> attackerRef = getAttackerReference(deathInfo);
            if(attackerRef == null) return;

            PlayerRef playerRef = store.getComponent(attackerRef, PlayerRef.getComponentType());
            if(playerRef == null) return;

            String playerUsername = playerRef.getUsername();
            PlayerStats stats = hytaleFoundations.getPlayerDataManager().getPlayerStats(playerUsername);
            if(stats == null || !stats.isCollectStats()) return;
            stats.addMobKill();

            Player player = store.getComponent(attackerRef, Player.getComponentType());
            if(player == null) return;
            ItemStack item = player.getInventory().getItemInHand();
            if(item != null) {
                if(!item.isBroken() && item.getDurability() != 0.0) {
                    if(item.getDurability() <= 1.0) {
                        stats.addItemBroken();
                    }
                }
            }
        } catch (Exception e) {
            FileUtils.logError("PlayerMobKillListener", e);
        }
    }

    private Ref<EntityStore> getAttackerReference(Damage damage) {
        Damage.Source source = damage.getSource();
        if(source instanceof Damage.EntitySource es) {
            return es.getRef();
        }
        return null;
    }
}