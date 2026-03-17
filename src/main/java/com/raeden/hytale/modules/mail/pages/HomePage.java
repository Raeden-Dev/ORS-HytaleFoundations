package com.raeden.hytale.modules.mail.pages;

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

public class HomePage extends InteractiveCustomUIPage<HomePage.HomePageData> {

    private final HytaleFoundations plugin;

    public static final String CLOSE_BUTTON_ID = "CloseButton";

    public record Home(String id, String name, String world, int x, int z) {}

    public static class HomePageData {

        public String clickedButton;

        public static final BuilderCodec<HomePageData> CODEC =
                BuilderCodec.builder(HomePageData.class, HomePageData::new)
                        .append(new KeyedCodec<>("ClickedButton", Codec.STRING),
                                (HomePageData obj, String val) -> obj.clickedButton = val,
                                (HomePageData obj) -> obj.clickedButton).add()
                        .build();
    }

    public HomePage(HytaleFoundations plugin, PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, HomePageData.CODEC);
        this.plugin = plugin;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref,
                      @Nonnull UICommandBuilder uiCommandBuilder,
                      @Nonnull UIEventBuilder uiEventBuilder,
                      @Nonnull Store<EntityStore> store) {

        uiCommandBuilder.append("Pages/Home.ui");

        uiEventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#CloseButton",
                new EventData().append("ClickedButton", CLOSE_BUTTON_ID)
        );

        List<Home> homes = List.of(
                new Home("home1", "55555", "default", -4073, 1532),
                new Home("home2", "h1", "default", -4073, 1532),
                new Home("home3", "h1", "default", -4073, 1532),
                new Home("home4", "Hicon", "default", -4073, 1532),
                new Home("home5", "h3", "default", -4073, 1532),
                new Home("home6", "h3", "default", -4073, 1532)
        );

        String homesMarkup = buildHomesGrid(homes, uiEventBuilder);

        uiCommandBuilder.append("#HomesGrid { \n" + homesMarkup + "\n }");
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref,
                                @Nonnull Store<EntityStore> store,
                                @Nonnull HomePageData data) {

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        if (CLOSE_BUTTON_ID.equals(data.clickedButton)) {
            player.getPageManager().setPage(ref, store, Page.None);
            return;
        }

        if (data.clickedButton != null && data.clickedButton.startsWith("TP_")) {

            String homeId = data.clickedButton.substring(3);

            player.sendMessage(Message.parse("Teleporting to home: " + homeId));

            player.getPageManager().setPage(ref, store, Page.None);
        }

        if (data.clickedButton != null && data.clickedButton.startsWith("EDIT_")) {

            String homeId = data.clickedButton.substring(5);

            player.sendMessage(Message.parse("Editing home: " + homeId));
        }
    }

    private String buildHomesGrid(List<Home> homes, UIEventBuilder uiEventBuilder) {

        StringBuilder sb = new StringBuilder();

        int maxPerRow = 4;

        for (int i = 0; i < homes.size(); i++) {

            Home home = homes.get(i);

            if (i % maxPerRow == 0) {

                if (i > 0) {
                    sb.append("}\n");
                    sb.append("Group { Anchor: (Height: 12); }\n");
                }

                sb.append("Group { LayoutMode: Left; Anchor: (Height: 120); \n");

            } else {

                sb.append("Group { Anchor: (Width: 12); }\n");
            }

            String cardId = "#Home_" + home.id();

            sb.append("Group ").append(cardId).append(" {\n");
            sb.append("Anchor: (Width: 150, Height: 110);\n");
            sb.append("Background: #263047;\n");
            sb.append("Padding: (Full: 10);\n");
            sb.append("LayoutMode: Top;\n");

            sb.append("Label { Text: \"").append(home.name()).append("\"; Style:(FontSize:16,RenderBold:true,TextColor:#55FF55); }\n");

            sb.append("Label { Text: \"World: ").append(home.world()).append("\"; Style:(FontSize:11,TextColor:#AAAAAA); }\n");

            sb.append("Label { Text: \"").append(home.x()).append(", ").append(home.z()).append("\"; Style:(FontSize:11,TextColor:#AAAAAA); }\n");

            sb.append("Group { FlexWeight:1; }\n");

            sb.append("Group { LayoutMode: Left; Anchor:(Height:30); \n");

            String tpButton = "#Go_" + home.id();
            String editButton = "#Edit_" + home.id();

            sb.append("TextButton ").append(tpButton).append(" { Text:\"GO\"; Anchor:(Width:60,Height:28); }\n");

            sb.append("Group { Anchor:(Width:6); }\n");

            sb.append("TextButton ").append(editButton).append(" { Text:\"EDIT\"; Anchor:(Width:70,Height:28); }\n");

            sb.append("}\n");

            sb.append("}\n");

            uiEventBuilder.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    tpButton,
                    new EventData().append("ClickedButton", "TP_" + home.id())
            );

            uiEventBuilder.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    editButton,
                    new EventData().append("ClickedButton", "EDIT_" + home.id())
            );
        }

        if (!homes.isEmpty()) {
            sb.append("}\n");
        }

        return sb.toString();
    }
}