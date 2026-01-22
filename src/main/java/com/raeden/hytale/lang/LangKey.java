package com.raeden.hytale.lang;

import com.raeden.hytale.utils.colors;

public enum LangKey {
    GET_PLAYER_FAIL("Failed to get player {0}"),
    CREATE_DIRECTORY("Created {0} directory"),
    CREATE_DIRECTORY_W_LOC("Created {0} directory at {1}"),
    CREATE_DIRECTORY_FAIL("Failed to create {0} directory"),
    CREATE_DIRECTORY_FAIL_W_LOC("Failed to create {0} at {1}"),
    SAVE("Saved {0}"),
    SAVE_W_LOC("Saved {0} at {1}"),
    SAVE_FAILURE("Failed to save {0}"),
    SAVE_FAILURE_W_LOC("Failed to save {0} at {1}"),
    LOAD_FILE("Loaded {0}"),
    LOAD_FILE_W_TYPE("Loaded {0} {1}"),
    LOAD_FAILURE("Failed to load {0}"),
    LOAD_FAILURE_W_LOC("Failed to load {0} at {1}"),
    READ_FAILURE("Failed to read {0}"),
    READ_FAILURE_W_LOC("Failed to read {0} at {1}"),
    USERNAME_FIND_FAILURE("Failed to find player with username {0}"),
    USER_ONLINE_CHECK_FAILURE("Failed to check if player {0} is online!"),
    INV_SLOT_CHECK_ERROR("Failed to check inventory slots for player {0}"),

    // Chat
    PRIVATE_MSG_FORMAT_SENDER("You -> {0}: {1}"),
    PRIVATE_MSG_FORMAT_RECEIVER("{0} -> You: {1}"),
    PRIVATE_MSG_FORMAT_ADMIN("[PVT MSG] {0} -> {1}: {2}"),
    PLAYER_BLOCKED_SENDER("Cannot send message to {0}, they have blocked you!"),
    PLAYER_MUTED("You cannot send message because you are muted!", colors.MC_RED, true),
    PLAYER_MUTED_W_TIME("You cannot send message because you are muted! (Time Remaining: {0})", colors.MC_RED, true),
    PLAYER_MUTED_PM("You cannot send message to {0} because you are muted!", colors.MC_RED, true),
    PLAYER_MUTED_PM_W_TIME("You cannot send message to {0} because you are muted! (Time Remaining: {1})", colors.MC_RED, true),
    RECEIVER_NOT_ONLINE("{0} is not online!", colors.MC_RED, true),
    RECEIVER_IS_MUTED("{0} is muted, they cannot reply to your message!", colors.MC_RED, true),
    PLAYER_SELF_MSG("You cannot send a private message to yourself!", colors.MC_RED, true),
    PLAYER_NO_RECEIVER("You have no one to reply to! /msg [player] first.", colors.MC_RED, true),
    CHAT_LOG_EXPORTED("Successfully exported chat log ({0}) at {1}"),
    CHAT_LOG_EXPORT_FAIL("Failed to export chat log {0}"),

    // Plugin Actions
    PA_CLEAR_CHAT("Whole chat messages were purged."),
    PA_CLEAR_CHAT_AMOUNT("{0} previous chat messages"),
    PA_CHAT_LOG_EXPORTED("[{0}] » Exported Chat Log."),
    PA_CHAT_LOG_EXPORT_FAIL("[{0}] » Failed to export chat Log."),
    PA_CLEAR_ACTIVE_MESSENGER_CACHE("[{0}] » Cleared active private messenger cache...");


    private final colors startColor;
    private final colors endColor;
    private final boolean isBold;
    private final String defaultMessage;

    LangKey(String defaultMessage) {
        this.defaultMessage = defaultMessage;
        this.startColor = colors.WHITE;
        this.endColor = null;
        this.isBold = false;
    }

    LangKey(String defaultMessage, boolean isBold) {
        this.defaultMessage = defaultMessage;
        this.startColor = colors.WHITE;
        this.endColor = null;
        this.isBold = isBold;
    }

    LangKey(String defaultMessage, colors solidColor, boolean isBold) {
        this.defaultMessage = defaultMessage;
        this.startColor = solidColor;
        this.isBold = isBold;
        this.endColor = null;
    }

    LangKey(String defaultMessage, colors startColor, colors endColor, boolean isBold) {
        this.defaultMessage = defaultMessage;
        this.startColor = startColor;
        this.endColor = endColor;
        this.isBold = isBold;
    }

    LangKey(String defaultMessage, colors solidColor) {
        this(defaultMessage, solidColor, false);
    }

    LangKey(String defaultMessage, colors startColor, colors endColor) {
        this(defaultMessage, startColor, endColor, false);
    }

    public String getDefaultMessage() {return defaultMessage;}
    public String getKey() { return name().toLowerCase();}

    public colors getStartColor() {return startColor;}
    public colors getEndColor() {return endColor;}
    public boolean isGradient() {return endColor != null;}
    public boolean isBold() {return isBold;}
}
