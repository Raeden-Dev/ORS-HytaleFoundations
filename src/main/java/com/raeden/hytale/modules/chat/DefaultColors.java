package com.raeden.hytale.modules.chat;

public enum DefaultColors {
    BLACK("&0", "#000000"),
    DARK_BLUE("&1", "#0000AA"),
    DARK_GREEN("&2", "#00AA00"),
    DARK_AQUA("&3", "#00AAAA"),
    DARK_RED("&4", "#AA0000"),
    DARK_PURPLE("&5", "#AA00AA"),
    GOLD("&6", "#FFAA00"),
    GRAY("&7", "#AAAAAA"),
    DARK_GRAY("&8", "#555555"),
    BLUE("&9", "#5555FF"),
    GREEN("&a", "#55FF55"),
    AQUA("&b", "#55FFFF"),
    RED("&c", "#FF5555"),
    LIGHT_PURPLE("&d", "#FF55FF"),
    YELLOW("&e", "#FFFF55"),
    WHITE("&f", "#FFFFFF");

    private final String code;
    private final String hex;
    DefaultColors(String code, String hex) {
        this.code = code;
        this.hex = hex;
    }
    public String getHex() {return hex;}
    public String getCode() {return code;}
}
