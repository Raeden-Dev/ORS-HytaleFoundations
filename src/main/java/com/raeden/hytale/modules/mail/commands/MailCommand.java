package com.raeden.hytale.modules.mail.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;

import javax.annotation.Nonnull;

public class MailCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;

    public MailCommand(HytaleFoundations hytaleFoundations) {
        super("mail", "All mail command context.");
        this.hytaleFoundations = hytaleFoundations;
        this.addSubCommand(new SendMailCommand(hytaleFoundations));
        this.addSubCommand(new QuickMailCommand(hytaleFoundations));
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

    }
}
