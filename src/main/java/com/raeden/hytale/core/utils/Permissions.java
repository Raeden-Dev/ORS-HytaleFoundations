package com.raeden.hytale.core.utils;

public enum Permissions {
    ACCESS("hytalefoundations.access"),
    ADMIN("hytalefoundations.admin"),
    NICK("hytalefoundations.nick"),
    NICK_ADMIN("hytalefoundations.nick.admin"),
    AFFIX("hytalefoundations.affix"),
    PREFIX("hytalefoundations.prefix"),
    SUFFIX("hytalefoundations.suffix"),
    VANISH("hytalefoundations.admin.vanish"),
    RANK("hytalefoundations.rank"),
    RANK_ADMIN("hytalefoundations.rank"),
    PLAYER_DATABASE("hytalefoundations.admin.playerdb"),
    ANALYTICS("hytalefoundations.analytics"),
    BLOCK_PLAYER("hytalefoundations.block.player"),
    MUTE_PLAYER("hytalefoundations.mute.player"),
    SILENCE_PLAYER("hytalefoundations.silence.player"),
    CLEAR_CHAT("hytalefoundations.chat.clear"),
    CHAT_COLORS("hytalefoundations.chat.colors"),
    PLAYTIME("hytalefoundations.utils.playtime"),
    ANNOUNCE("hytalefoundations.utils.announce"),
    TITLE("hytalefoundations.utils.send.title");

    private final String permission;
    Permissions(String permission) {
        this.permission = permission;
    }
    public String getPermission() {return permission;}
}
