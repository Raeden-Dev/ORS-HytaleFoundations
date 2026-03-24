package com.raeden.hytale.core.config.containers;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class MailConfig {
    @SerializedName("version")
    private String version;
    @SerializedName("max_inbox_size")
    private int maxInboxSize;
    @SerializedName("max_mail_lines")
    private int maxMailLines;
    @SerializedName("max_mail_per_day")
    private int maxMailPerDay;
    @SerializedName("allow_gifts")
    private boolean allowGifting;
    @SerializedName("max_gift_per_mail")
    private int maxGiftPerMail;
    @SerializedName("blacklisted_gifts")
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