package com.raeden.hytale.modules.admin.pages;

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
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.lang.LangKey;
import com.raeden.hytale.modules.admin.pages.PlayerReportPage.PlayerReportData;
import javax.annotation.Nonnull;

import static com.raeden.hytale.HytaleFoundations.LM;

public class PlayerReportPage extends InteractiveCustomUIPage<PlayerReportData> {

    private final HytaleFoundations hytaleFoundations;

    public static final String CANCEL_BUTTON_ID = "CancelButton";
    public static final String REPORT_BUTTON_ID = "ReportButton";
    public static final String TODAY_BUTTON_ID = "TodayButton";
    public static final String FORGOT_BUTTON_ID = "ForgotButton";

    public static class PlayerReportData {
        public String targetPlayerName;
        public String dateOfOffense;
        public String offenseType;
        public String description;
        public String clickedButton;

        public static final BuilderCodec<PlayerReportData> CODEC =
                BuilderCodec.builder(PlayerReportData.class, PlayerReportData::new)
                        .append(new KeyedCodec<>("@TargetPlayerName", Codec.STRING), (PlayerReportData obj, String val) -> obj.targetPlayerName = val, (PlayerReportData obj) -> obj.targetPlayerName).add()
                        .append(new KeyedCodec<>("@DateOfOffense", Codec.STRING), (PlayerReportData obj, String val) -> obj.dateOfOffense = val, (PlayerReportData obj) -> obj.dateOfOffense).add()
                        .append(new KeyedCodec<>("@OffenseType", Codec.STRING), (PlayerReportData obj, String val) -> obj.offenseType = val, (PlayerReportData obj) -> obj.offenseType).add()
                        .append(new KeyedCodec<>("@Description", Codec.STRING), (PlayerReportData obj, String val) -> obj.description = val, (PlayerReportData obj) -> obj.description).add()
                        .append(new KeyedCodec<>("ClickedButton", Codec.STRING), (PlayerReportData obj, String val) -> obj.clickedButton = val, (PlayerReportData obj) -> obj.clickedButton).add()
                        .build();
    }

    public PlayerReportPage(HytaleFoundations hytaleFoundations, PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, PlayerReportData.CODEC);
        this.hytaleFoundations = hytaleFoundations;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        // Assuming your UI file is placed here
        uiCommandBuilder.append("Pages/HytaleFoundations_PlayerReport.ui");

        // Bind the main Report button to capture all form fields
        uiEventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#ReportButton",
                new EventData()
                        .append("@TargetPlayerName", "#PlayerNameInput.Value")
                        .append("@DateOfOffense", "#DateInput.Value")
                        .append("@OffenseType", "#OffenseTypeDropdown.Value")
                        .append("@Description", "#DescriptionInput.Value")
                        .append("ClickedButton", REPORT_BUTTON_ID)
        );

        // Bind secondary buttons
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#CancelButton", new EventData().append("ClickedButton", CANCEL_BUTTON_ID));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#TodayButton", new EventData().append("ClickedButton", TODAY_BUTTON_ID));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ForgotButton", new EventData().append("ClickedButton", FORGOT_BUTTON_ID));
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull PlayerReportData data) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if(player == null) return;

        if(CANCEL_BUTTON_ID.equals(data.clickedButton)) {
            player.getPageManager().setPage(ref, store, Page.None);
            return;
        }


        if(TODAY_BUTTON_ID.equals(data.clickedButton)) {

            return;
        }

        if(FORGOT_BUTTON_ID.equals(data.clickedButton)) {

            return;
        }

        if(REPORT_BUTTON_ID.equals(data.clickedButton)) {
            boolean isReportValid = checkIfReportValid(player, data);

            if(isReportValid) {
                player.getPageManager().setPage(ref, store, Page.None);
            }
        }
    }

    private boolean checkIfReportValid(Player player, PlayerReportData data) {

        if(data.targetPlayerName == null || data.dateOfOffense == null || data.description == null) {
            player.sendMessage(LM.getMessage(player.getDisplayName(), LangKey.MAIL_MISSING_FIELD, false));
            return false;
        }


        if(data.targetPlayerName.isEmpty() || data.dateOfOffense.isEmpty() || data.description.isEmpty()) {
            player.sendMessage(LM.getMessage(player.getDisplayName(), LangKey.MAIL_MISSING_FIELD, false));
            return false;
        } else {

            if(!hytaleFoundations.getPlayerDataManager().doesPlayerDataExist(data.targetPlayerName)) {
                player.sendMessage(LM.getMessage(player.getDisplayName(), LangKey.PLAYER_NOT_FOUND_MSG, false, data.targetPlayerName));
                return false;
            }
        }
        return true;
    }
}