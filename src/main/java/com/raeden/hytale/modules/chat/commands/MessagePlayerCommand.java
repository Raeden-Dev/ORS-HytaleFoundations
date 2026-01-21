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
import com.raeden.hytale.core.data.PlayerMetaData;
import com.raeden.hytale.lang.LangKey;

import javax.annotation.Nonnull;

import static com.raeden.hytale.HytaleEssentials.langManager;
import static com.raeden.hytale.utils.generalUtils.isPlayerOnline;

public class MessagePlayerCommand extends AbstractPlayerCommand {
    private final HytaleEssentials hytaleEssentials;
    private final RequiredArg<String> receiverUsername;
    private final RequiredArg<String> message;

    public MessagePlayerCommand(HytaleEssentials hytaleEssentials) {
        super("message", "Send a private message to a player");
        this.hytaleEssentials = hytaleEssentials;
        this.addAliases("msg");

        receiverUsername = withRequiredArg("player", "The message receiver", ArgTypes.STRING);
        message = withRequiredArg("message", "Message to send the receiver", ArgTypes.STRING);
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        PlayerMetaData senderData = hytaleEssentials.getPlayerDataManager().getPlayerMetaData(playerRef.getUsername());
        PlayerMetaData receiverData = hytaleEssentials.getPlayerDataManager().getPlayerMetaData(playerRef.getUsername());
        String senderUsername = commandContext.sender().getDisplayName();

        if(senderData.isMuted()) {
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.PLAYER_MUTED, receiverUsername.toString()));
            return;
        }
        if(receiverData.isMuted()) {
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.RECEIVER_IS_MUTED, receiverUsername.toString()));
            return;
        }
        if(!isPlayerOnline(receiverUsername.toString())) {
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.RECEIVER_NOT_ONLINE, receiverUsername.toString()));
            return;
        }


    }
}
