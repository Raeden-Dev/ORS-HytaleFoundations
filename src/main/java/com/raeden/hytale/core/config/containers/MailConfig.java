package com.raeden.hytale.core.config.containers;

import java.util.ArrayList;
import java.util.List;

public class MailConfig {
    private String VERSION;
    private int MAX_INBOX_SIZE;
    private int MAX_MAIL_LINES;
    private int MAX_MAIL_PER_DAY;
    private boolean ALLOW_GIFTING;
    private int MAX_GIFT_PER_MAIL;
    private final List<String> BLACKLISTED_GIFTS = new ArrayList<>();

    public String getVersion() {return VERSION;}
    public void setVersion(String VERSION) {this.VERSION = VERSION;}

    public int getMaxInboxSize() {return MAX_INBOX_SIZE;}
    public void setMaxInboxSize(int MAX_INBOX_SIZE) {this.MAX_INBOX_SIZE = MAX_INBOX_SIZE;}

    public int getMaxMailLines() {return MAX_MAIL_LINES;}
    public void setMaxMailLines(int MAX_MAIL_LINES) {this.MAX_MAIL_LINES = MAX_MAIL_LINES;}

    public int getMaxMailPerDay() {return MAX_MAIL_PER_DAY;}
    public void setMaxMailPerDay(int MAX_MAIL_PER_DAY) {this.MAX_MAIL_PER_DAY = MAX_MAIL_PER_DAY;}

    public boolean isAllowGifting() {return ALLOW_GIFTING;}
    public void setAllowGifting(boolean ALLOW_GIFTING) {this.ALLOW_GIFTING = ALLOW_GIFTING;}

    public int getMaxGiftPerMail() {return MAX_GIFT_PER_MAIL;}
    public void setMaxGiftPerMail(int MAX_GIFT_PER_MAIL) {this.MAX_GIFT_PER_MAIL = MAX_GIFT_PER_MAIL;}

    public List<String> getBlacklistedGifts() {return BLACKLISTED_GIFTS;}
    public void addGiftToBlacklist(String itemID) {BLACKLISTED_GIFTS.add(itemID);}
}
