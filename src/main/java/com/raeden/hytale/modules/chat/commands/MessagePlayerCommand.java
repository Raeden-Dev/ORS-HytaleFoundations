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
import com.raeden.hytale.core.data.PlayerData;
import com.raeden.hytale.lang.LangKey;
import com.raeden.hytale.modules.chat.ChatManager;

import javax.annotation.Nonnull;

import static com.raeden.hytale.HytaleFoundations.langManager;
import static com.raeden.hytale.core.utils.Permissions.isPlayerAdmin;
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
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.RECEIVER_NOT_ONLINE, receiverUsername));
            return;
        }

        ChatManager chatManager = hytaleFoundations.getChatManager();
        PlayerData senderData = hytaleFoundations.getPlayerDataManager().getPlayerData(senderUsername);
        PlayerData receiverData = hytaleFoundations.getPlayerDataManager().getPlayerData(receiverUsername);

        if(senderData.isMuted() && !isAdmin) {
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.PLAYER_MUTED_PM, receiverUsername));
            return;
        }
        if(receiverData.isMuted() && !isAdmin) {
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.RECEIVER_IS_MUTED, receiverUsername));
            return;
        }

        if(receiverData.getBlockedPlayers().contains(senderUsername) && !isAdmin) {
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.PLAYER_BLOCKED_SENDER, receiverUsername));
            return;
        }

        if(receiverUsername.equals(senderUsername) && !isAdmin) {
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.PLAYER_SELF_MSG));
            return;
        }

        receiver.sendMessage(langManager.getMessage(receiverUsername, LangKey.PRIVATE_MSG_FORMAT_RECEIVER, senderUsername, messageContent));
        sender.sendMessage(langManager.getMessage(senderUsername, LangKey.PRIVATE_MSG_FORMAT_SENDER, receiverUsername, messageContent));

        // Need to add for admins
        senderData.increaseMessageSent();
        chatManager.addActiveMessengers(senderUsername, receiverUsername);
    }
}
