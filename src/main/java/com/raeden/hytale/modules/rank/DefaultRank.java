package com.raeden.hytale.modules.rank;
import com.raeden.hytale.modules.chat.DefaultAffix;

public enum DefaultRank {
    OWNER("df_owner", "hytalefoundations.rank.owner", null, DefaultAffix.OWNER.getId(), null, 100000),
    MANAGER("df_manager", "hytalefoundations.rank.manager", null, DefaultAffix.MANAGER.getId(), null, 9999),
    ADMIN("df_admin", "hytalefoundations.rank.admin", null, DefaultAffix.ADMIN.getId(), null, 9998),
    MOD("df_mod", "hytalefoundations.rank.mod", null, DefaultAffix.MOD.getId(), null, 9997),
    HELPER("df_helper", "helper", null, DefaultAffix.HELPER.getId(), null, 9996),

    DEVELOPER("df_developer", "hytalefoundations.rank.developer", null, DefaultAffix.DEVELOPER.getId(), null, 9995),
    BUILDER("df_builder", "hytalefoundations.rank.builder", null, DefaultAffix.BUILDER.getId(), null, 9994),
    ARTIST("df_artist", "hytalefoundations.rank.artist", null, DefaultAffix.ARTIST.getId(), null, 9993),

    YOUTUBE("df_youtube", "hytalefoundations.rank.youtube", null, DefaultAffix.YOUTUBE.getId(), null, 9000),
    MEDIA("df_media", "hytalefoundations.rank.media", null, DefaultAffix.MEDIA.getId(), null, 8999),

    VETERAN("df_veteran", "hytalefoundations.rank.veteran", null, DefaultAffix.VETERAN.getId(), null, 50),
    MVP("df_mvp", "hytalefoundations.rank.mvp", null, DefaultAffix.MVP.getId(), null, 30),
    VIP("df_vip", "hytalefoundations.rank.vip", null, DefaultAffix.VIP.getId(), null, 20),
    PLAYER("df_player", "hytalefoundations.rank.player", null, DefaultAffix.PLAYER.getId(), null, 1),
    GUEST("df_guest", "hytalefoundations.rank.guest", null, DefaultAffix.GUEST.getId(), null, 0);

    private final String id;
    private final String permissionGroup;
    private final String rankGroup;
    private final String chatPrefixId;
    private final String chatSuffixId;
    private final int rankPriority;
    DefaultRank(String id, String permissionGroup, String rankGroup, String chatPrefixId, String chatSuffixId, int rankPriority) {
        this.id = id;
        this.permissionGroup = permissionGroup;
        this.rankGroup = rankGroup;
        this.chatPrefixId = chatPrefixId;
        this.chatSuffixId = chatSuffixId;
        this.rankPriority = rankPriority;
    }
    public String getId() {return id;}
    public String getPermissionGroup() {return permissionGroup;}
    public String getRankGroup() {return rankGroup;}
    public String getChatPrefixId() {return chatPrefixId;}
    public String getChatSuffixId() {return chatSuffixId;}
    public int getRankPriority() {return rankPriority;}
}
