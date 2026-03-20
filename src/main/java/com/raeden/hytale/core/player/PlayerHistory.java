package com.raeden.hytale.core.player;

import com.google.gson.annotations.SerializedName;
import com.raeden.hytale.modules.admin.ReportManager;

import java.util.ArrayList;
import java.util.List;

public class PlayerHistory {
    @SerializedName("PLAYER_OFFENCES")
    private List<ReportManager> offences = new ArrayList<>();
    public List<ReportManager> getOffences() {return offences;}
    public void setOffences(List<ReportManager> offences) {this.offences = offences;}
}
