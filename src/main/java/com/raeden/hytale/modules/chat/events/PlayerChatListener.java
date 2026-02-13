package com.raeden.hytale.modules.chat.events;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.data.PlayerProfile;
import com.raeden.hytale.core.data.PlayerStats;
import com.raeden.hytale.lang.LangKey;
import com.raeden.hytale.modules.chat.ChatManager;
import com.raeden.hytale.utils.ColorEngine;
import com.raeden.hytale.utils.TimeUtils;

import static com.raeden.hytale.HytaleFoundations.langManager;
import static com.raeden.hytale.core.utils.Permissions.isPlayerAdmin;

public class PlayerChatListener {
    public static void onPlayerChat(PlayerChatEvent e, HytaleFoundations hytaleFoundations) {
        ChatManager chatManager = hytaleFoundations.getChatManager();
        ColorEngine colorEngine = chatManager.getColorEngine();
        PlayerRef playerRef = e.getSender();
        String playerUsername = playerRef.getUsername();
        boolean isAdmin = isPlayerAdmin(playerRef);

        PlayerProfile profile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(playerUsername);
        PlayerStats stats = hytaleFoundations.getPlayerDataManager().getPlayerStats(playerUsername);
        if(profile == null) return;

        e.setCancelled(true); // Cancel event anyway because of our Custom Chat

        if(profile.isMuted() && !isAdmin) {
            playerRef.sendMessage(langManager.getMessage(playerUsername, LangKey.MUTE_ERROR_CHAT_TIME, TimeUtils.formatDuration(profile.getMuteDuration())));
            return;
        }

        Message finalMessage = Message.empty();
        finalMessage.insert(colorEngine.parseText(e.getContent()));

        for(PlayerRef players : Universe.get().getPlayers()) {
            players.sendMessage(finalMessage);
        }
        chatManager.addMessageToLog(e.getContent());
        stats.increaseMessageSent();
    }
}
