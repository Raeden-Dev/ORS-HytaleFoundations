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
import static com.raeden.hytale.core.utils.PermissionManager.isPlayerAdmin;
import static com.raeden.hytale.utils.GeneralUtils.findPlayerByName;

public class MessagePlayerCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;
    private final RequiredArg<String> receiver;
    private final RequiredArg<String> message;

    public MessagePlayerCommand(HytaleFoundations hytaleFoundations) {
        super("message", "Send a private message to a player");
        this.hytaleFoundations = hytaleFoundations;
        this.addAliases("msg");
        this.setAllowsExtraArguments(true);

        receiver = withRequiredArg("player", "The message receiver", ArgTypes.STRING);
        message = withRequiredArg("message", "Message to send the receiver", ArgTypes.STRING);
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef sender, @Nonnull World world) {
        boolean isAdmin = isPlayerAdmin(commandContext.sender());
        String senderUsername = commandContext.sender().getDisplayName();
        String receiverUsername = commandContext.get(this.receiver);
        String[] rawMessage = commandContext.getInputString().split("\\s+", 3);
        String messageContent = rawMessage[2];

        PlayerRef receiver = findPlayerByName("Message Command", receiverUsername);
        if(receiver == null) {
            commandContext.sender().sendMessage(LM.getMessage(senderUsername, LangKey.PM_ERROR_OFFLINE,false, receiverUsername));
            return;
        }

        ChatManager chatManager = hytaleFoundations.getChatManager();
        PlayerDataManager dataManager = hytaleFoundations.getPlayerDataManager();
        PlayerProfile senderProfile = dataManager.getOnlinePlayerProfile(senderUsername);
        PlayerStats senderStats = dataManager.getOnlinePlayerStats(senderUsername);
        PlayerProfile receiverProfile = dataManager.getOnlinePlayerProfile(receiverUsername);

        if(senderProfile.isMuted() && !isAdmin) {
            commandContext.sender().sendMessage(LM.getMessage(senderUsername, LangKey.MUTE_ERROR_CHAT,false, receiverUsername));
            return;
        }
        if(receiverProfile.isMuted() && !isAdmin) {
            commandContext.sender().sendMessage(LM.getMessage(senderUsername, LangKey.PM_ERROR_TARGET_MUTED,false, receiverUsername));
            return;
        }

        if(receiverProfile.getBlockedPlayers().contains(senderUsername) && !isAdmin) {
            commandContext.sender().sendMessage(LM.getMessage(senderUsername, LangKey.PM_ERROR_SENDER_BLOCKED,false, receiverUsername));
            return;
        }

        if(receiverUsername.equals(senderUsername) && !isAdmin) {
            commandContext.sender().sendMessage(LM.getMessage(senderUsername, LangKey.PM_ERROR_SELF,false));
            return;
        }

        receiver.sendMessage(LM.getMessage(receiverUsername, LangKey.PM_FORMAT_RECEIVER,false, senderUsername, messageContent));
        sender.sendMessage(LM.getMessage(senderUsername, LangKey.PM_FORMAT_SENDER,false, receiverUsername, messageContent));

        // Need to add for admins
        senderStats.increaseMessageSent();
        chatManager.addActiveMessengers(senderUsername, receiverUsername);
    }
}
