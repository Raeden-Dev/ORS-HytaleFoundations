package com.raeden.hytale.modules.utility.pages;

import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class CreateAliasPage extends InteractiveCustomUIPage<CreateAliasPage.Data> {

    public static class Data {
        public String clicked;

        public static final BuilderCodec<Data> CODEC =
                BuilderCodec.builder(Data.class, Data::new)
                        .append(new KeyedCodec<>("Clicked", Codec.STRING),
                                (d, v) -> d.clicked = v,
                                d -> d.clicked).add()
                        .build();
    }

    public CreateAliasPage(PlayerRef ref) {
        super(ref, CustomPageLifetime.CanDismiss, Data.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref,
                      @Nonnull UICommandBuilder cmd,
                      @Nonnull UIEventBuilder events,
                      @Nonnull Store<EntityStore> store) {

        cmd.append("Pages/CreateAlias.ui");

        events.addEventBinding(CustomUIEventBindingType.Activating, "#CreateButton",
                new EventData().append("Clicked", "CREATE"));

        events.addEventBinding(CustomUIEventBindingType.Activating, "#CancelButton",
                new EventData().append("Clicked", "CANCEL"));
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref,
                                @Nonnull Store<EntityStore> store,
                                @Nonnull Data data) {

        Player p = store.getComponent(ref, Player.getComponentType());

        if (data.clicked == null) return;

        if (data.clicked.equals("CREATE")) {
            p.sendMessage(Message.parse("Alias created!"));
        }

        if (data.clicked.equals("CANCEL")) p.getPageManager().setPage(ref, store, Page.None);
    }
}