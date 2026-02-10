package com.raeden.hytale.utils;

public enum DefaultColors {
    BLACK("&0", "#000000"),
    MC_DARK_BLUE("&1", "#0000AA"),
    MC_DARK_GREEN("&2", "#00AA00"),
    MC_DARK_AQUA("&3", "#00AAAA"),
    MC_DARK_RED("&4", "#AA0000"),
    MC_DARK_PURPLE("&5", "#AA00AA"),
    MC_GOLD("&6", "#FFAA00"),
    MC_GRAY("&7", "#AAAAAA"),
    MC_DARK_GRAY("&8", "#555555"),
    MC_BLUE("&9", "#5555FF"),
    MC_GREEN("&a", "#55FF55"),
    MC_AQUA("&b", "#55FFFF"),
    MC_RED("&c", "#FF5555"),
    MC_LIGHT_PURPLE("&d", "#FF55FF"),
    MC_YELLOW("&e", "#FFFF55"),
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
