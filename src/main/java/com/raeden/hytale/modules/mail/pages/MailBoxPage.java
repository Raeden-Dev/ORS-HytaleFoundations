package com.raeden.hytale.modules.mail.pages;
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
import com.raeden.hytale.modules.mail.MailManager;

import javax.annotation.Nonnull;

public class MailBoxPage extends InteractiveCustomUIPage<MailBoxPage.MailBoxData> {

    private final HytaleFoundations hytaleFoundations;

    public String CLOSE_BUTTON_ID = "CloseButton";
    public String REPLY_BUTTON_ID = "ReplyButton";
    public String DELETE_BUTTON_ID = "DeleteButton";
    public String MARK_UNREAD_BUTTON_ID = "MarkUnreadButton";
    public String COLLECT_BUTTON_ID = "CollectButton";
    public String DELETE_ALL_BUTTON_ID = "DeleteAllButton";


    public static class MailBoxData {
        public String clickedButton;
        public String selectedMailId;

        public static final BuilderCodec<MailBoxData> CODEC =
                BuilderCodec.builder(MailBoxData.class, MailBoxData::new)
                        .append(new KeyedCodec<>("ClickedButton", Codec.STRING),
                                (obj, val) -> obj.clickedButton = val,
                                obj -> obj.clickedButton).add()

                        .append(new KeyedCodec<>("@SelectedMailId", Codec.STRING),
                                (obj, val) -> obj.selectedMailId = val,
                                obj -> obj.selectedMailId).add()

                        .build();
    }


    public MailBoxPage(HytaleFoundations hytaleFoundations, PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, MailBoxData.CODEC);
        this.hytaleFoundations = hytaleFoundations;
    }


    @Override
    public void build(@Nonnull Ref<EntityStore> ref,
                      @Nonnull UICommandBuilder uiCommandBuilder,
                      @Nonnull UIEventBuilder uiEventBuilder,
                      @Nonnull Store<EntityStore> store) {

        uiCommandBuilder.append("Pages/HF_Mailbox.ui");


        uiEventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#CloseButton",
                new EventData().append("ClickedButton", CLOSE_BUTTON_ID)
        );


        uiEventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#ReplyButton",
                new EventData().append("ClickedButton", REPLY_BUTTON_ID)
                        .append("@SelectedMailId", "#MailList.Selected.Value")
        );


        uiEventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#DeleteButton",
                new EventData().append("ClickedButton", DELETE_BUTTON_ID)
                        .append("@SelectedMailId", "#MailList.Selected.Value")
        );


        uiEventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#MarkUnreadButton",
                new EventData().append("ClickedButton", MARK_UNREAD_BUTTON_ID)
                        .append("@SelectedMailId", "#MailList.Selected.Value")
        );


        uiEventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#CollectButton",
                new EventData().append("ClickedButton", COLLECT_BUTTON_ID)
                        .append("@SelectedMailId", "#MailList.Selected.Value")
        );


        uiEventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#DeleteAllButton",
                new EventData().append("ClickedButton", DELETE_ALL_BUTTON_ID)
        );
    }


    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref,
                                @Nonnull Store<EntityStore> store,
                                @Nonnull MailBoxData data) {

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        MailManager mailManager = hytaleFoundations.getMailManager();


        if (CLOSE_BUTTON_ID.equals(data.clickedButton)) {
            player.getPageManager().setPage(ref, store, Page.None);
            return;
        }


        if (DELETE_ALL_BUTTON_ID.equals(data.clickedButton)) {
            mailManager.deleteAllMails(playerRef);
            return;
        }


        if (data.selectedMailId == null) return;


        if (DELETE_BUTTON_ID.equals(data.clickedButton)) {
            mailManager.deleteMail(playerRef, data.selectedMailId);
            return;
        }


        if (MARK_UNREAD_BUTTON_ID.equals(data.clickedButton)) {
            mailManager.markAsUnread(playerRef, data.selectedMailId);
            return;
        }

        // COLLECT ATTACHMENTS
        if (COLLECT_BUTTON_ID.equals(data.clickedButton)) {
            mailManager.collectAttachments(playerRef, data.selectedMailId);
            return;
        }
        if (REPLY_BUTTON_ID.equals(data.clickedButton)) {
            String receiver = mailManager.getSenderFromMail(playerRef, data.selectedMailId);
            SendMailPage page = new SendMailPage(hytaleFoundations, playerRef);
            player.getPageManager().openCustomPage(ref, store, page);
        }
    }
}