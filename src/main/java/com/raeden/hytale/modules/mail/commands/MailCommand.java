package com.raeden.hytale.modules.mail.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.permission.Permissions;
import com.raeden.hytale.modules.mail.MailManager;
import com.raeden.hytale.modules.mail.pages.MailBoxPage;
import com.raeden.hytale.modules.mail.pages.SendMailPage;
import com.raeden.hytale.utils.TimeUtils;

import javax.annotation.Nonnull;

public class MailCommand extends AbstractCommandCollection {
    private final HytaleFoundations hytaleFoundations;

    public MailCommand(HytaleFoundations hytaleFoundations) {
        super("mail", "All mail command context.");
        this.hytaleFoundations = hytaleFoundations;
        this.requirePermission(Permissions.MAIL.getPermission());
        this.addSubCommand(new SendMailCommand(hytaleFoundations));
        this.addSubCommand(new QuickMailCommand(hytaleFoundations));
        this.addSubCommand(new MailInboxCommand(hytaleFoundations));
    }
    public static class MailInboxCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        public MailInboxCommand(HytaleFoundations hytaleFoundations) {
            super("inbox", "Opens your mailbox");
            this.hytaleFoundations = hytaleFoundations;
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            Player player = store.getComponent(ref, Player.getComponentType());
            MailBoxPage mailBoxPage = new MailBoxPage(hytaleFoundations, playerRef);
            if(player == null) return;
            player.getPageManager().openCustomPage(ref, store, mailBoxPage);
        }
    }
    public static class SendMailCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        public SendMailCommand(HytaleFoundations hytaleFoundations) {
            super("send", "Opens send mail interface.");
            this.hytaleFoundations = hytaleFoundations;
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            Player player = store.getComponent(ref, Player.getComponentType());
            SendMailPage sendMailPage = new SendMailPage(hytaleFoundations, playerRef);
            if(player == null) return;
            player.getPageManager().openCustomPage(ref, store, sendMailPage);
        }
    }
    public static class QuickMailCommand extends AbstractPlayerCommand {
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

}
