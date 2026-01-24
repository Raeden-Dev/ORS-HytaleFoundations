package com.raeden.hytale.modules.chat.events;

import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.raeden.hytale.HytaleEssentials;
import com.raeden.hytale.core.data.PlayerData;
import com.raeden.hytale.lang.LangKey;
import com.raeden.hytale.modules.chat.ChatManager;
import com.raeden.hytale.utils.TimeUtils;

import static com.raeden.hytale.HytaleEssentials.langManager;

public class PlayerChatListener {
    public static void onPlayerChat(PlayerChatEvent e, HytaleEssentials hytaleEssentials) {
        System.out.println("PLAYER CHAT EVENT");
        ChatManager chatManager = hytaleEssentials.getChatManager();
        PlayerRef playerRef = e.getSender();
        String playerUsername = playerRef.getUsername();

        PlayerData senderData = hytaleEssentials.getPlayerDataManager().getPlayerData(playerUsername);
        if(senderData == null) return;

        if(senderData.isMuted()) {
            playerRef.sendMessage(langManager.getMessage(playerUsername, LangKey.PLAYER_MUTED, TimeUtils.formatDuration(senderData.getMuteDuration())));
            e.setCancelled(true);
            return;
        }

        chatManager.addMessageToLog(e.getContent());
        senderData.increaseMessageSent();
    }
}
