package com.raeden.hytale.modules.chatcontrol.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleEssentials;

import javax.annotation.Nonnull;

public class ClearChatCommand extends AbstractPlayerCommand {
    private final HytaleEssentials hytaleEssentials;

    public ClearChatCommand(HytaleEssentials hytaleEssentials) {
        super("clearchat", "Clears certain or all messages in chat");
        this.addAliases("cchat","clrc","chatclear","purgec");
        this.hytaleEssentials = hytaleEssentials;
    }

    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

    }
}
