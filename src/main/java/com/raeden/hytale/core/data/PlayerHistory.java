package com.raeden.hytale.core.data;

import com.raeden.hytale.modules.admin.PlayerOffence;

import java.util.List;

public class PlayerHistory {
    private List<PlayerOffence> offences;
    public List<PlayerOffence> getOffences() {return offences;}
    public void setOffences(List<PlayerOffence> offences) {this.offences = offences;}
}
