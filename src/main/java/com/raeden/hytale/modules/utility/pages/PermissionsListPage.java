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

public class PermissionsListPage extends InteractiveCustomUIPage<PermissionsListPage.Data> {

    public static class Data {
        public String clicked;

        public static final BuilderCodec<Data> CODEC =
                BuilderCodec.builder(Data.class, Data::new)
                        .append(new KeyedCodec<>("Clicked", Codec.STRING),
                                (d, v) -> d.clicked = v,
                                d -> d.clicked).add()
                        .build();
    }

    public record Permission(String id, String name, String node) {}

    public PermissionsListPage(PlayerRef ref) {
        super(ref, CustomPageLifetime.CanDismiss, Data.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref,
                      @Nonnull UICommandBuilder cmd,
                      @Nonnull UIEventBuilder events,
                      @Nonnull Store<EntityStore> store) {

        cmd.append("Pages/HF_PermissionsList.ui");

        List<Permission> perms = List.of(
                new Permission("vanish", "VANISH", "hytalefoundations.admin.vanish"),
                new Permission("announce", "ANNOUNCE", "hytalefoundations.utils.announce"),
                new Permission("affix", "AFFIX", "hytalefoundations.admin.affix"),
                new Permission("nick", "NICK_ADMIN", "hytalefoundations.admin.nick")
        );

        StringBuilder ui = new StringBuilder();

        for (Permission p : perms) {

            String id = "#Perm_" + p.id();

            ui.append("Group { Anchor:(Height:70); Background:#1c2733; Padding:(Left:15,Right:15); LayoutMode:Left; \n");

            ui.append("Group { FlexWeight:1; LayoutMode:Top; \n");
            ui.append("Label { Text:\"").append(p.name()).append("\"; Style:(FontSize:16,RenderBold:true,TextColor:#ffd27f); } \n");
            ui.append("Label { Text:\"Node: ").append(p.node()).append("\"; Style:(FontSize:12,TextColor:#96a9be); } \n");
            ui.append("} \n");

            ui.append("TextButton ").append(id).append(" { Text:\"EDIT\"; Anchor:(Width:100,Height:36); Style:@SecondaryBtnStyle; } \n");

            ui.append("} \n");
            ui.append("Group { Anchor:(Height:10); } \n");

            events.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    id,
                    new EventData().append("Clicked", p.id())
            );
        }

        cmd.append("#PermissionItems { \n" + ui + "\n }");

        events.addEventBinding(CustomUIEventBindingType.Activating,
                "#ManageButton",
                new EventData().append("Clicked", "MANAGE"));
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref,
                                @Nonnull Store<EntityStore> store,
                                @Nonnull Data data) {

        Player p = store.getComponent(ref, Player.getComponentType());
        if (p == null || data.clicked == null) return;

        if (data.clicked.equals("MANAGE")) {
            p.sendMessage(Message.parse("Opening permission manager..."));
            return;
        }


        p.sendMessage(Message.parse("Editing permission: " + data.clicked));
    }
}
