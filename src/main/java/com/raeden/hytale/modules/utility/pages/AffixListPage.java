package com.raeden.hytale.modules.utility.pages;

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

import javax.annotation.Nonnull;
import java.util.List;

public class AffixListPage extends InteractiveCustomUIPage<AffixListPage.Data> {

    public static class Data {
        public String clicked;

        public static final BuilderCodec<Data> CODEC =
                BuilderCodec.builder(Data.class, Data::new)
                        .append(new KeyedCodec<>("Clicked", Codec.STRING),
                                (d, v) -> d.clicked = v,
                                d -> d.clicked).add()
                        .build();
    }

    public record Affix(String id, String display, String color) {}

    private String currentAffix = "None";

    public AffixListPage(PlayerRef ref) {
        super(ref, CustomPageLifetime.CanDismiss, Data.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref,
                      @Nonnull UICommandBuilder cmd,
                      @Nonnull UIEventBuilder events,
                      @Nonnull Store<EntityStore> store) {

        cmd.append("Pages/AffixList.ui");

        List<Affix> affixes = List.of(
                new Affix("admin", "[Admin] [df_admin]", "#c0392b"),
                new Affix("youtube", "[YouTube] [df_youtube]", "#f1c40f"),
                new Affix("artist", "[Artist] [df_artist]", "#e056fd"),
                new Affix("beast", "[BEAST] [df_beast]", "#f39c12"),
                new Affix("mvp", "[MVP] [df_mvp]", "#00cec9"),
                new Affix("cracked", "[CRACKED] [df_cracked]", "#e67e22"),
                new Affix("mod", "[Moderator] [df_mod]", "#00b894"),
                new Affix("veteran", "[Veteran] [df_veteran]", "#2ecc71"),
                new Affix("op", "[OP] [df_op]", "#f1c40f"),
                new Affix("vip", "[VIP] [df_vip]", "#27ae60"),
                new Affix("amaze", "[AMAZE] [df_amaze]", "#9b59b6")
        );

        StringBuilder ui = new StringBuilder();

        for (Affix a : affixes) {

            String id = "#Affix_" + a.id();

            ui.append("Group { Anchor:(Height:55); Background:").append(a.color()).append("; Padding:(Left:10,Right:10); LayoutMode:Left; \n");

            ui.append("Label { Text:\"").append(a.display()).append("\"; Style:(FontSize:14,RenderBold:true,TextColor:#ffffff); } \n");

            ui.append("Group { FlexWeight:1; } \n");

            ui.append("TextButton ").append(id).append(" { Text:\"SELECT\"; Anchor:(Width:90,Height:30); Style:@PrimaryButtonStyle; } \n");

            ui.append("} \n");
            ui.append("Group { Anchor:(Height:8); } \n");

            events.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    id,
                    new EventData().append("Clicked", a.id())
            );
        }

        cmd.append("#AffixItems { \n" + ui + "\n }");

        cmd.append("#CurrentAffix { Text: \"CURRENTLY APPLIED AFFIX: [" + currentAffix + "]\"; }");

        events.addEventBinding(CustomUIEventBindingType.Activating,
                "#CloseButton",
                new EventData().append("Clicked", "CLOSE"));
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref,
                                @Nonnull Store<EntityStore> store,
                                @Nonnull Data data) {

        Player p = store.getComponent(ref, Player.getComponentType());
        if (p == null || data.clicked == null) return;

        if (data.clicked.equals("CLOSE")) {
            p.getPageManager().setPage(ref, store, Page.None);
            return;
        }


        currentAffix = data.clicked.toUpperCase();

        p.sendMessage(Message.parse("Selected affix: " + currentAffix));


        p.getPageManager().setPage(ref, store,
                Page.of(new AffixListPage(p.getPlayerRef())));
    }
}
