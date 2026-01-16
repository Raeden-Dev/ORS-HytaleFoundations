package com.raeden.hytale.utils;

import com.hypixel.hytale.protocol.ItemWithAllMetadata;
import com.hypixel.hytale.protocol.packets.interface_.NotificationStyle;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.util.NotificationUtil;

public class titleUtils {
    public static void sendTitle(PacketHandler packetHandler, Message primaryMessage, Message secondaryMessage, ItemWithAllMetadata itemIcon, NotificationStyle style) {
        NotificationUtil.sendNotification(packetHandler, primaryMessage, secondaryMessage, itemIcon, style);
    }
}
