package com.raeden.hytale.modules.rank.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;

import javax.annotation.Nonnull;

public class RankCommand extends AbstractCommandCollection {
    private final HytaleFoundations hytaleFoundations;
    public RankCommand(HytaleFoundations hytaleFoundations) {
        super("rank", "Agrument for all rank related commands.");
        this.hytaleFoundations = hytaleFoundations;
    }
    public class RankSetCommand extends AbstractPlayerCommand {
        public RankSetCommand() {
            super("set", "Set the rank of a target player.");
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

        }
    }
    public class RankListCommand extends AbstractPlayerCommand {
        public RankListCommand() {
            super("list", "Set the rank of a target player.");
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

        }
    }
    public class RankPromoteCommand extends AbstractPlayerCommand {
        public RankPromoteCommand() {
            super("set", "Set the rank of a target player.");
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

        }
    }
    public class RankDemoteCommand extends AbstractPlayerCommand {
        public RankDemoteCommand() {
            super("list", "Set the rank of a target player.");
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

        }
    }
    public class RankChainCommands extends AbstractCommandCollection {

        public RankChainCommands() {
            super("chain", "Sub-Argument for rank chain related commands.");
        }
        public class RankChainCreateCommand extends AbstractPlayerCommand {
            public RankChainCreateCommand() {
                super("create", "Create an empty rank chain.");
            }
            @Override
            protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

            }
        }
    }

}
