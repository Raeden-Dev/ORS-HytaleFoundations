package com.raeden.hytale.modules.utility.pages;

import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.BasicCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.raeden.hytale.modules.utility.commands.SendMailCommand;

public class SendMailPage extends BasicCustomUIPage {
    public SendMailPage(PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss);
    }
    @Override
    public void build(UICommandBuilder cmd) {
        cmd.append("Pages/HytaleFoundations_SendMail.ui");
    }
}
