package com.raeden.hytale.core.utils;

import static com.raeden.hytale.core.utils.PermissionManager.PERMISSION_GROUP_PREFIX;

public enum PermissionNodes {
    ACCESS("access"),
    ADMIN("admin"),
    NICK("nick"),
    NICK_ADMIN("nick.admin"),
    AFFIX("affix"),
    PREFIX("prefix"),
    SUFFIX("suffix"),
    VANISH("admin.vanish"),
    RANK("rank"),
    RANK_ADMIN("rank"),
    PLAYER_DATABASE("admin.playerdb"),
    ANALYTICS("analytics"),
    BLOCK_PLAYER("block.player"),
    MUTE_PLAYER("mute.player"),
    SILENCE_PLAYER("silence.player"),
    CLEAR_CHAT("chat.clear"),
    CHAT_COLORS("chat.colors"),
    PLAYTIME("utils.playtime"),
    ANNOUNCE("utils.announce"),
    TITLE("utils.send.title");

    private final String permission;
    PermissionNodes(String permission) {
        this.permission = PERMISSION_GROUP_PREFIX + permission;
    }
    public String getPermission() {return permission;}
}
