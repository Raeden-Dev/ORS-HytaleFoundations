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
import com.hypixel.hytale.server.core.ui.Anchor;
import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.lang.LangKey;
import com.raeden.hytale.modules.admin.ReportManager;
import com.raeden.hytale.utils.TimeUtils;

import javax.annotation.Nonnull;
import java.util.Objects;

import static com.raeden.hytale.HytaleFoundations.LM;
import static com.raeden.hytale.utils.FileUtils.logError;

public class PlayerReportPage extends InteractiveCustomUIPage<PlayerReportPage.ReportIssueData> {

    private final HytaleFoundations hytaleFoundations;

    public static final String CANCEL_BUTTON_ID = "CancelButton";
    public static final String REPORT_BUTTON_ID = "ReportButton";
    public static final String TODAY_BUTTON_ID = "TodayButton";
    public static final String FORGOT_BUTTON_ID = "ForgotButton";
    public static final String ISSUE_TYPE_CHANGED_ID = "IssueTypeChanged";

    private String currentIssueType = "NONE";
    private String currentDateText = "";

    public static class ReportIssueData {
        public String issueType;
        public String dateOfEvent;
        public String description;
        public String clickedButton;

        public String playerName;
        public String playerAbuseType;
        public String staffName;
        public String staffAbuseType;
        public String severity;
        public String triggeredWhen;
        public String transactionID;
        public String activePlayers;
        public String purchaseProblem;
        public String otherContext;

        public static final BuilderCodec<ReportIssueData> CODEC =
                BuilderCodec.builder(ReportIssueData.class, ReportIssueData::new)
                        .append(new KeyedCodec<>("@IssueType", Codec.STRING), (obj, val) -> obj.issueType = val, obj -> obj.issueType).add()
                        .append(new KeyedCodec<>("@DateOfEvent", Codec.STRING), (obj, val) -> obj.dateOfEvent = val, obj -> obj.dateOfEvent).add()
                        .append(new KeyedCodec<>("@Description", Codec.STRING), (obj, val) -> obj.description = val, obj -> obj.description).add()
                        .append(new KeyedCodec<>("ClickedButton", Codec.STRING), (obj, val) -> obj.clickedButton = val, obj -> obj.clickedButton).add()
                        .append(new KeyedCodec<>("@PlayerName", Codec.STRING), (obj, val) -> obj.playerName = val, obj -> obj.playerName).add()
                        .append(new KeyedCodec<>("@PlayerAbuseType", Codec.STRING), (obj, val) -> obj.playerAbuseType = val, obj -> obj.playerAbuseType).add()
                        .append(new KeyedCodec<>("@StaffName", Codec.STRING), (obj, val) -> obj.staffName = val, obj -> obj.staffName).add()
                        .append(new KeyedCodec<>("@StaffAbuseType", Codec.STRING), (obj, val) -> obj.staffAbuseType = val, obj -> obj.staffAbuseType).add()
                        .append(new KeyedCodec<>("@BugSeverity", Codec.STRING), (obj, val) -> obj.severity = val, obj -> obj.severity).add()
                        .append(new KeyedCodec<>("@TriggeredWhen", Codec.STRING), (obj, val) -> obj.triggeredWhen = val, obj -> obj.triggeredWhen).add()
                        .append(new KeyedCodec<>("@TransactionID", Codec.STRING), (obj, val) -> obj.transactionID = val, obj -> obj.transactionID).add()
                        .append(new KeyedCodec<>("@ActivePlayers", Codec.STRING), (obj, val) -> obj.activePlayers = val, obj -> obj.activePlayers).add()
                        .append(new KeyedCodec<>("@PurchaseProblem", Codec.STRING), (obj, val) -> obj.purchaseProblem = val, obj -> obj.purchaseProblem).add()
                        .append(new KeyedCodec<>("@OtherContext", Codec.STRING), (obj, val) -> obj.otherContext = val, obj -> obj.otherContext).add()
                        .build();
    }

    public PlayerReportPage(HytaleFoundations hytaleFoundations, PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, ReportIssueData.CODEC);
        this.hytaleFoundations = hytaleFoundations;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/Reporting/HF_ReportIssue_MainMenu.ui");
        uiCommandBuilder.append("#PlaceholderContainer", "Pages/Reporting/HF_ReportIssue_Blank.ui");
        uiCommandBuilder.append("#PlayerAbuseContainer", "Pages/Reporting/HF_ReportIssue_PlayerAbuse.ui");
        uiCommandBuilder.append("#StaffAbuseContainer", "Pages/Reporting/HF_ReportIssue_StaffAbuse.ui");
        uiCommandBuilder.append("#BugContainer", "Pages/Reporting/HF_ReportIssue_Bug.ui");
        uiCommandBuilder.append("#GlitchContainer", "Pages/Reporting/HF_ReportIssue_Glitch.ui");
        uiCommandBuilder.append("#ServerLagContainer", "Pages/Reporting/HF_ReportIssue_ServerLag.ui");
        uiCommandBuilder.append("#PurchaseContainer", "Pages/Reporting/HF_ReportIssue_Purchase.ui");
        uiCommandBuilder.append("#OtherContainer", "Pages/Reporting/HF_ReportIssue_Other.ui");

        applyIssueType(this.currentIssueType, uiCommandBuilder);

        uiEventBuilder.addEventBinding(
                CustomUIEventBindingType.ValueChanged,
                "#IssueTypeDropdown",
                new EventData()
                        .append("ClickedButton", ISSUE_TYPE_CHANGED_ID)
                        .append("@IssueType", "#IssueTypeDropdown.Value")
        );

        uiEventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#ReportButton",
                new EventData()
                        .append("ClickedButton", REPORT_BUTTON_ID)
                        .append("@IssueType", "#IssueTypeDropdown.Value")
                        .append("@DateOfEvent", "#DateInput.Value")
                        .append("@Description", "#DescriptionInput.Value")
                        .append("@PlayerName", "#PlayerNameInput.Value")
                        .append("@PlayerAbuseType", "#PlayerAbuseDropdown.Value")
                        .append("@TransactionID", "#TransactionID.Value")
                        .append("@StaffName", "#StaffNameInput.Value")
                        .append("@StaffAbuseType", "#StaffAbuseDropdown.Value")
                        .append("@BugSeverity", "#BugSeverityDropdown.Value")
                        .append("@TriggeredWhen", "#BugTriggerInput.Value")
                        .append("@OtherContext", "#OtherContextInput.Value")
        );

        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#CancelButton", new EventData().append("ClickedButton", CANCEL_BUTTON_ID));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#TodayButton", new EventData().append("ClickedButton", TODAY_BUTTON_ID));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ForgotButton", new EventData().append("ClickedButton", FORGOT_BUTTON_ID));
    }

    private void applyIssueType(String issueType, UICommandBuilder existingBuilder) {
        UICommandBuilder builder = existingBuilder != null ? existingBuilder : new UICommandBuilder();

        boolean isFormActive = !issueType.equals("NONE");

        builder.set("#PlaceholderPage.Visible", !isFormActive);
        builder.set("#SharedFormElements.Visible", isFormActive);

        builder.set("#PlayerAbusePage.Visible", issueType.equals("PLAYER_ABUSE"));
        builder.set("#StaffAbusePage.Visible", issueType.equals("STAFF_ABUSE"));
        builder.set("#BugPage.Visible", issueType.equals("BUG"));
        builder.set("#GlitchPage.Visible", issueType.equals("GLITCH"));
        builder.set("#ServerLagPage.Visible", issueType.equals("SERVER_LAG"));
        builder.set("#PurchasePage.Visible", issueType.equals("PURCHASE"));
        builder.set("#OtherPage.Visible", issueType.equals("OTHER"));

        Anchor newAnchor = getAnchor(issueType, isFormActive);
        builder.setObject("#MainContainer.Anchor", newAnchor);

        if (existingBuilder == null) {
            this.sendUpdate(builder);
        }
    }

    private static Anchor getAnchor(String issueType, boolean isFormActive) {
        int targetHeight = 300;
        if (isFormActive) {
            targetHeight = switch (issueType) {
                case "PLAYER_ABUSE", "PURCHASE", "STAFF_ABUSE", "BUG", "GLITCH" -> 645;
                case "SERVER_LAG" -> 720;
                case "OTHER" -> 570;
                default -> targetHeight;
            };
        }

        Anchor newAnchor = new Anchor();
        newAnchor.setWidth(Value.of(650));
        newAnchor.setHeight(Value.of(targetHeight));
        return newAnchor;
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull ReportIssueData data) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if(player == null) return;

        if(CANCEL_BUTTON_ID.equals(data.clickedButton)) {
            player.getPageManager().setPage(ref, store, Page.None);
            return;
        }

        if(ISSUE_TYPE_CHANGED_ID.equals(data.clickedButton)) {
            if (data.issueType != null && !data.issueType.equals(currentIssueType)) {
                this.currentIssueType = data.issueType;
                applyIssueType(this.currentIssueType, null);
            }
            return;
        }

        if(TODAY_BUTTON_ID.equals(data.clickedButton)) {
            this.currentDateText = TimeUtils.getCurrentDate();
            UICommandBuilder builder = new UICommandBuilder();
            builder.set("#DateInput.Value", this.currentDateText);
            this.sendUpdate(builder);
            return;
        }

        if(FORGOT_BUTTON_ID.equals(data.clickedButton)) {
            UICommandBuilder builder = new UICommandBuilder();
            builder.set("#DateInput.Value", "Unknown Date");
            this.sendUpdate(builder);
            return;
        }

        if(REPORT_BUTTON_ID.equals(data.clickedButton)) {
            boolean isReportValid = checkIfReportValid(player, data);
            String formattedIssue = data.issueType.toLowerCase().replace("_", " ");
            if(isReportValid) {
                try {
                    ReportManager.IssueContext context;
                    if(data.staffAbuseType != null) {
                        context = ReportManager.IssueContext.fromString(data.staffAbuseType);
                    }
                    else if(data.playerAbuseType != null) {
                        context = ReportManager.IssueContext.fromString(data.playerAbuseType);
                    }
                    else if(data.purchaseProblem != null) {
                        context = ReportManager.IssueContext.fromString(data.purchaseProblem);
                    }
                    else {
                        context = ReportManager.IssueContext.SERVER_PROBLEM;
                    }
                    ReportManager.IssueReport report = new ReportManager.IssueReport
                            (
                                    ReportManager.IssueType.fromString(data.issueType),
                                    context,
                                    data.severity == null ? ReportManager.IssueSeverity.UNNECESSARY : ReportManager.IssueSeverity.fromString(data.severity),
                                    player.getDisplayName(),
                                    TimeUtils.getTimeNow(),
                                    Objects.requireNonNull(player.getWorld()).getName(),
                                    data.playerName == null ? "<Unapplicable>" : data.playerName,
                                    data.dateOfEvent,
                                    data.description
                            );
                    hytaleFoundations.getAdminFunctionsManager().getReportManager().saveIssueReport(report, false);
                    player.sendMessage(LM.getPlayerMessage(player.getDisplayName(), LangKey.REPORT_SEND_SUCCESS, formattedIssue));
                } catch (Exception e) {
                    logError("ReportButtonClick", e);
                    player.sendMessage(LM.getPlayerMessage(player.getDisplayName(), LangKey.REPORT_SEND_FAILURE, formattedIssue));
                }
            }
            player.getPageManager().setPage(ref, store, Page.None);
        }
    }

    private boolean checkIfReportValid(Player player, ReportIssueData data) {
        if(data == null || player == null) return false;
        if((data.description == null || data.description.isEmpty()) || (data.dateOfEvent == null  || data.dateOfEvent.isEmpty())) {
            player.sendMessage(LM.getPlayerMessage(player.getDisplayName(), LangKey.REPORT_MISSING_FIELD));
            return false;
        }
        if(data.issueType.equalsIgnoreCase(ReportManager.IssueType.PLAYER_ABUSE.name())) {
            if(data.playerName == null || data.playerName.isEmpty()) {
                player.sendMessage(LM.getPlayerMessage(player.getDisplayName(), LangKey.REPORT_MISSING_FIELD));
                return false;
            }
        }
        else if(data.issueType.equalsIgnoreCase(ReportManager.IssueType.STAFF_ABUSE.name())) {
            if(data.staffName == null || data.staffName.isEmpty()) {
                player.sendMessage(LM.getPlayerMessage(player.getDisplayName(), LangKey.REPORT_MISSING_FIELD));
                return false;
            }
        }
        else if(data.issueType.equalsIgnoreCase(ReportManager.IssueType.BUG.name())) {
            if(data.triggeredWhen == null || data.triggeredWhen.isEmpty()) {
                player.sendMessage(LM.getPlayerMessage(player.getDisplayName(), LangKey.REPORT_MISSING_FIELD));
                return false;
            }
        }
        else if(data.issueType.equalsIgnoreCase(ReportManager.IssueType.GLITCH.name())) {
            if(data.triggeredWhen == null || data.triggeredWhen.isEmpty()) {
                player.sendMessage(LM.getPlayerMessage(player.getDisplayName(), LangKey.REPORT_MISSING_FIELD));
                return false;
            }
        }
        else if(data.issueType.equalsIgnoreCase(ReportManager.IssueType.SERVER_LAG.name())) {
            if((data.triggeredWhen == null || data.triggeredWhen.isEmpty()) || (data.activePlayers == null  || data.activePlayers.isEmpty())) {
                player.sendMessage(LM.getPlayerMessage(player.getDisplayName(), LangKey.REPORT_MISSING_FIELD));
                return false;
            }
        }
        else if(data.issueType.equalsIgnoreCase(ReportManager.IssueType.PURCHASE.name())) {
            if(data.transactionID == null || data.transactionID.isEmpty()) {
                player.sendMessage(LM.getPlayerMessage(player.getDisplayName(), LangKey.REPORT_MISSING_FIELD));
                return false;
            }
        }
        else if(data.issueType.equalsIgnoreCase(ReportManager.IssueType.OTHER.name())) {
            if(data.otherContext == null || data.otherContext.isEmpty()) {
                player.sendMessage(LM.getPlayerMessage(player.getDisplayName(), LangKey.REPORT_MISSING_FIELD));
                return false;
            }
        }
        return true;
    }
}