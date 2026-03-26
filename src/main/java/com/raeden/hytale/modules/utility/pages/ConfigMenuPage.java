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

public class ConfigMenuPage extends InteractiveCustomUIPage<ConfigMenuPage.Data> {

    private boolean prefix = true;
    private boolean suffix = false;
    private boolean filter = true;

    public static class Data {
        public String clicked;

        public static final BuilderCodec<Data> CODEC =
                BuilderCodec.builder(Data.class, Data::new)
                        .append(new KeyedCodec<>("Clicked", Codec.STRING),
                                (d, v) -> d.clicked = v,
                                d -> d.clicked).add()
                        .build();
    }

    public ConfigMenuPage(PlayerRef ref) {
        super(ref, CustomPageLifetime.CanDismiss, Data.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref,
                      @Nonnull UICommandBuilder cmd,
                      @Nonnull UIEventBuilder events,
                      @Nonnull Store<EntityStore> store) {

        cmd.append("Pages/ConfigMenu.ui");

        bind(events, "#PrefixToggle", "PREFIX");
        bind(events, "#SuffixToggle", "SUFFIX");
        bind(events, "#FilterToggle", "FILTER");

        bind(events, "#SaveButton", "SAVE");
        bind(events, "#CancelButton", "CANCEL");
        bind(events, "#ResetButton", "RESET");

        cmd.append("#PrefixState { Text: \"Chat Prefixes: " + (prefix ? "Enabled" : "Disabled") + "\"; }");
        cmd.append("#SuffixState { Text: \"Chat Suffixes: " + (suffix ? "Enabled" : "Disabled") + "\"; }");
        cmd.append("#FilterState { Text: \"Cuss Filter: " + (filter ? "Enabled" : "Disabled") + "\"; }");
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref,
                                @Nonnull Store<EntityStore> store,
                                @Nonnull Data data) {

        Player p = store.getComponent(ref, Player.getComponentType());

        if (data.clicked == null) return;

        switch (data.clicked) {
            case "PREFIX" -> prefix = !prefix;
            case "SUFFIX" -> suffix = !suffix;
            case "FILTER" -> filter = !filter;

            case "RESET" -> {
                prefix = true;
                suffix = false;
                filter = true;
            }

            case "SAVE" -> p.sendMessage(Message.parse("Settings saved!"));

            case "CANCEL" -> p.getPageManager().setPage(ref, store, Page.None);
        }

        p.getPageManager().setPage(ref, store,
                new ConfigMenuPage((p.getPlayerRef()));
    }
    private void bind(UIEventBuilder e, String id, String key) {
        e.addEventBinding(CustomUIEventBindingType.Activating,
                id,
                new EventData().append("Clicked", key));
    }
}