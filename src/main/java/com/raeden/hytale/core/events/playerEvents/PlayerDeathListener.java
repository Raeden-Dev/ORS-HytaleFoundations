package com.raeden.hytale.core.events.playerEvents;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleEssentials;
import com.raeden.hytale.core.data.PlayerDataManager;
import com.raeden.hytale.core.data.PlayerData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.raeden.hytale.HytaleEssentials.myLogger;

public class PlayerDeathListener extends DeathSystems.OnDeathSystem {
    private final HytaleEssentials hytaleEssentials;
    public PlayerDeathListener(HytaleEssentials hytaleEssentials) {
        this.hytaleEssentials = hytaleEssentials;
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

        PlayerDataManager dataManager = hytaleEssentials.getPlayerDataManager();
        PlayerData data = dataManager.getPlayerMetaData(playerComponent.getDisplayName());

        if(deathComponent.getDeathInfo() != null) {
            data.setDamageTaken((int) deathComponent.getDeathInfo().getAmount());
        }

        data.addDeath();
    }


}
