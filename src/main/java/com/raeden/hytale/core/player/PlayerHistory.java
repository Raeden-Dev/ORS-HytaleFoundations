package com.raeden.hytale.core.player;

import com.google.gson.annotations.SerializedName;
import com.raeden.hytale.modules.admin.PlayerOffence;

import java.util.ArrayList;
import java.util.List;

public class PlayerHistory {
    @SerializedName("PLAYER_OFFENCES")
    private List<PlayerOffence> offences = new ArrayList<>();
    public List<PlayerOffence> getOffences() {return offences;}
    public void setOffences(List<PlayerOffence> offences) {this.offences = offences;}
}
