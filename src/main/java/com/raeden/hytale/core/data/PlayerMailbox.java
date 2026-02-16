package com.raeden.hytale.core.data;

import com.raeden.hytale.modules.mail.MailManager;

import java.util.ArrayList;
import java.util.List;

public class PlayerMailbox {
    private boolean hasUnreadMail;
    private List<MailManager.Mail> mailList = new ArrayList<>();

    public List<MailManager.Mail> getMailList() {return mailList;}
    public void addMail(MailManager.Mail mail) {
        mailList.add(mail);
        hasUnreadMail = true;
    }
    public void readMail(MailManager.Mail mail) {
        hasUnreadMail = false;
        mail.setRead(true);
    }
    public void setMailList(List<MailManager.Mail> mailList) {this.mailList = mailList;}

    public boolean isHasUnreadMail() {return hasUnreadMail;}
    public void setHasUnreadMail(boolean hasUnreadMail) {this.hasUnreadMail = hasUnreadMail;}
}
