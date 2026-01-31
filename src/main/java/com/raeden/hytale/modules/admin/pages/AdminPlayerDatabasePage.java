package com.raeden.hytale.modules.admin.pages;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Admin-only UI: lists known players from PlayerDataManager's usermap.json.
 *
 * UI file: resources/Common/UI/Custom/Pages/AdminPlayerDatabase.ui
 */
public class AdminPlayerDatabasePage extends InteractiveCustomUIPage<AdminPlayerDatabasePage.BindingData> {

    private static final String UI_TEMPLATE = "Pages/AdminPlayerDatabase.ui";

    private final HytaleFoundations plugin;

    public AdminPlayerDatabasePage(@Nonnull HytaleFoundations plugin,
                                   @Nonnull PlayerRef playerRef,
                                   @Nonnull CustomPageLifetime lifetime) {
        super(playerRef, lifetime, BindingData.CODEC);
        this.plugin = plugin;
    }

    public static class BindingData {
        // Keys must start with uppercase if you ever decode them with KeyedCodec.
        // We'll keep it simple: Action + Index.
        public String action;
        public String index;

        public static final BuilderCodec<BindingData> CODEC =
                BuilderCodec.builder(BindingData.class, BindingData::new).build();

    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref,
                      @Nonnull UICommandBuilder commands,
                      @Nonnull UIEventBuilder events,
                      @Nonnull Store<EntityStore> store) {

        commands.append(UI_TEMPLATE);

        // Populate list initially
        renderPlayerList(commands, events);

        // Footer buttons
        events.addEventBinding(CustomUIEventBindingType.Activating, "#closeBtn", EventData.of("Action", "close"));
        events.addEventBinding(CustomUIEventBindingType.Activating, "#refreshBtn", EventData.of("Action", "refresh"));
        events.addEventBinding(CustomUIEventBindingType.Activating, "#addPlayerBtn", EventData.of("Action", "add"));
    }

    private void renderPlayerList(UICommandBuilder commands, UIEventBuilder events) {
        List<String> usernames = plugin.getPlayerDataManager().getKnownUsernames();

        commands.set("#playersCountText", "PLAYERS (" + usernames.size() + ")");
        commands.clear("#listContent");

        if (usernames.isEmpty()) {
            commands.appendInline(
                    "#listContent",
                    "Label { Text: No saved players found; Anchor: (Height: 28); Style: (FontSize: 14, TextColor: #FFFFFF(0.7), Alignment: Center); }"
            );
            return;
        }

        for (int i = 0; i < usernames.size(); i++) {
            String name = usernames.get(i);

            // Create a row with unique IDs so we can bind events
            String rowMarkup = "Group { LayoutMode: Left; Anchor: (Height: 44); Gap: 10; Padding: (Left: 8, Right: 8, Top: 6, Bottom: 6); Background: #141422(0.55); "
                    + "Label #row-" + i + "-name { Text: \"" + escape(name) + "\"; Anchor: (Height: 32); Style: (FontSize: 14, TextColor: #FFFFFF); Flex: (Grow: 1); } "
                    + "TextButton #row-" + i + "-view { Text: \"View Data\"; Anchor: (Width: 120, Height: 32); } "
                    + "TextButton #row-" + i + "-remove { Text: \"Remove\"; Anchor: (Width: 100, Height: 32); } "
                    + "}";

            commands.appendInline("#listContent", rowMarkup);

            // Bind row buttons (Index must be a string)
            events.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    "#row-" + i + "-view",
                    EventData.of("Action", "view").append("Index", String.valueOf(i))
            );
            events.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    "#row-" + i + "-remove",
                    EventData.of("Action", "remove").append("Index", String.valueOf(i))
            );
        }
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref,
                                @Nonnull Store<EntityStore> store,
                                @Nonnull BindingData data) {
        super.handleDataEvent(ref, store, data);

        if (data == null || data.action == null) {
            return;
        }

        switch (data.action) {
            case "close" -> close();
            case "refresh" -> {
                UICommandBuilder update = new UICommandBuilder();
                UIEventBuilder updateEvents = new UIEventBuilder();
                renderPlayerList(update, updateEvents);
                sendUpdate(update, updateEvents, false);
            }
            case "add" -> {
                this.playerRef.sendMessage(Message.raw("[INFO] Add Player not implemented yet (UI wired)."));
            }
            case "view" -> {
                int idx = safeInt(data.index, -1);
                List<String> names = plugin.getPlayerDataManager().getKnownUsernames();
                if (idx >= 0 && idx < names.size()) {
                    this.playerRef.sendMessage(Message.raw("[INFO] View Data: " + names.get(idx)));
                }
            }
            case "remove" -> {
                int idx = safeInt(data.index, -1);
                List<String> names = plugin.getPlayerDataManager().getKnownUsernames();
                if (idx >= 0 && idx < names.size()) {
                    this.playerRef.sendMessage(Message.raw("[INFO] Remove not implemented yet (would remove: " + names.get(idx) + ")"));
                }
            }
        }
    }

    private static int safeInt(String s, int fallback) {
        try {
            return Integer.parseInt(s);
        } catch (Exception ignored) {
            return fallback;
        }
    }

    /**
     * Extremely small escaping for inline markup strings.
     */
    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
