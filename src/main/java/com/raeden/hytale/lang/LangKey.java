package com.raeden.hytale.lang;

public enum LangKey {
    // Core System
    CHAT_MSG_PREFIX         ("system.prefix",                       "&l|"),
    NO_PERMISSION           ("system.no_permission",                "You don't have permission to run that command!"),
    MISMATCH_FOUND          ("system.error.mismatch",               "Mismatch found for {0}"),
    INVALID_TIME_FORMAT     ("system.error.invalid_time",           "Time format is incorrect! Correct format: (d|h|m|s eg. 1d8h5m33s)"),
    INVALID_COLOR_FORMAT    ("system.error.invalid_color",          "Skipping invalid color format. Code: {0} | Hex: {1}"),
    SCHEDULER_CREATED       ("system.scheduler.create",             "Created scheduler {0} called by {1}"),
    NULL_POINTER            ("system.error.null",                   "[ERROR] Unexpected null error at {0}"),
    PLUGIN_RELOAD_SUCCESS   ("system.plugin.reload_success",        "&a&lSuccessfully reloaded {0}!"),
    PLUGIN_RELOAD_FAILURE   ("system.plugin.reload_failure",        "&c&lFailed to reload {0}!"),
    GENERAL_LIST            ("message.info.list",                   "&a&lAvailable {0}:"),
    GENERAL_LIST_ITEM       ("message.info.list_item",              "&7&l  - &r{0}"),

    FILE_NOT_FOUND          ("system.file.not_found",               "[ERROR] Could not find file {0}"),
    FILE_NOT_FOUND_LOC      ("system.file.not_found_loc",           "[ERROR] Could not find file {0} at {1}"),

    DIR_CREATE_SUCCESS_LOC  ("system.dir.create.success_loc",       "[DIR] Created {0} directory at {1}"),
    DIR_CREATE_FAIL_LOC     ("system.dir.create.fail_loc",          "[DIR] Failed to create {0} at {1}"),

    CREATE_SUCCESS          ("system.data.create.success",          "[INFO] Created {0}"),
    CREATE_FAILURE          ("system.data.create.fail",             "[ERROR] Failed to create {0}"),
    SAVE_SUCCESS_LOC        ("system.data.save.success_loc",        "[SAVE] Saved {0} at {1}"),
    SAVE_FAILURE            ("system.data.save.fail",               "[SAVE] Failed to save {0}"),
    SAVE_FAILURE_LOC        ("system.data.save.fail_loc",           "[SAVE] Failed to save {0} at {1}"),
    LOAD_SUCCESS            ("system.data.load.success",            "[LOAD] Loaded {0}"),
    LOAD_FAILURE_LOC        ("system.data.load.fail_loc",           "[LOAD] Failed to load {0} at {1}"),
    READ_FAILURE_LOC        ("system.data.read.fail_loc",           "[READ] Failed to read {0} at {1}"),
    CHECK_FAILURE           ("system.data.check.fail",              "[INFO] Failed to check {0}"),

    DEBUG_MODE              ("system.process.debug",                                "&a&lDebug Mode: &r&e&l{0}"),
    PENDING_UPDATE          ("system.data.update.pending",          "[UPDATE] An update is available for {0}"),
    UPDATE_SUCCESS          ("system.data.update.success",          "[UPDATE] Updated {0}"),
    UPDATE_SUCCESS_VERSION  ("system.data.update.success_version",  "[UPDATE] Updated {0} from {1} to {2}"),
    UPDATE_FAILURE          ("system.data.update.failure",          "[UPDATE] Failed to update {0}"),

    STOP_SUCCESS            ("system.process.stop.success",         "[STOP] Stopped {0}"),
    STOP_FAILURE            ("system.process.stop.fail",            "[STOP] Failed to stop {0}"),
    PLAYER_GET_FAIL         ("system.player.get_fail",              "[ERROR] Failed to get player {0}"),
    PLAYER_NOT_FOUND        ("system.player.not_found",             "[ERROR] Player with username {0} was not found!"),
    PLAYER_NOT_FOUND_MSG    ("system.player.not_found_msg",         "&c&lPlayer with username &r&e&l{0} &r&c&lwas not found!"),
    PLAYER_ONLINE_CHECK_FAIL("system.player.online_check_fail",     "[ERROR] Failed to check if player {0} is online!"),
    PLAYER_INV_CHECK_FAIL   ("system.player.inv_check_fail",        "[ERROR] Failed to check inventory slots for player {0}"),

    CHAT_CLEAR_ALL          ("system.chat.clear.all",               "[CHAT] Whole chat messages were purged."),
    CHAT_CLEAR_AMOUNT       ("system.chat.clear.amount",            "[CHAT] Cleared {0} previous chat messages"),
    INVALID_CHAT_FORMAT     ("system.chat.invalid_format",          "[CHAT] Chat format is invalid! To ensure correct format, string must contain: {player}, {prefix}, {suffix} and {message}."),

    // Logs
    LOG_CHAT_EXPORT_SUCCESS ("log.chat.export.success",             "[{0}] >> Exported Chat Log."),
    LOG_CHAT_EXPORT_FAIL    ("log.chat.export.fail",                "[{0}] >> Failed to export chat Log."),
    LOG_CACHE_CLEAR_MSG     ("log.cache.clear.messenger",           "[{0}] >> Cleared active private messenger cache..."),

    // Chat format
    CHAT_FORMAT             ("chat.format.default",                 "{prefix} {player} {suffix} » {message}"),
    CHANNEL_CHAT_FORMAT     ("chat.format.has_channel",             "{channel}{prefix}{player}{suffix} » {message}"),

    // Private Messaging
    PM_FORMAT_SENDER        ("chat.pm.format.sender",               "&e&lYou &r&f&l» &r&e&l{0}&r&f&l: &r&f{1}"),
    PM_FORMAT_RECEIVER      ("chat.pm.format.receiver",             "&e&l{0} &r&f&l» &r&e&lYou&r&f&l: &r&f{1}"),
    PM_FORMAT_SPY           ("chat.pm.format.spy",                  "&c&l[PEEKING] &r&e&l{0} &r&f&l» &r&e&l{1}&r&f&l: &r&a{2}"),

    PM_ERROR_SELF           ("chat.pm.error.self",                  "&e&lYou cannot send a private message to yourself!"),
    PM_ERROR_NO_REPLY       ("chat.pm.error.no_reply",              "&c&lYou have no one to reply to! &r&f&l/msg [player] first."),
    PM_ERROR_OFFLINE        ("chat.pm.error.offline",               "&c&l{0} &r&e&lis not online!"),
    PM_ERROR_TARGET_MUTED   ("chat.pm.error.target_muted",          "&e&l{0} &r&c&lis muted, they cannot reply to your message!"),
    PM_ERROR_SENDER_BLOCKED ("chat.pm.error.sender_blocked",        "&c&lCannot send message to &r&e&l{0}, &r&c&lthey have blocked you!"),

    // Blocking, Muting & Ignoring
    BLOCK_SUCCESS           ("chat.block.success",                  "&c&lYou've blocked &r&e&l{0}"),
    BLOCK_ALREADY           ("chat.block.already",                  "&e&l{0} &r&c&lis already blocked by you"),
    UNBLOCK_SUCCESS         ("chat.block.unblock_success",          "&a&lYou've unblocked &r&e&l{0}"),
    UNBLOCK_NOT_FOUND       ("chat.block.not_blocked",              "&e&l{0} &r&a&lis not in your blocklist"),

    MUTE_ERROR_CHAT         ("moderation.mute.error.chat",          "&c&lYou cannot send message because you are muted!"),
    MUTE_ERROR_CHAT_TIME    ("moderation.mute.error.chat_time",     "&c&lYou cannot send message because you are muted! &r&e&l(Time Remaining: {0})"),
    MUTE_ERROR_PM           ("moderation.mute.error.pm",            "&c&lYou cannot send message to &r&e&l{0} &r&c&lbecause you are muted!"),
    MUTE_ERROR_PM_TIME      ("moderation.mute.error.pm_time",       "&c&lYou cannot send message to &r&e&l{0} &r&c&lbecause you are muted! &r&e&l(Time Remaining: {1})"),
    MUTE_ACTION_SUCCESS     ("moderation.mute.action.success",      "&c&lMuted &r&e&l{0} &r&c&lfor &r&e&l{1}"),
    MUTE_ACTION_INCREASED   ("moderation.mute.action.increased",    "&e&l{0} &r&c&lwas already muted, increased their duration from &r&e&l{1}  &r&c&lto &r&e&l{2}."),
    UNMUTE_ACTION_SUCCESS   ("moderation.mute.action.unmuted",      "&e&l{0} &r&a&lhas been unmuted"),
    UNMUTE_ERROR_NOT_MUTED  ("moderation.mute.error.not_muted",     "&e&l{0} &r&c&lis not muted!"),
    MUTE_NOTIFY_ACTIVE      ("moderation.mute.notify.active",       "&c&lYou have been muted by &r&e&l{0} &r&c&lfor &r&e&l{1}."),
    MUTE_NOTIFY_INCREASED   ("moderation.mute.notify.increased",    "&c&lYour mute duration was increased to &r&e&l{0} &r&c&lby &r&e&l{1}."),
    UNMUTE_NOTIFY_ACTIVE    ("moderation.mute.notify.unmuted",      "&a&lYou have been unmuted by &r&e&l{0}"),

    MAIL_NOTIFY_UNREAD      ("mail.notify.unread",                  "&f&lYou have &r&e&l{0} &r&f&lunread mails."),
    MAIL_NOTIFY_CHECK       ("mail.notify.check",                   "&f&lCheck your mailbox to read mails."),
    MAIL_SEND_SUCCESS       ("mail.send.success",                   "&a&lMail sent to &r&e&l{0} &r&a&lsuccessfully!"),
    MAIL_SEND_FAILURE       ("mail.send.fail",                      "&c&lFailed to send mail to &r&e&l{0}&r&c&l!"),
    MAIL_MISSING_FIELD      ("mail.send.empty_field",               "&c&lPlease fill out all fields to send the mail."),

    NICKNAME_DISABLED       ("nickname.disabled",                   "&c&lChanging  &r&e&lnickname &r&c&lis disabled!"),
    NICKNAME_SET            ("nickname.set",                        "&a&lChanged nickname of &r&e&l{0} to &r{1}"),
    NICKNAME_CLEARED        ("nickname.clear",                      "&a&lCleared nickname of &r&e&l{0}}"),
    NICKNAME_LENGTH         ("nickname.error.length",               "&c&Nickname must have atleast &r&e&l3 &r&c&lcharacters!"),
    NICKNAME_NO_SPECIAL_CHAR("nickname.error.no_special_characters","&c&Nickname must not contain any &r&e&lspecial &r&c&lcharacter!"),
    NICKNAME_EXISTS         ("nickname.error.exists",               "&c&Someone already has the &r&e&lsame &r&c&lnickname!"),
    NICKNAME_INAPPROPRIATE  ("nickname.error.inappropriate",        "&c&Please pick an &r&e&lappropriate &r&c&lnickname!"),
    NICKNAME_IMPERSONATION  ("nickname.error.impersonation",        "&c&Please refrain from &r&e&limpersonating &r&c&la staff through nicknames!"),

    PREFIX_DISABLED         ("affix.prefix.disabled",               "&c&lAdding  &r&e&lprefix &r&c&lis disabled!"),
    SUFFIX_DISABLED         ("affix.suffix.disabled",               "&c&lAdding  &r&e&lsuffix &r&c&lis disabled!"),
    AFFIX_MAX               ("affix.add.max",                       "&c&lCould not add {0} to &r&e&l{1}&r&c&l! Max limit of &r&e&l{2} &r&c&lis reached!"),
    AFFIX_ADD_SUCCESS       ("affix.add.success",                   "&a&lAdded affix {0} &r&a&lto &r&e&l{1}&r&a&l!"),
    AFFIX_ADD_FAIL          ("affix.add.failure",                   "&c&lCould not add affix to &r&e&l{1}&r&c&l!"),
    AFFIX_REMOVE_SUCCESS    ("affix.remove.success",                "&a&lRemoved affix &r{0} &r&a&lto &r&e&l{1}&r&a&l!"),
    AFFIX_REMOVE_FAIL       ("affix.remove.failure",                "&c&lCould not remove affix to &r&e&l{1}&r&c&l!"),
    AFFIX_CREATE            ("affix.create",                        "&a&lCreated new affix &r{0}"),
    AFFIX_DELETE            ("affix.delete",                        "&c&lDeleted affix &r{0}"),
    AFFIX_NOT_FOUND         ("affix.not_found",                     "&c&lCould not find affix with ID &r&e&l{0}"),
    AFFIX_UPDATE            ("affix.updated",                       "&a&lAffix was updated from &r&e&l{0} &r&a&lto &r&e&l{1}"),

    STATS_PLAYTIME          ("utility.stats.playtime",              "&a&lTotal playtime of &r&e&l{0}&r&a&l: &r&f&l{1}");

    private final String key;
    private final String defaultMessage;

    LangKey(String key, String defaultMessage) {
        this.key = key;
        this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public String getKey() {
        return key;
    }
}
