package com.raeden.hytale.lang;

public enum LangPaths {
    GET_PLAYER_FAIL("test");

    private final String path;
    LangPaths(String path) {
        this.path = path;
    }

    public String getPath() {return path;}
}
