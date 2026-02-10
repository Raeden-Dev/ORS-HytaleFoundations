package com.raeden.hytale.modules.utility.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;

import javax.annotation.Nonnull;

public class TitleCommand extends AbstractPlayerCommand {

    private final RequiredArg<String> targetArg;
    private final RequiredArg<String> titleArg;
    private final RequiredArg<String> messageArg;

//    private final OptionalArg<Integer> fadeInArg;
//    private final OptionalArg<Integer> stayArg;
//    private final OptionalArg<Integer> fadeOutArg;


    public TitleCommand(HytaleFoundations plugin) {
        super("title", "Sends a customized title to a player or all players.");

        // Fetch the defaults directly from your JSON config object
        int defaultFadeIn = plugin.getConfigManager().getDefaultConfig().getTitleDefaultFadeIn();
        int defaultStay = plugin.getConfigManager().getDefaultConfig().getTitleDefaultStay();
        int defaultFadeOut = plugin.getConfigManager().getDefaultConfig().getTitleDefaultFadeOut();

        this.targetArg = withRequiredArg("target", "Target player or 'all'", ArgTypes.STRING);
        this.titleArg = withRequiredArg("title", "The main title text (supports & color codes)", ArgTypes.STRING);
        this.messageArg =withRequiredArg("message", "The subtitle message (supports & color codes)", ArgTypes.STRING);

    }

    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

    }


//    @Override
//    protected CompletableFuture<Void> execute(@Nonnull CommandContext commandContext) {
//        String target = this.targetArg.get(commandContext);
//        String mainTitle = this.titleArg.get(commandContext);
//        String subTitle = this.messageArg.get(commandContext);
//
//        int fadeIn = this.fadeInArg.get(commandContext);
//        int stay = this.stayArg.get(commandContext);
//        int fadeOut = this.fadeOutArg.get(commandContext);
//
//
//        Message titleMsg = Message.raw(colorize(mainTitle));
//        Message subtitleMsg = Message.raw(colorize(subTitle));
//
//
//        if (target.equalsIgnoreCase("all")) {
//            HytaleServer.get().getUniverse().getPlayers().forEach(playerRef -> {
//                sendTitleToPlayer(playerRef, titleMsg, subtitleMsg, fadeIn, stay, fadeOut);
//            });
//            commandContext.sendMessage(Message.raw("Title sent to all players.").color(DefaultColors.MC_GREEN.getHex()));
//            return null;
//        }
//
//
//        PlayerRef targetPlayerRef = HytaleServer.get().getUniverse().getPlayerByName(target);
//        if (targetPlayerRef != null && targetPlayerRef.isValid()) {
//            sendTitleToPlayer(targetPlayerRef, titleMsg, subtitleMsg, fadeIn, stay, fadeOut);
//            commandContext.sendMessage(Message.raw("Title sent to " + target).color(DefaultColors.MC_GREEN.getHex()));
//        } else {
//            commandContext.sendMessage(Message.raw("Failed to send: Player '" + target + "' not found!").color(DefaultColors.MC_RED.getHex()));
//        }
//        return null;
//    }
//
//    private void sendTitleToPlayer(PlayerRef player, Message title, Message subtitle, int fadeIn, int stay, int fadeOut) {
//        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
//    }
//
//
//    private String colorize(String text) {
//        return text.replace("&", "§");
//    }
}