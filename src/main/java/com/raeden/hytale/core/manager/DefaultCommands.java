package com.raeden.hytale.core.manager;

import com.raeden.hytale.core.utils.Permissions;
import java.util.List;

public enum DefaultCommands {
    CORE_COMMAND("foundation", Permissions.ADMIN.getPermission(), "hf", "fd"),
    RANK_COMMAND("rank", Permissions.RANK.getPermission(), "rk"),
    HOME_COMMAND("home", Permissions.HOME.getPermission()),
    HOMES_COMMAND("homes", Permissions.HOME.getPermission()),
    MAIL_COMMAND("mail", Permissions.MAIL.getPermission()),
    ADMIN_ANNOUNCE_COMMAND("announce", Permissions.ANNOUNCE.getPermission(), "anno", "anc"),
    ADMIN_REPORT_COMMAND("report", Permissions.ACCESS.getPermission()),
    ADMIN_TITLE_COMMAND("title", Permissions.TITLE.getPermission()),
    ADMIN_VANISH_COMMAND("vanish", Permissions.VANISH.getPermission(), "v"),
    UTILITY_PLAYER_INFO("playerinfo", Permissions.PLAYER_INFO.getPermission(), "pinfo"),
    UTILITY_PLAYTIME("playtime", Permissions.PLAYTIME.getPermission(), "ptime"),
    CHAT_CLEAR("clearchat", Permissions.CLEAR_CHAT.getPermission(), "cchat"),
    CHAT_MUTE_PLAYER("mute", Permissions.MUTE_PLAYER.getPermission()),
    CHAT_UNMUTE_PLAYER("unmute", Permissions.MUTE_PLAYER.getPermission()),
    CHAT_UNMUTE_ALL_PLAYER("unmuteall", Permissions.MUTE_PLAYER.getPermission(), "umall"),
    CHAT_AFFIX_COMMAND("affix", Permissions.AFFIX.getPermission(), "afx"),
    CHAT_PREFIX_COMMAND("prefix", Permissions.PREFIX.getPermission(), "pfx"),
    CHAT_SUFFIX_COMMAND("suffix", Permissions.SUFFIX.getPermission(), "sfx"),
    CHAT_SILENCE_PLAYER("silence", Permissions.SILENCE_PLAYER.getPermission()),
    CHAT_BLOCK_COMMAND("block", Permissions.BLOCK_PLAYER.getPermission()),
    CHAT_UNBLOCK_COMMAND("unblock", Permissions.BLOCK_PLAYER.getPermission()),
    CHAT_IGNORE_COMMAND("ignore", Permissions.ACCESS.getPermission(), "igr"),
    CHAT_MESSAGE_COMMAND("message", Permissions.ACCESS.getPermission(), "msg"),
    CHAT_REPLY_COMMAND("reply", Permissions.ACCESS.getPermission(), "r"),
    CHAT_NICKNAME_COMMAND("nick", Permissions.NICK.getPermission());

    private final String name;
    private final String permission;
    private final String[] aliases;

    DefaultCommands(String name, String permission, String... aliases) {
        this.name = name;
        this.permission = permission;
        this.aliases = aliases;
    }

    public String getName() { return name; }
    public String getPermission() { return permission; }
    public String[] getAliases() { return aliases; }
}