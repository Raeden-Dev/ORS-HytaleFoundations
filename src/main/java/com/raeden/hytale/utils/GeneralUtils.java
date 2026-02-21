package com.raeden.hytale.utils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.lang.LangKey;

import java.util.Objects;
import java.util.UUID;

import static com.raeden.hytale.HytaleFoundations.langManager;
import static com.raeden.hytale.HytaleFoundations.myLogger;
import static com.raeden.hytale.utils.FileManager.logError;

public class GeneralUtils {
    public static UUID getPlayerUUID(String username) {
        return getPlayerUUID(findPlayerByName(username));
    }
    public static UUID getPlayerUUID(PlayerRef playerRef) {
        Ref<EntityStore> ref = playerRef.getReference();
        Store<EntityStore> store = Objects.requireNonNull(ref).getStore();
        UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());

        if (uuidComponent != null) {
            return uuidComponent.getUuid();
        }

        return null;
    }

    public static boolean playerHasInventorySpace(PlayerRef playerRef, int slots) {
        return playerHasInventorySpace(null, playerRef, slots);
    }
    public static boolean playerHasInventorySpace(String caller, PlayerRef playerRef, int slots) {
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
            myLogger.atWarning().log((caller == null ? "" : "[Called by: " + caller + "]") +
                    langManager.getMessage(LangKey.PLAYER_INV_CHECK_FAIL).getAnsiMessage());
            return false;
        }
    }

    public static PlayerRef findPlayerByName(String username) {
        return findPlayerByName(null, username);
    }

    public static PlayerRef findPlayerByName(String caller, String username) {
        try {
            Universe universe = Universe.get();
            if(universe == null) {
                return null;
            } else {
                PlayerRef player = universe.getPlayerByUsername(username, NameMatching.EXACT);
                if(player == null) {
                    myLogger.atWarning().log((caller == null ? "" : "[Called by: " + caller + "]") +
                            langManager.getMessage(LangKey.PLAYER_NOT_FOUND, username).getAnsiMessage()
                    );
                }

                return player;
            }
        } catch (Exception e) {
            FileManager.logError("findPlayerByName", e);
            myLogger.atWarning().log((caller == null ? "" : "[Called by: " + caller + "]") +
                    langManager.getMessage(LangKey.PLAYER_NOT_FOUND, username).getAnsiMessage() + e.getMessage());
            return null;
        }
    }

    public static boolean isPlayerOnline(String username) {
        return isPlayerOnline(null, username);
    }

    public static boolean isPlayerOnline(String caller, String username) {
        try {
            PlayerRef playerRef = findPlayerByName(username);
            return playerRef != null && playerRef.getReference() != null && playerRef.getReference().isValid();
        } catch (Exception e) {
            myLogger.atWarning().log((caller == null ? "" : "[Called by: " + caller + "]") +
                    langManager.getMessage(LangKey.PLAYER_ONLINE_CHECK_FAIL, username).getAnsiMessage() + " - " + e.getMessage());
            return false;
        }
    }
}
