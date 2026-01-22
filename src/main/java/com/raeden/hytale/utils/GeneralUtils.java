package com.raeden.hytale.utils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.lang.LangKey;

import static com.raeden.hytale.HytaleEssentials.langManager;
import static com.raeden.hytale.HytaleEssentials.myLogger;

public class GeneralUtils {
    public static boolean playerHasInventorySpace(PlayerRef playerRef, int slots) {
        if(slots <= 0) {
            return true;
        }

        try {
            if(playerRef == null) {
                return false;
            }

            Ref<EntityStore> entityStoreRef = playerRef.getReference();
            if(entityStoreRef != null && entityStoreRef.isValid()) {
                Store<EntityStore> store = entityStoreRef.getStore();
                Player player = store.getComponent(entityStoreRef, Player.getComponentType());
                if(player == null) {
                    return false;
                } else {
                    ItemContainer inventory = player.getInventory().getCombinedEverything();
                    int availableSlots = 0;
                    short totalSlots = inventory.getCapacity();

                    for(short i = 0; i < totalSlots; ++i) {
                        ItemStack item = inventory.getItemStack(i);
                        if(item == null || item.isEmpty()) {
                            ++availableSlots;
                        }
                    }

                    return availableSlots >= slots;
                }
            } else {
                return false;
            }
        }  catch (Exception e) {
            myLogger.atWarning().log(langManager.getMessage(null, LangKey.INV_SLOT_CHECK_ERROR).toString());
            return false;
        }
    }

    public static PlayerRef findPlayerByName(String username) {
        try {
            Universe universe = Universe.get();
            if(universe == null) {
                return null;
            } else {
                PlayerRef player = universe.getPlayerByUsername(username, NameMatching.EXACT);
                if(player == null) {
                    myLogger.atWarning().log("Failed to find player with username: " + username);
                }

                return player;
            }
        } catch (Exception e) {
            myLogger.atWarning().log("Failed to find player with username: " + username + " - " + e.getMessage());
            return null;
        }
    }

    public static boolean isPlayerOnline(String username) {
        try {
            PlayerRef playerRef = findPlayerByName(username);
            return playerRef != null && playerRef.getReference() != null && playerRef.getReference().isValid();
        } catch (Exception e) {
            myLogger.atWarning().log("Error checking if player is online: " + username + " - " + e.getMessage());
            return false;
        }
    }
}
