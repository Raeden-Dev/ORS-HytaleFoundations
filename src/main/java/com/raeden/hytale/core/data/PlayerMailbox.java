package com.raeden.hytale.core.data;

import com.raeden.hytale.modules.chat.MailManager;

import java.util.List;

public class PlayerMailbox {
    private List<MailManager.Mail> mailList;

    public List<MailManager.Mail> getMailList() {return mailList;}
    public void setMailList(List<MailManager.Mail> mailList) {this.mailList = mailList;}
}
