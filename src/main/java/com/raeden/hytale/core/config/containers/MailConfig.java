package com.raeden.hytale.core.config.containers;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class MailConfig {
    @SerializedName("VERSION")
    private String version;
    @SerializedName("MAX_INBOX_SIZE")
    private int maxInboxSize;
    @SerializedName("MAX_MAIL_LINES")
    private int maxMailLines;
    @SerializedName("MAX_MAIL_PER_DAY")
    private int maxMailPerDay;
    @SerializedName("ALLOW_GIFTING")
    private boolean allowGifting;
    @SerializedName("MAX_GIFT_PER_MAIL")
    private int maxGiftPerMail;
    @SerializedName("BLACKLISTED_GIFTS")
    private final List<String> blacklistedGifts = new ArrayList<>();

    // --- Getters and Setters ---

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public int getMaxInboxSize() { return maxInboxSize; }
    public void setMaxInboxSize(int maxInboxSize) { this.maxInboxSize = maxInboxSize; }

    public int getMaxMailLines() { return maxMailLines; }
    public void setMaxMailLines(int maxMailLines) { this.maxMailLines = maxMailLines; }

    public int getMaxMailPerDay() { return maxMailPerDay; }
    public void setMaxMailPerDay(int maxMailPerDay) { this.maxMailPerDay = maxMailPerDay; }

    public boolean isAllowGifting() { return allowGifting; }
    public void setAllowGifting(boolean allowGifting) { this.allowGifting = allowGifting; }

    public int getMaxGiftPerMail() { return maxGiftPerMail; }
    public void setMaxGiftPerMail(int maxGiftPerMail) { this.maxGiftPerMail = maxGiftPerMail; }

    public List<String> getBlacklistedGifts() { return blacklistedGifts; }
    public void addGiftToBlacklist(String itemID) { blacklistedGifts.add(itemID); }
}