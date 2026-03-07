package com.raeden.hytale.lang;

public enum LangKey {
    // Core System (PLayer Message)
    CHAT_MSG_PREFIX         ("system.prefix",                       "&l|&r"),
    CHAT_MSG_PREFIX_ADMIN   ("system.prefix_admin",                 "&7&l[&r&b&a&lHytaleFoundations&r&7&l]&r"),
    MISMATCH_FOUND          ("system.error.mismatch",               "&c&lMismatch found for {0}"),
    INVALID_TIME_FORMAT     ("system.error.invalid_time",           "Time format is incorrect! Correct format: (d|h|m|s eg. 1d8h5m33s)"),
    INVALID_COLOR_FORMAT    ("system.error.invalid_color",          "Skipping invalid color format. Code: {0} | Hex: {1}"),
    SCHEDULER_CREATED       ("system.scheduler.create",             "Created scheduler {0} called by {1}"),
    RELOAD_SUCCESS          ("system.plugin.reload_success",        "&a&lSuccessfully reloaded {0}!"),
    RELOAD_FAILURE          ("system.plugin.reload_failure",        "&c&lFailed to reload {0}!"),
    NOTHING_FOUND           ("message.info.nothing_found",          "&c&lCould not find any {0}. &r&7&l[Reason: {1}]"),
    COMMA_SEPARATED         ("message.info.comma_separated",        "&c&lPlease split the {0} with commas. &r&7&l(eg. item1, item2...)"),
    LIST_CONTEXT            ("message.info.list",                   "&a&lAvailable {0}:"),
    LIST_ITEM               ("message.info.list_item",              "&7&l  - &r{0}"),
    LIST_NUMBERED_ITEM      ("message.info.number_item",            "&7&l   &r&e&l{0}. &r{1}"),

    // Core System (Logging)
    NULL_POINTER            ("system.error.null",                   "[ERROR] Unexpected null error at {0}"),
    FILE_NOT_FOUND          ("system.file.not_found",               "[ERROR] Could not find file {0}"),
    FILE_NOT_FOUND_LOC      ("system.file.not_found_loc",           "[ERROR] Could not find file {0} at {1}"),

    DIR_CREATE_SUCCESS_LOC  ("system.dir.create.success_loc",       "[DIR] Created {0} directory at {1}"),
    DIR_CREATE_FAIL_LOC     ("system.dir.create.fail_loc",          "[DIR] Failed to create {0} at {1}"),

    CREATE_SUCCESS          ("system.data.create.success",          "[INFO] Created {0}"),
    CREATE_FAILURE          ("system.data.create.fail",             "[ERROR] Failed to create {0}"),
    SAVE_SUCCESS            ("system.data.save.success",            "[SAVE] Saved {0}"),
    SAVE_SUCCESS_LOC        ("system.data.save.success_loc",        "[SAVE] Saved {0} at {1}"),
    SAVE_FAILURE            ("system.data.save.fail",               "[SAVE] Failed to save {0}"),
    SAVE_FAILURE_LOC        ("system.data.save.fail_loc",           "[SAVE] Failed to save {0} at {1}"),
    SAVE_PD_SUCCESS         ("system.data.player.save.success",     "[SAVE] Saved data of player {0}"),
    RELOAD_PLAYER_DATA      ("system.data.player.reload",           "[RELOAD] Reloaded data of player {0}"),
    LOAD_SUCCESS            ("system.data.load.success",            "[LOAD] Loaded {0}"),
    LOAD_FAILURE_LOC        ("system.data.load.fail_loc",           "[LOAD] Failed to load {0} at {1}"),
    READ_FAILURE_LOC        ("system.data.read.fail_loc",           "[READ] Failed to read {0} at {1}"),
    CHECK_FAILURE           ("system.data.check.fail",              "[INFO] Failed to check {0}"),

    DEBUG_MODE              ("system.process.debug",                "&a&lDebug Mode: &r&e&l{0}"),
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

    // Permissions
    PERMISSION_GROUP_ADD    ("permission.group.add",                "&a&lAdded permission group &r&e&l{0} &r&a&lfor &r&e&l{1}"),
    PERMISSION_GROUP_REMOVE ("permission.group.remove",             "&a&lRemoved permission group &r&e&l{0} &r&a&lfrom &r&e&l{1}"),
    PERMISSION_ADD          ("permission.add",                      "&a&lAdded permission &r&e&l{0} &r&a&lto &r&e&l{1}"),
    PERMISSION_REMOVE       ("permission.remove",                   "&a&lRemoved permission &r&e&l{0} &r&a&lfrom &r&e&l{1}"),
    PERMISSION_GROUP_CREATE ("permission.group.create",             "&a&lCreated permission group &r&e&l{0}"),
    PERMISSION_GROUP_DELETE ("permission.group.delete",             "&c&lDeleted permission group &r&e&l{0}"),
    PERMISSION_GROUP_NF     ("permission.group.not_found",          "&c&lPermission group &r&e&l{0} &r&c&lwas not found!"),
    PERMISSION_NOT_FOUND    ("permission.not_found",                "&c&lPermission &r&e&l{0} &r&c&lwas not found!"),
    PLAYER_PERM_LIST        ("permission.player.perm_list",         "&a&lPermission list of &r&e&l{0}&r&a&l:"),
    PLAYER_PERM_GROUP_LIST  ("permission.player.perm_group_list",   "&a&lPermission group list of &r&e&l{0}&r&a&l:"),

    // Logs
    LOG_CHAT_EXPORT_SUCCESS ("log.chat.export.success",             "[{0}] >> Exported Chat Log."),
    LOG_CHAT_EXPORT_FAIL    ("log.chat.export.fail",                "[{0}] >> Failed to export chat Log."),
    LOG_CACHE_CLEAR_MSG     ("log.cache.clear.messenger",           "[{0}] >> Cleared active private messenger cache..."),

    // Chat format
    CHAT_FORMAT             ("chat.format.default",                 "{prefix} {player} {suffix} » {message}"),
    CHANNEL_CHAT_FORMAT     ("chat.format.has_channel",             "{channel} {prefix} {player} {suffix} » {message}"),

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
    BLOCK_SUCCESS           ("chat.block.success",                  "&a&lYou've blocked &r&e&l{0}"),
    BLOCK_ALREADY           ("chat.block.already",                  "&e&l{0} &r&c&lis already blocked by you"),
    UNBLOCK_SUCCESS         ("chat.block.unblock_success",          "&a&lYou've unblocked &r&e&l{0}"),
    UNBLOCK_NOT_FOUND       ("chat.block.not_blocked",              "&e&l{0} &r&a&lis not in your blocklist"),

    MUTE_ERROR_CHAT         ("moderation.mute.error.chat",          "&c&lYou cannot send message because you are muted!"),
    MUTE_ERROR_CHAT_TIME    ("moderation.mute.error.chat_time",     "&c&lYou cannot send message because you are muted! &r&e&l(Time Remaining: {0})"),
    MUTE_ERROR_PM           ("moderation.mute.error.pm",            "&c&lYou cannot send message to &r&e&l{0} &r&c&lbecause you are muted!"),
    MUTE_ERROR_PM_TIME      ("moderation.mute.error.pm_time",       "&c&lYou cannot send message to &r&e&l{0} &r&c&lbecause you are muted! &r&e&l(Time Remaining: {1})"),
    MUTE_ACTION_SUCCESS     ("moderation.mute.action.success",      "&c&lMuted &r&e&l{0} &r&c&lfor &r&e&l{1}"),
    MUTE_ACTION_INCREASED   ("moderation.mute.action.increased",    "&e&l{0} &r&c&lwas already muted, increased their duration from &r&e&l{1} &r&c&lto &r&e&l{2}."),
    UNMUTE_ACTION_SUCCESS   ("moderation.mute.action.unmuted",      "&e&l{0} &r&a&lhas been unmuted"),
    UNMUTE_ERROR_NOT_MUTED  ("moderation.mute.error.not_muted",     "&e&l{0} &r&c&lis not muted!"),
    MUTE_NOTIFY_ACTIVE      ("moderation.mute.notify.active",       "&c&lYou have been muted by &r&e&l{0} &r&c&lfor &r&e&l{1}."),
    MUTE_NOTIFY_INCREASED   ("moderation.mute.notify.increased",    "&c&lYour mute duration was increased to &r&e&l{0} &r&c&lby &r&e&l{1}."),
    UNMUTE_NOTIFY_ACTIVE    ("moderation.mute.notify.unmuted",      "&a&lYou have been unmuted by &r&e&l{0}"),

    // Mail System
    MAIL_NOTIFY_UNREAD      ("mail.notify.unread",                  "&f&lYou have &r&e&l{0} &r&f&lunread mails."),
    MAIL_NOTIFY_CHECK       ("mail.notify.check",                   "&f&lCheck your mailbox to read mails."),
    MAIL_SEND_SUCCESS       ("mail.send.success",                   "&a&lMail sent to &r&e&l{0} &r&a&lsuccessfully!"),
    MAIL_SEND_FAILURE       ("mail.send.fail",                      "&c&lFailed to send mail to &r&e&l{0}&r&c&l!"),
    MAIL_MISSING_FIELD      ("mail.send.empty_field",               "&c&lPlease fill out all fields to send the mail."),

    // Nickname System
    NICKNAME_DISABLED       ("nickname.disabled",                   "&c&lChanging &r&e&lnickname &r&c&lis disabled!"),
    NICKNAME_SET            ("nickname.set",                        "&a&lChanged nickname of &r&e&l{0} to &r{1}"),
    NICKNAME_CLEARED        ("nickname.clear",                      "&a&lCleared nickname of &r&e&l{0}"),
    NICKNAME_LENGTH         ("nickname.error.length",               "&c&lNickname must have atleast &r&e&l3 &r&c&lcharacters!"),
    NICKNAME_NO_SPECIAL_CHAR("nickname.error.no_special_characters","&c&lNickname must not contain any &r&e&lspecial &r&c&lcharacter!"),
    NICKNAME_EXISTS         ("nickname.error.exists",               "&c&lSomeone already has the &r&e&lsame &r&c&lnickname!"),
    NICKNAME_INAPPROPRIATE  ("nickname.error.inappropriate",        "&c&lPlease pick an &r&e&lappropriate &r&c&lnickname!"),
    NICKNAME_IMPERSONATION  ("nickname.error.impersonation",        "&c&lPlease refrain from &r&e&limpersonating &r&c&la staff through nicknames!"),

    // Affix System
    PREFIX_DISABLED         ("affix.prefix.disabled",               "&c&lAdding &r&e&lprefix &r&c&lis disabled!"),
    SUFFIX_DISABLED         ("affix.suffix.disabled",               "&c&lAdding &r&e&lsuffix &r&c&lis disabled!"),
    AFFIX_MAX               ("affix.add.max",                       "&c&lCould not add {0} to &r&e&l{1}&r&c&l! Max limit of &r&e&l{2} &r&c&lis reached!"),
    AFFIX_ADD_SUCCESS       ("affix.add.success",                   "&a&lAdded affix {0} &r&a&lto &r&e&l{1}&r&a&l!"),
    AFFIX_ADD_FAIL          ("affix.add.failure",                   "&c&lCould not add affix to &r&e&l{1}&r&c&l!"),
    AFFIX_REMOVE_SUCCESS    ("affix.remove.success",                "&a&lRemoved affix &r{0} &r&a&lof &r&e&l{1}&r&a&l!"),
    AFFIX_REMOVE_FAIL       ("affix.remove.failure",                "&c&lCould not remove affix to &r&e&l{1}&r&c&l!"),
    AFFIX_REMOVE_ALL        ("affix.remove.all",                    "&a&lRemoved all {0} of &r&e&l{1}&r&a&l!"),
    AFFIX_REPLACE           ("affix.add.replace",                   "&a&lReplaced affix &r{0} &r&a&lwith &r&e&l{1} &r&a&lfor {2}. (Affix had least priority)"),
    AFFIX_CREATE            ("affix.create",                        "&a&lCreated new affix &r{0}"),
    AFFIX_DELETE            ("affix.delete",                        "&c&lDeleted affix &r{0}"),
    AFFIX_NOT_FOUND         ("affix.not_found",                     "&c&lCould not find affix with ID &r&e&l{0}"),
    AFFIX_ACTIVE            ("affix.add.active",                    "&e&l{0} &r&c&lalready has an active &r&e&l{1} &r&c&lcalled &r&e&l{2}"),
    AFFIX_INACTIVE          ("affix.remove.inactive",               "&e&l{0} &r&c&ldoes not have an active &r&e&l{1} &r&c&lcalled &r&e&l{2}"),
    AFFIX_UPDATE            ("affix.updated",                       "&a&lAffix was updated from &r&e&l{0} &r&a&lto &r&e&l{1}"),

    // Rank System
    RANK_NOT_SET            ("rank.set.false",                      "&c&l{0} doesn't have an active rank!"),
    RANK_SET_SUCCESS        ("rank.set.success",                    "&a&lSet rank &r{0} for &r&e&l{1}"),
    RANK_SET_FAIL           ("rank.set.fail",                       "&c&lFailed to set rank &r{0} for &r&e&l{1}"),
    RANK_PROMOTE            ("rank.promote",                        "&a&lPromoted &r&e&l{0} from &r{1} &r&a&l-> &r{2}"),
    RANK_PROMOTE_CAP        ("rank.promote.cap",                    "&c&lCould not promote &r&e&l{0}, &r&a&lmax rank is reached."),
    RANK_DEMOTE             ("rank.demote",                         "&a&lDemoted &r&e&l{0} from &r{1} &r&a&l-> &r{2}"),
    RANK_DEMOTE_CAP         ("rank.demote.cap",                     "&c&lCould not demote &r&e&l{0}, &r&a&llowest rank is reached."),
    RANK_INDEPENDENT        ("rank.independent",                    "&c&lTarget's rank is independent and devoid of next/previous ranks."),
    RANK_NOT_FOUND          ("rank.not_found",                      "&c&lRank with id &r&e&l{0} &r&c&lis not found!"),
    RANK_REMOVE             ("rank.remove",                         "&a&lRemoved rank &r{0}&r&a&l for &r&e&l{1}"),
    RANK_SWITCH_GROUP       ("rank.group.switch",                   "&a&lSwitched group of rank &r&e&l{0} &r&a&lfrom {1} to {2}."),
    RANK_GROUP_EXISTS       ("rank.group.exists",                   "&c&lRank group called &r&e&l{0} &r&c&lalready exists!"),
    RANK_GROUP_NOT_FOUND    ("rank.group.not_found",                "&c&lRank group with id &r&e&l{0} &r&c&lis not found!"),
    RANK_GROUP_CREATE       ("rank.group.create",                   "&a&lCreated rank group called &r&e&l{0}"),
    RANK_GROUP_DELETE       ("rank.group.delete",                   "&c&lDeleted rank group called &r&e&l{0}"),
    RANK_GROUP_IGNORE_RANK  ("rank.group.ignore_rank",              "&c&lRank with id &r&e&l{0}&r&c&l, doesn't exist, ignoring it."),
    RANK_GROUP_APPEND       ("rank.group.append",                   "&a&lAdded &r&e&l{0} &r&a&lto rank group &r&e&l{1}"),
    RANK_GROUP_REMOVE       ("rank.group.remove",                   "&a&lRemoved &r&e&l{0} &r&a&lfrom rank group &r&e&l{1}"),
    RANK_GROUP_REMOVE_NF    ("rank.group.remove_not_found",         "&c&lRank with id &r&e&l{0} &r&c&lwas not found in rank group &r&e&l{1}"),
    RANK_GROUP_MOVE         ("rank.group.move",                     "&a&lMoved &r&e&l{0} &r&a&lfrom position &r&e&l{1}&r&a&l to &r&e&l{2}&r&a&l in rank group &r&e&l{3}"),
    RANK_GROUP_BELONGS      ("rank.group.belongs_to_group",         "&c&lRank with id &r&e&l{0} &r&c&lalready belongs to group &r&e&l{1}"),

    // Utilities
    PLAYER_INFO             ("utility.player_info.",                "&e&l {0}'s info: "),
    PLAYER_INFO_PROFILE     ("utility.player_info.player_profile",  "&7&l===== &r&f&l[&r&b&lPROFILE&r&f&l] &r&7&l====="),
    PLAYER_INFO_STATS       ("utility.player_info.player_stats",    "&7&l===== &r&f&l[&r&a&lSTATS&r&f&l] &r&7&l====="),
    PLAYER_INFO_LOAD_FAIL   ("utility.player_info.load.fail",       "&c&lFailed to load info of player &r&e&l{0}&r&c&l!"),
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