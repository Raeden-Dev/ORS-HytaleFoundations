package com.raeden.hytale.modules.rank;

import com.raeden.hytale.HytaleFoundations;

public class RankManager {
    private final HytaleFoundations hytaleFoundations;
    public RankManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        
    }

    public static class Rank {
        private String title;
        private String permissionGroup;
        private String chatPrefix;
        private int rankPriority;

    }
}
