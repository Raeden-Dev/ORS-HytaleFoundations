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
import com.raeden.hytale.core.lang.LangKey;
import com.raeden.hytale.modules.mail.MailManager;
import com.raeden.hytale.utils.TimeUtils;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

import static com.raeden.hytale.HytaleFoundations.LM;

public class SendMailPage extends InteractiveCustomUIPage<SendMailPage.SendMailData> {
    private final HytaleFoundations hytaleFoundations;
    public String CANCEL_BUTTON_ID = "CancelButton";
    public String SEND_BUTTON_ID = "SendButton";
    public String GIFT_BUTTON_ID = "GiftButton";

    public static class SendMailData {
        public String receiverName;
        public String mailSubject;
        public String mailBody;
        public List<String> mailGifts;
        public String clickedButton;
        public static final BuilderCodec<SendMailData> CODEC =
                BuilderCodec.builder(SendMailData.class, SendMailData::new)
                        .append(new KeyedCodec<>("@ReceiverName", Codec.STRING), (SendMailData obj, String val) -> obj.receiverName = val, (SendMailData obj) -> obj.receiverName).add()
                        .append(new KeyedCodec<>("@MailSubject", Codec.STRING), (SendMailData obj, String val) -> obj.mailSubject = val, (SendMailData obj) -> obj.mailSubject).add()
                        .append(new KeyedCodec<>("@MailBody", Codec.STRING), (SendMailData obj, String val) -> obj.mailBody = val, (SendMailData obj) -> obj.mailBody).add()
                        .append(new KeyedCodec<>("@MailGifts", Codec.STRING_ARRAY), (SendMailData obj, String[] val) -> obj.mailGifts = Arrays.asList(val), (SendMailData obj) -> obj.mailGifts.toArray(new String[0])).add()
                        .append(new KeyedCodec<>("ClickedButton", Codec.STRING), (SendMailData obj, String val) -> obj.clickedButton = val, (SendMailData obj) -> obj.clickedButton).add()
                        .build();
    }
    public SendMailPage(Object hytaleFoundations, PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, SendMailData.CODEC);
        this.hytaleFoundations = hytaleFoundations;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/HytaleFoundations_SendMail.ui");
        uiEventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#SendButton",
                new EventData()
                .append("@ReceiverName", "#ToInput.Value")
                .append("@MailSubject","#SubjectInput.Value")
                .append("@MailBody","#MessageInput.Value")
                .append("ClickedButton", SEND_BUTTON_ID)
                // Skipping Gifts now since its more complicated implement
        );
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#CancelButton", new EventData().append("ClickedButton", CANCEL_BUTTON_ID));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#GiftButton", new EventData().append("ClickedButton", GIFT_BUTTON_ID));
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull SendMailData data) {
        Player player = store.getComponent(ref, Player.getComponentType());
        if(player == null) return;

        if(CANCEL_BUTTON_ID.equals(data.clickedButton)) {
            player.getPageManager().setPage(ref, store, Page.None);
            return;
        }

        if(GIFT_BUTTON_ID.equals(data.clickedButton)) {
            return;
        }

        if(SEND_BUTTON_ID.equals(data.clickedButton)) {
            boolean isMailValid = checkIfMailValid(player, data);
            if(isMailValid) {
                MailManager mailManager = hytaleFoundations.getMailManager();
                MailManager.Mail mail = new MailManager.Mail(
                        player.getDisplayName(),
                        data.receiverName,
                        data.mailSubject,
                        TimeUtils.getTimeNow(),
                        data.mailBody
                );
                mailManager.sendMailToPlayer(playerRef, data.receiverName, mail);
            }
            player.getPageManager().setPage(ref, store, Page.None);
        }
    }

    private boolean checkIfMailValid(Player player, SendMailData data) {
        if(data.receiverName == null || data.mailBody == null || data.mailSubject == null) {
            player.sendMessage(LM.getPlayerMessage(player.getDisplayName(), LangKey.MAIL_MISSING_FIELD));
            return false;
        }

        if(data.receiverName.isEmpty() || data.mailSubject.isEmpty() || data.mailBody.isEmpty()) {
            player.sendMessage(LM.getPlayerMessage(player.getDisplayName(), LangKey.MAIL_MISSING_FIELD));
            return false;
        } else {
            if(!hytaleFoundations.getPlayerDataManager().doesPlayerDataExist(data.receiverName)) {
                player.sendMessage(LM.getPlayerMessage(player.getDisplayName(), LangKey.PLAYER_NOT_FOUND_MSG, data.receiverName));
                return false;
            }
        }
        return true;
    }

}
