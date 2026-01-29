package com.raeden.hytale.modules.chat.events;

import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.data.PlayerProfile;
import com.raeden.hytale.core.data.PlayerStats;
import com.raeden.hytale.lang.LangKey;
import com.raeden.hytale.modules.chat.ChatManager;
import com.raeden.hytale.utils.TimeUtils;

import static com.raeden.hytale.HytaleFoundations.langManager;

public class PlayerChatListener {
    public static void onPlayerChat(PlayerChatEvent e, HytaleFoundations hytaleFoundations) {
        ChatManager chatManager = hytaleFoundations.getChatManager();
        PlayerRef playerRef = e.getSender();
        String playerUsername = playerRef.getUsername();

        PlayerProfile profile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(playerUsername);
        PlayerStats stats = hytaleFoundations.getPlayerDataManager().getPlayerStats(playerUsername);
        if(profile == null) return;

        if(profile.isMuted()) {
            playerRef.sendMessage(langManager.getMessage(playerUsername, LangKey.PLAYER_MUTED, TimeUtils.formatDuration(profile.getMuteDuration())));
            e.setCancelled(true);
            return;
        }

        chatManager.addMessageToLog(e.getContent());
        stats.increaseMessageSent();
    }
}
