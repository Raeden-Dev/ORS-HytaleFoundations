package com.raeden.hytale.modules.mail;

import com.hypixel.hytale.protocol.packets.interface_.NotificationStyle;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.player.PlayerDataManager;
import com.raeden.hytale.core.player.PlayerMailbox;
import com.raeden.hytale.lang.LangKey;

import java.util.List;

import static com.raeden.hytale.HytaleFoundations.langManager;
import static com.raeden.hytale.utils.GeneralUtils.findPlayerByName;

public class MailManager {
    private final HytaleFoundations hytaleFoundations;

    public MailManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
    }

    public void sendMailToPlayer(String receiver, Mail mail) {
        sendMailToPlayer(null, receiver, mail);
    }
    public void sendMailToPlayer(PlayerRef sender, String receiver, Mail mail) {
        PlayerDataManager playerDataManager = hytaleFoundations.getPlayerDataManager();
        PlayerMailbox mailbox = playerDataManager.getPlayerMailbox(receiver);
        if(mailbox != null) {
            mailbox.addMail(mail);
            playerDataManager.savePlayerData(receiver, playerDataManager.MAIL_JSON, mailbox, false);
            if(sender != null) {
                sender.sendMessage(langManager.getMessage(sender.getUsername(), LangKey.MAIL_SEND_SUCCESS, false, receiver));
            }
        } else {
            if(sender != null) {
                sender.sendMessage(langManager.getMessage(sender.getUsername(), LangKey.MAIL_SEND_FAILURE, false, receiver));
            }
        }
    }

    public void doesPlayerHaveUnreadMails(String username) {
        PlayerMailbox mailbox = hytaleFoundations.getPlayerDataManager().getPlayerMailbox(username);
        List<Mail> mailList = mailbox.getMailList();
        if(mailList == null) {
            return;
        }
        int unreadMailCount = 0;
        for(Mail mail : mailList) {
            if(!mail.isRead()) {
                mailbox.setHasUnreadMail(true);
                unreadMailCount++;
            }
        }

        if(!mailbox.isHasUnreadMail()) {
            return;
        }

        PlayerRef ref = findPlayerByName(username);
        PacketHandler handler = ref.getPacketHandler();
        Message pm = langManager.getMessage(LangKey.MAIL_NOTIFY_UNREAD, false, String.valueOf(unreadMailCount));
        Message sm = langManager.getMessage(LangKey.MAIL_NOTIFY_CHECK, false);
        NotificationUtil.sendNotification(handler, pm, sm,  NotificationStyle.Default);
    }

    public static class Mail {
        private final String from;
        private final String to;
        private final String title;
        private final String timeSent;
        private final String message;
        private boolean read;

        public Mail(String from, String to, String title, String timeSent, String message) {
            this.from = from;
            this.to = to;
            this.title = title;
            this.timeSent = timeSent;
            this.message = message;
            this.read = false;
        }

        public String getFrom() {return from;}
        public String getTo() {return to;}
        public String getTitle() {return title;}
        public String getTimeSent() {return timeSent;}
        public String getMessage() {return message;}

        public boolean isRead() {return read;}
        public void setRead(boolean read) {this.read = read;}
    }
}
