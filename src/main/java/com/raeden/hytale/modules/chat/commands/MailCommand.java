package com.raeden.hytale.modules.chat.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleEssentials;
import org.bouncycastle.cert.ocsp.Req;

import javax.annotation.Nonnull;

public class MailCommand extends AbstractPlayerCommand {
    private final HytaleEssentials hytaleEssentials;
    private final RequiredArg<String> targetPlayer;
    private final RequiredArg<String> message;

    public MailCommand(HytaleEssentials hytaleEssentials) {
        super("block", "Unblocks a player so they can interact with you again.");
        this.hytaleEssentials = hytaleEssentials;
        targetPlayer = withRequiredArg("Player", "Player to send the mail.", ArgTypes.STRING);
        message = withRequiredArg("Message", "Message to send the player.", ArgTypes.STRING);
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

    }
}
