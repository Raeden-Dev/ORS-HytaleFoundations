package com.raeden.hytale.utils;

public enum colors {
    BLACK("000000"),
    WHITE("FFFFFF"),
    MC_DARK_BLUE("0000AA"),
    MC_DARK_GREEN("00AA00"),
    MC_DARK_AQUA("00AAAA"),
    MC_DARK_RED("AA0000"),
    MC_DARK_PURPLE("AA00AA"),
    MC_GOLD("FFAA00"),
    MC_GRAY("AAAAAA"),
    MC_DARK_GRAY("555555"),
    MC_BLUE("5555FF"),
    MC_GREEN("55FF55"),
    MC_AQUA("55FFFF"),
    MC_RED("FF5555"),
    MC_LIGHT_PURPLE("FF55FF"),
    MC_YELLOW("FFFF55"),
    PEACH("F8B195"),
    CUTE_PINK("F67290"),
    LIGHT_CRIMSON("C06C84"),
    CHARRED_BLUE("6C5B7B"),
    BLUEY_BLUE("355C7D"),
    LIME("BCC07B"),
    SKY_BLUE("D5EDF8"),
    PALE_RED("EA7D70"),
    PALE_WATER("7D8BE0"),
    PALE_PURPLE("9A81B0"),
    LIGHT_ORANGE("FFAF6E");

    private final String hex;
    colors(String hex) {this.hex = hex;}
    public String getHex() {return hex;}
}
