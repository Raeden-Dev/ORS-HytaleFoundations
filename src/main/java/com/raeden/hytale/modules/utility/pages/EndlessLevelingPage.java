package com.raeden.hytale.modules.utility.pages;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
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
import java.util.HashMap;
import java.util.Map;

public class EndlessLevelingPage extends InteractiveCustomUIPage<EndlessLevelingPage.SettingsData> {

    private final HytaleFoundations plugin;

    public static final String CLOSE_BUTTON_ID = "CloseButton";


    private static final String HUD = "HUD";
    private static final String CRIT = "CRIT";
    private static final String XP = "XP";
    private static final String PASSIVE = "PASSIVE";
    private static final String LUCK = "LUCK";
    private static final String REGEN = "REGEN";
    private static final String AUGMENT = "AUGMENT";


    private final Map<String, Boolean> settings = new HashMap<>();

    public static class SettingsData {
        public String clickedButton;

        public static final BuilderCodec<SettingsData> CODEC =
                BuilderCodec.builder(SettingsData.class, SettingsData::new)
                        .append(new KeyedCodec<>("ClickedButton", Codec.STRING),
                                (SettingsData obj, String val) -> obj.clickedButton = val,
                                (SettingsData obj) -> obj.clickedButton).add()
                        .build();
    }

    public EndlessLevelingPage(HytaleFoundations plugin, PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, SettingsData.CODEC);
        this.plugin = plugin;


        settings.put(HUD, true);
        settings.put(CRIT, true);
        settings.put(XP, true);
        settings.put(PASSIVE, true);
        settings.put(LUCK, true);
        settings.put(REGEN, true);
        settings.put(AUGMENT, false);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref,
                      @Nonnull UICommandBuilder uiCommandBuilder,
                      @Nonnull UIEventBuilder uiEventBuilder,
                      @Nonnull Store<EntityStore> store) {

        uiCommandBuilder.append("Pages/EndlessLeveling.ui");


        bind(uiEventBuilder, "#HudToggle", HUD);
        bind(uiEventBuilder, "#CritToggle", CRIT);
        bind(uiEventBuilder, "#XpToggle", XP);
        bind(uiEventBuilder, "#PassiveToggle", PASSIVE);
        bind(uiEventBuilder, "#LuckToggle", LUCK);
        bind(uiEventBuilder, "#RegenToggle", REGEN);
        bind(uiEventBuilder, "#AugmentToggle", AUGMENT);


        uiCommandBuilder.append(buildState("#HudState", settings.get(HUD)));
        uiCommandBuilder.append(buildState("#CritState", settings.get(CRIT)));
        uiCommandBuilder.append(buildState("#XpState", settings.get(XP)));
        uiCommandBuilder.append(buildState("#PassiveState", settings.get(PASSIVE)));
        uiCommandBuilder.append(buildState("#LuckState", settings.get(LUCK)));
        uiCommandBuilder.append(buildState("#RegenState", settings.get(REGEN)));
        uiCommandBuilder.append(buildState("#AugmentState", settings.get(AUGMENT)));
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref,
                                @Nonnull Store<EntityStore> store,
                                @Nonnull SettingsData data) {

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        if (data.clickedButton == null) return;


        if (settings.containsKey(data.clickedButton)) {

            boolean newValue = !settings.get(data.clickedButton);
            settings.put(data.clickedButton, newValue);

            player.sendMessage(Message.parse(
                    data.clickedButton + " set to " + (newValue ? "ON" : "OFF")
            ));


            player.getPageManager().setPage(ref, store,
                    new EndlessLevelingPage ((plugin, player.getPlayerRef()));
        }
    }

    private void bind(UIEventBuilder builder, String elementId, String key) {
        builder.addEventBinding(
                CustomUIEventBindingType.Activating,
                elementId,
                new EventData().append("ClickedButton", key)
        );
    }

    private String buildState(String id, boolean value) {
        return id + " { Text: \"" + (value ? "ON" : "OFF") + "\"; }";
    }
}
