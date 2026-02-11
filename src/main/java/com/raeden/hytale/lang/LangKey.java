package com.raeden.hytale.lang;

public enum LangKey {
    // General
    PLAYER_MSG_PREFIX("&f&l| "),
    NO_PERMISSION("&c&lYou don't have permission to run that command!"),
    FILE_NOT_FOUND("&c&l[ERROR] &r&cCould not find file &e{0}"),
    FILE_NOT_FOUND_W_LOC("&c&l[ERROR] Could not find file &r&e&l{0} &r&c&lat &r&e&l{1}"),
    CREATE_SUCCESS("&a&l[INFO] Created &r&e&l{0}"),
    CREATE_FAILURE("&c&l[ERROR] Failed to create &r&e&l{0}"),
    CREATE_DIRECTORY("&e&l[DIR] Created &r&a&l{0} &r&e&ldirectory"),
    CREATE_DIRECTORY_W_LOC("&e&l[DIR] Created &r&a&l{0} directory at &r&e&l{1}"),
    CREATE_DIRECTORY_FAIL("&c&l[DIR] Failed to create &r&e&l{0} &r&c&ldirectory"),
    CREATE_DIRECTORY_FAIL_W_LOC("&c&l[DIR] Failed to create &r&e&l{0} &r&c&lat &r&e&l{1}"),
    SAVE("&a&l[SAVE] Saved &r&e&l{0}"),
    SAVE_W_LOC("&a&l[SAVE] Saved &r&e&l{0} &r&a&lat &r&e&l{1}"),
    SAVE_FAILURE("&c&l[SAVE] Failed to save &r&e&l{0}"),
    SAVE_FAILURE_W_LOC("&c&l[SAVE] Failed to save &r&e&l{0} &r&c&lat &r&e&l{1}"),
    CHECK_FAILURE("&c&l[INFO] Failed to check &r&e&l{0}"),
    LOAD_FILE("&e&l[LOAD] Loaded &r&a&l{0}"),
    LOAD_FILE_W_TYPE("&e&l[LOAD] Loaded &r&a&l{0} {1}"),
    LOAD_FAILURE("&c&l[LOAD] Failed to load &r&e&l{0}"),
    LOAD_FAILURE_W_LOC("&c&l[LOAD] Failed to load &r&e&l{0} &r&c&lat &r&e&l{1}"),
    READ_FAILURE("&c&l[READ] Failed to read &r&e&l{0}"),
    READ_FAILURE_W_LOC("&c&l[READ] Failed to read &r&e&l{0} &r&c&lat &r&e&l{1}"),
    STOP_SUCCESS("&e&l[STOP] Stopped &r&f&l{0}"),
    STOP_FAILURE("&c&l[STOP] Failed to stop &r&e&l{0}"),
    GET_PLAYER_FAIL("&c&l[ERROR] Failed to get player &r&e&l{0}"),
    USERNAME_FIND_FAILURE("&c&l[ERROR] Failed to find player with username &r&e&l{0}"),
    USER_ONLINE_CHECK_FAILURE("&c&l[ERROR] Failed to check if player &r&e&l{0} &r&c&lis online!"),
    INV_SLOT_CHECK_ERROR("&c&l[ERROR] Failed to check inventory slots for player &r&e&l{0}"),
    INVALID_COLOR_FORMAT("&c&l[ERROR] Skipping invalid color for chat formating. &r&e&lCode: {0} | Hex: {1}"),
    INCORRECT_TIME_FORMAT("&c&lTime format is incorrect! Correct format: &r&e&l(d|h|m|s eg. 1d8h5m33s)"),
    PLAYER_NEVER_JOINED("&c&lPlayer with username &r&e&l{0} &c&lwas not found!"),
    MISMATCH_FOUND("&c&lMismatch found for &r&e&l{0}"),

    // Chat
    PRIVATE_MSG_FORMAT_SENDER("&e&lYou &r&f&l» &r&e&l{0}&r&f&l: &r&f{1}"),
    PRIVATE_MSG_FORMAT_RECEIVER("&e&l{0} &r&f&l» &r&e&lYou&r&f&l: &r&f{1}"),
    PRIVATE_MSG_FORMAT_ADMIN("&c&l[PEEKING] &r&e&l{0} &r&f&l» &r&e&l{1}&r&f&l: &r&a{2}"),
    PLAYER_BLOCKED_SENDER("&c&lCannot send message to &r&e&l{0}, &r&c&lthey have blocked you!"),
    RECEIVER_NOT_ONLINE("&c&l{0} &r&e&lis not online!"),
    RECEIVER_IS_MUTED("&e&l{0} &r&c&lis muted, they cannot reply to your message!"),
    PLAYER_SELF_MSG("&e&lYou cannot send a private message to yourself!"),
    PLAYER_NO_RECEIVER("&c&lYou have no one to reply to! &r&f&l/msg [player] first."),
    CHAT_LOG_EXPORTED("&a&lSuccessfully exported chat log &r&e&l({0}) &r&a&lat &r&e&l{1}"),
    CHAT_LOG_EXPORT_FAIL("&c&lFailed to export chat log &r&e&l{0}"),
    BLOCKED_PLAYER("&c&lYou've blocked &r&e&l{0}&r&c&l!"),
    BLOCKED_PLAYER_ALREADY("&e&l{0} &r&c&lis already blocked by you!"),
    UNBLOCKED_PLAYER("&a&lYou've unblocked &r&e&l{0}&r&c&l!"),
    NOT_BLOCKED_PLAYER("&e&l{0} &r&a&lis not in your blocklist!"),

    // Mute Command
    PLAYER_MUTED("&c&lYou cannot send message because you are muted!"),
    PLAYER_MUTED_W_TIME("&c&lYou cannot send message because you are muted! &r&e&l(Time Remaining: {0})"),
    PLAYER_MUTED_PM("&c&lYou cannot send message to &r&e&l{0} &r&c&lbecause you are muted!"),
    PLAYER_MUTED_PM_W_TIME("&c&lYou cannot send message to &r&e&l{0} &r&c&lbecause you are muted! &r&e&l(Time Remaining: {1})"),
    MUTE_PLAYER("&c&lMuted &r&e&l{0} &r&c&lfor &r&e&l{1}"),
    PLAYER_MUTE_MSG("&c&lYou have been muted by &r&e&l{0} &r&c&lfor &r&e&l{1}."),
    UNMUTED_PLAYER("&e&l{0} &r&a&lhas been unmuted!"),
    PLAYER_UNMUTE_MSG("&a&lYou have been unmuted by &r&e&l{0}&r&a&l!"),
    MUTE_DURATION_INCREASE("&e&l{0} &r&c&lwas already muted, increased their duration from &r&e&l{1}  &r&c&lto &r&e&l{2}."),
    PLAYER_MSG_MUTE_DURATION_INCREASE("&c&lYour mute duration was increased to &r&e&l{0} &r&c&lby &r&e&l{1}."),
    NOT_MUTED("{0} is not muted!"),

    // Mail System
    UNREAD_MAILS("&f&lYou have &r&e&l{0} &r&f&lunread mails."),
    CHECK_MAILBOX("&f&lCheck your mailbox to read mails."),

    // Utility
    PLAYER_PLAYTIME("&a&l» Total playtime of &r&e&l{0}&r&a&l: &r&e&l{1}"),

    // Plugin Actions
    PA_CLEAR_CHAT("&c&lWhole chat messages were purged."),
    PA_CREATED_SCHEDULER("&a&lCreated scheduler &r&e&l{0} &r&a&lcalled by &r&e&l{1}"),
    PA_CLEAR_CHAT_AMOUNT("&c&lCleared &e&l{0} &r&c&lprevious chat messages"),
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
