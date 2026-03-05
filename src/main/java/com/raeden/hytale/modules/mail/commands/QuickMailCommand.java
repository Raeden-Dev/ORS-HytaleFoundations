package com.raeden.hytale.modules.mail.commands;

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
import com.raeden.hytale.modules.mail.MailManager;
import com.raeden.hytale.utils.TimeUtils;
import javax.annotation.Nonnull;

public class QuickMailCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;
    private final RequiredArg<String> targetPlayer;
    private final RequiredArg<String> message;

    public QuickMailCommand(HytaleFoundations hytaleFoundations) {
        super("quick", "Send a mail to a player.");
        this.hytaleFoundations = hytaleFoundations;
        this.setAllowsExtraArguments(true);

        targetPlayer = withRequiredArg("Player", "Player to send the mail.", ArgTypes.STRING);
        message = withRequiredArg("Message", "Message to send the player.", ArgTypes.STRING);
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
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
        mailManager.sendMailToPlayer(receiverUsername, mail);
    }
}
