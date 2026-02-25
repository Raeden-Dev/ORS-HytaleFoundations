package com.raeden.hytale.modules.chat;

public enum DefaultAffix {
    // Staff
    OWNER("df_owner", "&7&l[&r&c&lOWNER&r&7&l]", 100000),
    OP("df_op",      "&7&l[&r&b&lOP&r&7&l]",         10000),
    MANAGER("df_manager", "&7&l[&r&c&lManager&r&7&l]",    9999),
    ADMIN("df_admin",   "&7&l[&r&4&lAdmin&r&7&l]",      9998),
    MOD("df_mod",     "&7&l[&r&3&lModerator&r&7&l]",  9997),
    HELPER("df_helper", "&7&l[&r&a&lHelper&r&7&l]",     9996),
    // Dev
    DEVELOPER("df_developer", "&7&l[&r&3&lDev&b&leloper&r&7&l]", 9995),
    BUILDER("df_builder", "&7&l[&r&6&lBuilder&r&7&l]", 9994),
    ARTIST("df_artist", "&7&l[&r&d&lArt&5&list&r&7&l]", 9993),
    // Media
    YOUTUBE("df_youtube", "&7&l[&r&f&lYou&c&lTube&r&7&l]", 9000),
    MEDIA("df_media", "&7&l[&r&5&lMedia&r&7&l]", 8999),
    // Premium
    VETERAN("df_veteran", "&7&l[&r&2&lVeteran&r&7&l]", 50),
    MVP("df_mvp", "&7&l[&r&b&lMVP&r&7&l]", 30),
    VIP("df_vip", "&7&l[&r&a&lVIP&r&7&l]", 20),
    // Cosmetic
    CRACKED("df_cracked", "&7&l[&r&c&lC&r&0&lR&r&f&lAC&r&6&lK&r&c&lED&r&7&l]", 10),
    BEAST("df_beast",   "&7&l[&r&e&6&lBEAST&r&7&l]",    10),
    NOOB("df_noob",    "&7&l[&r&5&d&lNOOB&r&7&l]",     10),
    PRO("df_pro",     "&7&l[&r&9&lPRO&r&7&l]",        10),
    AMAZE("df_amaze",   "&7&l[&r&c&6&e&2&a&b&3&1&d&5&lAMAZE&r&7&l]", 10),
    // Base
    PLAYER("df_player",  "&7&l[&r&f&lPlayer&r&7&l]",     1),
    GUEST("df_guest", "&7&l[&r&8&lGuest&r&7&l]", 0);

    private final String id;
    private final String text;
    private final int priority;
    DefaultAffix(String id, String text, int priority) {
        this.id = id;
        this.text = text;
        this.priority = priority;
    }
    public String getId() {return id;}
    public String getText() {return text;}
    public int getPriority() {return priority;}
}
