package com.raeden.hytale.lang;

public enum LangKey {
    // General
    NO_PERMISSION("You don't have permission to run that command!"),
    CREATE_SUCCESS("[INFO] Created {0}"),
    CREATE_FAILURE("[ERROR] Failed to create {0}"),
    CREATE_DIRECTORY("[DIR] Created {0} directory"),
    CREATE_DIRECTORY_W_LOC("[DIR] Created {0} directory at {1}"),
    CREATE_DIRECTORY_FAIL("[DIR] Failed to create {0} directory"),
    CREATE_DIRECTORY_FAIL_W_LOC("[DIR] Failed to create {0} at {1}"),
    SAVE("[SAVE] Saved {0}"),
    SAVE_W_LOC("[SAVE] Saved {0} at {1}"),
    SAVE_FAILURE("[SAVE] Failed to save {0}"),
    SAVE_FAILURE_W_LOC("[SAVE] Failed to save {0} at {1}"),
    LOAD_FILE("[LOAD] Loaded {0}"),
    LOAD_FILE_W_TYPE("[LOAD] Loaded {0} {1}"),
    LOAD_FAILURE("[LOAD] Failed to load {0}"),
    LOAD_FAILURE_W_LOC("[LOAD] Failed to load {0} at {1}"),
    READ_FAILURE("[READ] Failed to read {0}"),
    READ_FAILURE_W_LOC("[READ] Failed to read {0} at {1}"),
    STOP_SUCCESS("[STOP] Stopped {0}"),
    STOP_FAILURE("[STOP] Failed to stop {0}"),
    GET_PLAYER_FAIL("[ERROR] Failed to get player {0}"),
    USERNAME_FIND_FAILURE("[ERROR] Failed to find player with username {0}"),
    USER_ONLINE_CHECK_FAILURE("[ERROR] Failed to check if player {0} is online!"),
    INV_SLOT_CHECK_ERROR("[ERROR] Failed to check inventory slots for player {0}"),
    INCORRECT_TIME_FORMAT("Time format is incorrect! Correct format: (d|h|m|s eg. 1d8h5m33s)"),
    PLAYER_NEVER_JOINED("Player with username {0} was not found!"),
    MISMATCH_FOUND("Mismatch found for {0}"),

    // Chat
    PRIVATE_MSG_FORMAT_SENDER("You » {0}: {1}"),
    PRIVATE_MSG_FORMAT_RECEIVER("{0} » You: {1}"),
    PRIVATE_MSG_FORMAT_ADMIN("[PEEKING] {0} » {1}: {2}"),
    PLAYER_BLOCKED_SENDER("Cannot send message to {0}, they have blocked you!"),
    RECEIVER_NOT_ONLINE("{0} is not online!"),
    RECEIVER_IS_MUTED("{0} is muted, they cannot reply to your message!"),
    PLAYER_SELF_MSG("You cannot send a private message to yourself!"),
    PLAYER_NO_RECEIVER("You have no one to reply to! /msg [player] first."),
    CHAT_LOG_EXPORTED("Successfully exported chat log ({0}) at {1}"),
    CHAT_LOG_EXPORT_FAIL("Failed to export chat log {0}"),
    BLOCKED_PLAYER("You've blocked {0}!"),
    BLOCKED_PLAYER_ALREADY("{0} is already blocked by you!"),
    UNBLOCKED_PLAYER("You've unblocked {0}!"),
    NOT_BLOCKED_PLAYER("{0} is not in your blocklist!"),

    // Mute Command
    PLAYER_MUTED("You cannot send message because you are muted!"),
    PLAYER_MUTED_W_TIME("You cannot send message because you are muted! (Time Remaining: {0})"),
    PLAYER_MUTED_PM("You cannot send message to {0} because you are muted!"),
    PLAYER_MUTED_PM_W_TIME("You cannot send message to {0} because you are muted! (Time Remaining: {1})"),
    MUTE_PLAYER("Muted {0} for {1}"),
    PLAYER_MUTE_MSG("You have been muted by {0} for {1}."),
    UNMUTED_PLAYER("{0} has been unmuted!"),
    PLAYER_UNMUTE_MSG("You have been unmuted by {0}!"),
    MUTE_DURATION_INCREASE("{0} was already muted, increased their duration from {1} to {2}."),
    PLAYER_MSG_MUTE_DURATION_INCREASE("Your mute duration was increased to {0} by {1}."),
    NOT_MUTED("{0} is not muted!"),

    // Mail System
    UNREAD_MAILS("You have {0} unread mails."),
    CHECK_MAILBOX("Check your mailbox to read mails."),

    // Utility
    PLAYER_PLAYTIME("» Total playtime of {0}: {1}"),

    // Plugin Actions
    PA_CLEAR_CHAT("Whole chat messages were purged."),
    PA_CREATED_SCHEDULER("Created scheduler {0} called by {1}"),
    PA_CLEAR_CHAT_AMOUNT("{0} previous chat messages"),
    PA_CHAT_LOG_EXPORTED("[{0}] » Exported Chat Log."),
    PA_CHAT_LOG_EXPORT_FAIL("[{0}] » Failed to export chat Log."),
    PA_CLEAR_ACTIVE_MESSENGER_CACHE("[{0}] » Cleared active private messenger cache...");

    private final String defaultMessage;

    LangKey(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public String getKey() {
        return name().toLowerCase();
    }
}
