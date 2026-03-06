package com.raeden.hytale.modules.chat.events;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.player.PlayerProfile;
import com.raeden.hytale.core.player.PlayerStats;
import com.raeden.hytale.lang.LangKey;
import com.raeden.hytale.modules.chat.ChatManager;
import com.raeden.hytale.modules.chat.ColorManager;
import com.raeden.hytale.utils.TimeUtils;

import static com.raeden.hytale.HytaleFoundations.LM;
import static com.raeden.hytale.HytaleFoundations.myLogger;

public class PlayerChatListener {
    public static void onPlayerChat(PlayerChatEvent e, HytaleFoundations hytaleFoundations) {
        ChatManager chatManager = hytaleFoundations.getChatManager();
        ColorManager colorManager = chatManager.getColorEngine();
        PlayerRef playerRef = e.getSender();
        String playerUsername = playerRef.getUsername();
        boolean isAdmin = hytaleFoundations.getPermissionManager().isPlayerAdmin(playerRef);

        PlayerProfile profile = hytaleFoundations.getPlayerDataManager().getOnlinePlayerProfile(playerUsername);
        PlayerStats stats = hytaleFoundations.getPlayerDataManager().getOnlinePlayerStats(playerUsername);
        if(profile == null) {
            myLogger.atSevere().log(LM.getConsoleMessage(LangKey.NULL_POINTER, "PlayerChatListener - profile").getAnsiMessage());
            return;
        }

        if(hytaleFoundations.getConfigManager().getDefaultConfig().isToggleChatModule()) {
            e.setCancelled(true);
            if(profile.isMuted() && !isAdmin) {
                playerRef.sendMessage(LM.getPlayerMessage(playerUsername, LangKey.MUTE_ERROR_CHAT_TIME,TimeUtils.formatDuration(profile.getMuteDuration())));
                return;
            }
            String chatContent = e.getContent();
            String formattedChatContent = hytaleFoundations.getChatManager().formatChat(profile, playerUsername, chatContent);

            Message finalMessage = Message.empty();
            finalMessage.insert(colorManager.parseText(formattedChatContent));

            for(PlayerRef players : Universe.get().getPlayers()) {
                players.sendMessage(finalMessage);
            }
            chatManager.addMessageToLog(e.getContent());
        }

        stats.increaseMessageSent();
    }
}
