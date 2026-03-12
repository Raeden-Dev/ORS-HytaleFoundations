package com.raeden.hytale.modules.home.pages;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;

import javax.annotation.Nonnull;
import java.util.List;

public class HomesPage extends InteractiveCustomUIPage<HomesPage.HomesData> {

    private final HytaleFoundations hytaleFoundations;
    public static final String CLOSE_BUTTON_ID = "CloseButton";

    public record Home(String id, String name, String world, int x, int z) {}

    public static class HomesData {
        public String clickedButton;

        public static final BuilderCodec<HomesData> CODEC =
                BuilderCodec.builder(HomesData.class, HomesData::new)
                        .append(new KeyedCodec<>("ClickedButton", Codec.STRING), (HomesData obj, String val) -> obj.clickedButton = val, (HomesData obj) -> obj.clickedButton).add()
                        .build();
    }

    public HomesPage(HytaleFoundations hytaleFoundations, PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, HomesData.CODEC);
        this.hytaleFoundations = hytaleFoundations;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/HytaleFoundations_Homes.ui");

        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#CloseButton", new EventData().append("ClickedButton", CLOSE_BUTTON_ID));

        List<Home> playerHomes = List.of(
                new Home("home_1", "55555", "default", -4073, 1532),
                new Home("home_2", "h1", "default", -4073, 1532),
                new Home("home_3", "h1", "default", -4073, 1532),
                new Home("home_4", "🏠", "default", -4073, 1532)
        );

        String dynamicGridMarkup = buildDynamicHomesGrid(playerHomes, uiEventBuilder);

        uiCommandBuilder.append("#HomesScroll { \n" + dynamicGridMarkup + "\n}");
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull HomesData data) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if(player == null) return;

        if (CLOSE_BUTTON_ID.equals(data.clickedButton)) {
            player.getPageManager().setPage(ref, store, Page.None);
            return;
        }

        if (data.clickedButton != null && data.clickedButton.startsWith("TP_")) {
            String homeIdToTeleport = data.clickedButton.substring(3);

            player.sendMessage(Message.parse("Teleporting to home: " + homeIdToTeleport));
            player.getPageManager().setPage(ref, store, Page.None);
        }
    }

    private String buildDynamicHomesGrid(List<Home> homes, UIEventBuilder uiEventBuilder) {
        StringBuilder sb = new StringBuilder();
        int maxPerRow = 5;

        for (int i = 0; i < homes.size(); i++) {
            Home home = homes.get(i);

            if (i % maxPerRow == 0) {
                if (i > 0) {
                    sb.append("} \n");
                    sb.append("Group { Anchor: (Height: 15); } \n");
                }
                sb.append("Group { LayoutMode: Left; Anchor: (Height: 160); \n");
            } else {
                sb.append("Group { Anchor: (Width: 15); } \n");
            }

            String cardId = "#HomeCard_" + home.id();
            String locationText = "World: " + home.world() + "\\nat " + home.x() + ", " + home.z();

            sb.append("Button ").append(cardId).append(" { \n");
            sb.append("  Anchor: (Width: 140, Height: 160); \n");
            sb.append("  Background: #2b3542; \n");
            sb.append("  Padding: (Full: 12); \n");
            sb.append("  LayoutMode: Top; \n");

            sb.append("  Group { Anchor: (Height: 40); \n");
            sb.append("    Label { Text: \"").append(home.name()).append("\"; Style: (FontSize: 24, TextColor: #85c77e, RenderBold: true, HorizontalAlignment: Left); } \n");
            sb.append("  } \n");

            sb.append("  Group { FlexWeight: 1; } \n");

            sb.append("  Label { Text: \"").append(locationText).append("\"; Style: (FontSize: 12, TextColor: #ffffff, HorizontalAlignment: Left); } \n");

            sb.append("  Group { FlexWeight: 1; } \n");

            sb.append("  Group { LayoutMode: Left; Anchor: (Height: 20); \n");
            sb.append("    Group { FlexWeight: 1; } \n");
            sb.append("    Label { Text: \"⚙\"; Style: (FontSize: 18, TextColor: #b4c8c9); } \n");
            sb.append("  } \n");

            sb.append("} \n");

            uiEventBuilder.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    cardId,
                    new EventData().append("ClickedButton", "TP_" + home.id())
            );
        }

        if (!homes.isEmpty()) {
            sb.append("} \n");
        }

        return sb.toString();
    }
}