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
import com.raeden.hytale.HytaleEssentials;
import com.raeden.hytale.core.data.PlayerData;
import com.raeden.hytale.lang.LangKey;
import com.raeden.hytale.modules.chat.ChatManager;

import javax.annotation.Nonnull;

import static com.raeden.hytale.HytaleEssentials.langManager;
import static com.raeden.hytale.utils.GeneralUtils.findPlayerByName;

public class MessagePlayerCommand extends AbstractPlayerCommand {
    private final HytaleEssentials hytaleEssentials;
    private final RequiredArg<String> receiver;
    private final RequiredArg<String> message;

    public MessagePlayerCommand(HytaleEssentials hytaleEssentials) {
        super("message", "Send a private message to a player");
        this.hytaleEssentials = hytaleEssentials;
        this.addAliases("msg");

        receiver = withRequiredArg("player", "The message receiver", ArgTypes.STRING);
        message = withRequiredArg("message", "Message to send the receiver", ArgTypes.STRING);
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef sender, @Nonnull World world) {
        String senderUsername = commandContext.sender().getDisplayName();
        String receiverUsername = receiver.toString();
        PlayerRef receiver = findPlayerByName(String.valueOf(this.receiver));
        if(receiver == null) {
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.RECEIVER_NOT_ONLINE, receiverUsername));
            return;
        }

        ChatManager chatManager = hytaleEssentials.getChatManager();
        PlayerData senderData = hytaleEssentials.getPlayerDataManager().getPlayerMetaData(sender.getUsername());
        PlayerData receiverData = hytaleEssentials.getPlayerDataManager().getPlayerMetaData(receiver.getUsername());

        if(senderData.isMuted()) {
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.PLAYER_MUTED_PM, receiverUsername));
            return;
        }
        if(receiverData.isMuted()) {
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.RECEIVER_IS_MUTED, receiverUsername));
            return;
        }

        if(receiverData.getBlockedPlayers().contains(senderUsername)) {
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.PLAYER_BLOCKED_SENDER, receiverUsername));
            return;
        }

        senderData.setSendingPvtMsg(true);
        receiver.sendMessage(langManager.getMessage(receiverUsername, LangKey.PRIVATE_MSG_FORMAT_RECEIVER, senderUsername, message.toString()));
        sender.sendMessage(langManager.getMessage(senderUsername, LangKey.PRIVATE_MSG_FORMAT_SENDER, receiverUsername, message.toString()));

        // Need to add for admins
        senderData.setSendingPvtMsg(false);
        chatManager.addActiveMessengers(senderUsername, receiverUsername);

    }
}
