package com.raeden.hytale.modules.chat.pages;

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
import com.raeden.hytale.core.player.PlayerProfile;
import com.raeden.hytale.modules.chat.AffixManager;
import com.raeden.hytale.modules.chat.ColorManager;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class AffixListPage extends InteractiveCustomUIPage<AffixListPage.AffixListData> {

    public static final String CLOSE_BUTTON_ID = "CloseButton";
    public static final String CLOSE_X_BUTTON_ID = "CloseXButton";
    public static final String CLEAR_ALL_BUTTON_ID = "ClearAllButton";
    public static final String SET_PREFIX_PREFIX = "SetPrefix:";
    public static final String SET_SUFFIX_PREFIX = "SetSuffix:";

    private final HytaleFoundations hytaleFoundations;

    public static class AffixListData {
        public String clickedButton;

        public static final BuilderCodec<AffixListData> CODEC =
                BuilderCodec.builder(AffixListData.class, AffixListData::new)
                        .append(new KeyedCodec<>("ClickedButton", Codec.STRING),
                                (obj, val) -> obj.clickedButton = val,
                                obj -> obj.clickedButton).add()
                        .build();
    }

    public AffixListPage(HytaleFoundations hytaleFoundations, PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, AffixListData.CODEC);
        this.hytaleFoundations = hytaleFoundations;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref,
                      @Nonnull UICommandBuilder cmd,
                      @Nonnull UIEventBuilder events,
                      @Nonnull Store<EntityStore> store) {

        cmd.append("Pages/HF_AffixList.ui");

        AffixManager affixManager = hytaleFoundations.getChatManager().getAffixManager();
        ColorManager colorManager = hytaleFoundations.getChatManager().getColorManager();
        boolean isAdmin = hytaleFoundations.getPermissionManager().isPlayerAdmin(playerRef);

        List<AffixManager.PlayerAffix> sortedAffixes = new ArrayList<>(affixManager.getAffixMap().values());
        sortedAffixes.sort(Comparator.comparingInt(AffixManager.PlayerAffix::getPriority).reversed());

        cmd.set("#TitleLabel.Text", "AVAILABLE AFFIXES (" + sortedAffixes.size() + ")");

        for (int i = 0; i < sortedAffixes.size(); i++) {
            AffixManager.PlayerAffix affix = sortedAffixes.get(i);
            String rowBase = "#AffixItems[" + i + "]";

            cmd.append("#AffixItems", "Pages/HF_AffixListRow.ui");

            Message displayMessage = colorManager.parseText(affix.getDisplayText());
            cmd.set(rowBase + " #DisplayLabel.Text", displayMessage);

            if (isAdmin) {
                cmd.set(rowBase + " #IdLabel.Text", "[" + affix.getId() + "]");
            } else {
                cmd.set(rowBase + " #IdLabel.Visible", false);
                cmd.set(rowBase + " #SetPrefixButton.Visible", false);
                cmd.set(rowBase + " #SetSuffixButton.Visible", false);
            }

            if (isAdmin) {
                events.addEventBinding(CustomUIEventBindingType.Activating,
                        rowBase + " #SetPrefixButton",
                        new EventData().append("ClickedButton", SET_PREFIX_PREFIX + affix.getId()));
                events.addEventBinding(CustomUIEventBindingType.Activating,
                        rowBase + " #SetSuffixButton",
                        new EventData().append("ClickedButton", SET_SUFFIX_PREFIX + affix.getId()));
            }
        }

        cmd.set("#CurrentAffixLabel.Text", buildCurrentAffixMessage(colorManager));

        if (!isAdmin) {
            cmd.set("#ClearAllButton.Visible", false);
        }

        events.addEventBinding(CustomUIEventBindingType.Activating, "#CloseButton",
                new EventData().append("ClickedButton", CLOSE_BUTTON_ID));
        events.addEventBinding(CustomUIEventBindingType.Activating, "#CloseXButton",
                new EventData().append("ClickedButton", CLOSE_X_BUTTON_ID));
        events.addEventBinding(CustomUIEventBindingType.Activating, "#ClearAllButton",
                new EventData().append("ClickedButton", CLEAR_ALL_BUTTON_ID));
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref,
                                @Nonnull Store<EntityStore> store,
                                @Nonnull AffixListData data) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;
        if (data.clickedButton == null) return;

        if (CLOSE_BUTTON_ID.equals(data.clickedButton) || CLOSE_X_BUTTON_ID.equals(data.clickedButton)) {
            player.getPageManager().setPage(ref, store, Page.None);
            return;
        }

        boolean isAdmin = hytaleFoundations.getPermissionManager().isPlayerAdmin(playerRef);
        if (!isAdmin) return;

        AffixManager affixManager = hytaleFoundations.getChatManager().getAffixManager();
        String targetUsername = playerRef.getUsername();

        if (CLEAR_ALL_BUTTON_ID.equals(data.clickedButton)) {
            affixManager.removeAllAffixFromPlayer(playerRef, targetUsername);
            reopen(ref, store, player);
            return;
        }

        if (data.clickedButton.startsWith(SET_PREFIX_PREFIX)) {
            String affixId = data.clickedButton.substring(SET_PREFIX_PREFIX.length());
            affixManager.addPrefixToPlayer(playerRef, targetUsername, affixId, true);
            reopen(ref, store, player);
            return;
        }

        if (data.clickedButton.startsWith(SET_SUFFIX_PREFIX)) {
            String affixId = data.clickedButton.substring(SET_SUFFIX_PREFIX.length());
            affixManager.addSuffixToPlayer(playerRef, targetUsername, affixId, true);
            reopen(ref, store, player);
        }
    }

    private void reopen(Ref<EntityStore> ref, Store<EntityStore> store, Player player) {
        player.getPageManager().openCustomPage(ref, store, new AffixListPage(hytaleFoundations, playerRef));
    }

    private Message buildCurrentAffixMessage(ColorManager colorManager) {
        PlayerProfile profile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(playerRef.getUsername());
        Message message = Message.raw("CURRENTLY APPLIED AFFIX: ").color("#ffd27f").bold(true);
        if (profile == null) {
            message.insert(Message.raw("[None]").color("#ffd27f").bold(true));
            return message;
        }
        StringBuilder activeAffixes = new StringBuilder();
        for (Map.Entry<String, String> entry : profile.getActivePrefix().entrySet()) {
            if (activeAffixes.length() > 0) activeAffixes.append(" ");
            activeAffixes.append(entry.getValue());
        }
        for (Map.Entry<String, String> entry : profile.getActiveSuffix().entrySet()) {
            if (activeAffixes.length() > 0) activeAffixes.append(" ");
            activeAffixes.append(entry.getValue());
        }
        if (activeAffixes.length() == 0) {
            message.insert(Message.raw("[None]").color("#ffd27f").bold(true));
        } else {
            message.insert(colorManager.parseText(activeAffixes.toString()));
        }
        return message;
    }
}
