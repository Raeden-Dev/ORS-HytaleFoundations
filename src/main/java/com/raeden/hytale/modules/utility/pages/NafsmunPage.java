package com.raeden.hytale.modules.utility.pages;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.Message;

import javax.annotation.Nonnull;

public class NafsmunPage extends InteractiveCustomUIPage<NafsmunPage.NafsmunData> {

    public String SEND_BUTTON_ID = "SendButton";

    public static class NafsmunData {
        public String message;
        public String clickedButton;

        public static final BuilderCodec<NafsmunData> CODEC =
                BuilderCodec.builder(NafsmunData.class, NafsmunData::new)

                        .append(new KeyedCodec<>("@Message", Codec.STRING),
                                (NafsmunData obj, String val) -> obj.message = val,
                                (NafsmunData obj) -> obj.message).add()

                        .append(new KeyedCodec<>("ClickedButton", Codec.STRING),
                                (NafsmunData obj, String val) -> obj.clickedButton = val,
                                (NafsmunData obj) -> obj.clickedButton).add()

                        .build();
    }

    public NafsmunPage(PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, NafsmunData.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref,
                      @Nonnull UICommandBuilder uiCommandBuilder,
                      @Nonnull UIEventBuilder uiEventBuilder,
                      @Nonnull Store<EntityStore> store) {

        uiCommandBuilder.append("Pages/Nafsmun.ui");

        uiEventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#SendButton",
                new EventData()
                        .append("@Message", "#MessageInput.Value")
                        .append("ClickedButton", SEND_BUTTON_ID)
        );
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref,
                                @Nonnull Store<EntityStore> store,
                                @Nonnull NafsmunData data) {

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        if (SEND_BUTTON_ID.equals(data.clickedButton)) {

            if (data.message == null || data.message.isEmpty()) {
                player.sendMessage(Message.raw("Message cannot be empty."));
                return;
            }

            // Example action (you can replace this logic)
            player.sendMessage(Message.raw("You sent: " + data.message));

            // Close page
            player.getPageManager().setPage(ref, store, Page.None);
        }
    }
}