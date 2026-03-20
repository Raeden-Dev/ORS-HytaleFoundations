package com.raeden.hytale.core.alias;

import com.raeden.hytale.core.utils.Permissions;

public enum DefaultCommands {
    CORE_COMMAND("foundation","foundation", Permissions.ADMIN.getPermission(), "hf", "fd"),
    COMMAND_MANAGER_COMMAND("commandmanager","commandmanager", Permissions.ADMIN.getPermission(), "cm", "alias"),
    RANK_COMMAND("rank","rank", Permissions.RANK.getPermission(), "rk"),
    HOME_COMMAND("home","home", Permissions.HOME.getPermission()),
    HOMES_COMMAND("homes","homes", Permissions.HOME.getPermission()),
    MAIL_COMMAND("mail","mail", Permissions.MAIL.getPermission()),
    ADMIN_ANNOUNCE_COMMAND("announce", Permissions.ANNOUNCE.getPermission(), "anno", "anc"),
    ADMIN_REPORT_COMMAND("report","report", Permissions.ACCESS.getPermission()),
    ADMIN_TITLE_COMMAND("title","title", Permissions.TITLE.getPermission()),
    ADMIN_VANISH_COMMAND("vanish","vanish", Permissions.VANISH.getPermission(), "v"),
    UTILITY_PLAYER_INFO("playerinfo","playerinfo", Permissions.PLAYER_INFO.getPermission(), "pinfo"),
    UTILITY_PLAYTIME("playtime","playtime", Permissions.PLAYTIME.getPermission(), "ptime"),
    CHAT_CLEAR("clearchat","clearchat", Permissions.CLEAR_CHAT.getPermission(), "cchat"),
    CHAT_MUTE_PLAYER("mute","mute", Permissions.MUTE_PLAYER.getPermission()),
    CHAT_UNMUTE_PLAYER("unmute","unmute", Permissions.MUTE_PLAYER.getPermission()),
    CHAT_UNMUTE_ALL_PLAYER("unmuteall","unmuteall", Permissions.MUTE_PLAYER.getPermission(), "umall"),
    CHAT_AFFIX_COMMAND("affix","affix", Permissions.AFFIX.getPermission(), "afx"),
    CHAT_PREFIX_COMMAND("prefix","prefix", Permissions.PREFIX.getPermission(), "pfx"),
    CHAT_SUFFIX_COMMAND("suffix","suffix", Permissions.SUFFIX.getPermission(), "sfx"),
    CHAT_SILENCE_PLAYER("silence","silence", Permissions.SILENCE_PLAYER.getPermission()),
    CHAT_BLOCK_COMMAND("block","block", Permissions.BLOCK_PLAYER.getPermission()),
    CHAT_UNBLOCK_COMMAND("unblock","unblock", Permissions.BLOCK_PLAYER.getPermission()),
    CHAT_IGNORE_COMMAND("ignore","ignore", Permissions.ACCESS.getPermission(), "igr"),
    CHAT_MESSAGE_COMMAND("message","message", Permissions.ACCESS.getPermission(), "msg"),
    CHAT_REPLY_COMMAND("reply","reply", Permissions.ACCESS.getPermission(), "r"),
    CHAT_NICKNAME_COMMAND("nick","nick", Permissions.NICK.getPermission());

    private final String targetCommand;
    private final String name;
    private final String permission;
    private final String[] aliases;

    DefaultCommands(String targetCommand, String name, String permission, String... aliases) {
        this.targetCommand = targetCommand;
        this.name = name;
        this.permission = permission;
        this.aliases = aliases;
    }

    public String getTargetCommand() {return targetCommand;}
    public String getName() { return name; }
    public String getPermission() { return permission; }
    public String[] getAliases() { return aliases; }
}