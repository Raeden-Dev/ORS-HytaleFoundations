package com.raeden.hytale.modules.utility.pages;

import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.entities.player.pages.BasicCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

public class HomesPage extends BasicCustomUIPage {

    public HomesPage(PlayerRef ref) {
        super(ref, CustomPageLifetime.CanDismiss);
    }

    @Override
    public void build(UICommandBuilder uiCommandBuilder) {
        uiCommandBuilder.append("Pages/HomesDatabase.ui");
    }
}