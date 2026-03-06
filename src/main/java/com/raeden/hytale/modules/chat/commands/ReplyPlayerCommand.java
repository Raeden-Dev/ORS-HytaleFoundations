package com.raeden.hytale.modules.chat.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.player.PlayerDataManager;
import com.raeden.hytale.core.player.PlayerProfile;
import com.raeden.hytale.core.player.PlayerStats;
import com.raeden.hytale.lang.LangKey;
import com.raeden.hytale.modules.chat.ChatManager;

import javax.annotation.Nonnull;

import static com.raeden.hytale.HytaleFoundations.LM;
import static com.raeden.hytale.utils.GeneralUtils.findPlayerByName;

public class ReplyPlayerCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;
    private final RequiredArg<String> message;

    public ReplyPlayerCommand(HytaleFoundations hytaleFoundations) {
        super("reply", "Reply to your active private messenger.");
        this.hytaleFoundations = hytaleFoundations;
        this.addAliases("r", "re");
        this.setAllowsExtraArguments(true);

        message = withRequiredArg("message", "Message to send the receiver", ArgTypes.STRING);
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef sender, @Nonnull World world) {
        ChatManager chatManager = hytaleFoundations.getChatManager();

        boolean isAdmin = hytaleFoundations.getPermissionManager().isPlayerAdmin(commandContext.sender());
        String senderUsername = commandContext.sender().getDisplayName();
        String receiverUsername = chatManager.getReceiver(senderUsername);

        if(receiverUsername == null) {
            commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PM_ERROR_OFFLINE));
            return;
        }
        PlayerDataManager dataManager = hytaleFoundations.getPlayerDataManager();
        PlayerProfile senderProfile = dataManager.getOnlinePlayerProfile(senderUsername);
        PlayerStats senderStats = dataManager.getOnlinePlayerStats(senderUsername);
        PlayerProfile receiverProfile = dataManager.getOnlinePlayerProfile(receiverUsername);

        String[] rawMessage = commandContext.getInputString().split("\\s+", 2);
        String messageContent = rawMessage[1];

        PlayerRef receiver = findPlayerByName("Reply Command", receiverUsername);
        if(receiver == null) {
            commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PM_ERROR_OFFLINE, receiverUsername));
            return;
        }
        if(senderProfile.isMuted() && !isAdmin) {
            commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.MUTE_ERROR_CHAT,receiverUsername));
            return;
        }
        if(receiverProfile.isMuted() && !isAdmin) {
            commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PM_ERROR_TARGET_MUTED,receiverUsername));
            return;
        }

        if(receiverProfile.getBlockedPlayers().contains(senderUsername) && !isAdmin) {
            commandContext.sender().sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PM_ERROR_SENDER_BLOCKED,receiverUsername));
            return;
        }

        receiver.sendMessage(LM.getPlayerMessage(receiverUsername, LangKey.PM_FORMAT_RECEIVER,senderUsername, messageContent));
        sender.sendMessage(LM.getPlayerMessage(senderUsername, LangKey.PM_FORMAT_SENDER,receiverUsername, messageContent));

        senderStats.increaseMessageSent();
    }
}
