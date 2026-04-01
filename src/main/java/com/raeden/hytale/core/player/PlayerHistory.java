package com.raeden.hytale.core.player;

import com.google.gson.annotations.SerializedName;
import com.raeden.hytale.modules.admin.ReportManager;

import java.util.ArrayList;
import java.util.List;

public class PlayerHistory {
    @SerializedName("player_offences")
    private List<ReportManager.PlayerOffence> offences = new ArrayList<>();
    public List<ReportManager.PlayerOffence> getOffences() {return offences;}
    public void setOffences(List<ReportManager.PlayerOffence> offences) {this.offences = offences;}
}
