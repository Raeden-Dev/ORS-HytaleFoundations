package com.raeden.hytale.core.events.playerEvents;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.player.PlayerDataManager;

public class PlayerServerJoinListener {
    public static void onPlayerJoin(PlayerReadyEvent e, HytaleFoundations hytaleFoundations) {
        Player player = e.getPlayer();
        String username = player.getDisplayName();

        PlayerDataManager dataManager = hytaleFoundations.getPlayerDataManager();
        dataManager.playerLogin(player);
        if(hytaleFoundations.getMailManager() != null) {
            hytaleFoundations.getMailManager().doesPlayerHaveUnreadMails(username);
        }
    }
}
