package com.raeden.hytale.modules.chat.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleEssentials;

import javax.annotation.Nonnull;

public class MutePlayerCommand extends AbstractPlayerCommand {
    private final HytaleEssentials hytaleEssentials;
    private final RequiredArg<String> targetPlayer;
    private final OptionalArg<Integer> duration;

    public MutePlayerCommand(HytaleEssentials hytaleEssentials) {
        super("mute", "Mutes a player so they can't speak in chat.");
        this.hytaleEssentials = hytaleEssentials;
        targetPlayer = withRequiredArg("Player", "Player to execute command on.", ArgTypes.STRING);
        duration = withOptionalArg("Duration", "Duration of the mute. (D|H|M|S eg. 1d8h5m33s)", ArgTypes.INTEGER);
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

    }
}
