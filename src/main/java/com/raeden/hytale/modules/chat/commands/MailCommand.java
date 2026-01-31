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
import com.raeden.hytale.core.data.PlayerDataManager;
import com.raeden.hytale.core.data.PlayerMailbox;
import com.raeden.hytale.modules.chat.MailManager;
import com.raeden.hytale.utils.TimeUtils;

import javax.annotation.Nonnull;

import static com.raeden.hytale.core.utils.Permissions.isPlayerAdmin;

public class MailCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;
    private final RequiredArg<String> targetPlayer;
    private final RequiredArg<String> message;

    public MailCommand(HytaleFoundations hytaleFoundations) {
        super("mail", "Send a mail to a player.");
        this.hytaleFoundations = hytaleFoundations;
        this.setAllowsExtraArguments(true);

        targetPlayer = withRequiredArg("Player", "Player to send the mail.", ArgTypes.STRING);
        message = withRequiredArg("Message", "Message to send the player.", ArgTypes.STRING);
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        boolean isAdmin = isPlayerAdmin(commandContext.sender());

        String senderUsername = commandContext.sender().getDisplayName();
        String receiverUsername = commandContext.get(this.targetPlayer);
        String[] rawMessage = commandContext.getInputString().split("\\s+", 3);
        String messageContent = rawMessage[2];

        MailManager mailManager = hytaleFoundations.getMailManager();

        MailManager.Mail mail = new MailManager.Mail(
                senderUsername,
                receiverUsername,
                " ",
                TimeUtils.getCurrentTime(),
                messageContent
        );

        PlayerDataManager dataManager = hytaleFoundations.getPlayerDataManager();

        PlayerMailbox mailbox = dataManager.getPlayerMailbox(receiverUsername);
        mailbox.addMail(mail);

        dataManager.savePlayerData(receiverUsername, dataManager.MAIL_JSON, mailbox);

    }
}
