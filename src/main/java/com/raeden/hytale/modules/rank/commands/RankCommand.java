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
import com.raeden.hytale.core.utils.Permissions;
import com.raeden.hytale.lang.LangKey;
import com.raeden.hytale.modules.rank.RankManager;

import javax.annotation.Nonnull;

import java.util.Map;

import static com.raeden.hytale.HytaleFoundations.langManager;

public class RankCommand extends AbstractCommandCollection {
    public RankCommand(HytaleFoundations hytaleFoundations) {
        super("rank", "Agrument for all rank related commands.");
        this.requirePermission(Permissions.HFPermissions.RANK.getPermission());
        this.addSubCommand(new RankSetCommand(hytaleFoundations));
        this.addSubCommand(new RankListCommand(hytaleFoundations));
        this.addSubCommand(new RankPromoteCommand(hytaleFoundations));
        this.addSubCommand(new RankDemoteCommand(hytaleFoundations));
        this.addSubCommand(new RankGroupCommands(hytaleFoundations));
    }
    public static class RankSetCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        public RankSetCommand(HytaleFoundations hytaleFoundations) {
            super("set", "Set the rank of a target player.");
            this.requirePermission(Permissions.HFPermissions.RANK_ADMIN.getPermission());
            this.hytaleFoundations = hytaleFoundations;
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

        }
    }
    public static class RankListCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        public RankListCommand(HytaleFoundations hytaleFoundations) {
            super("list", "Show a list of all available ranks.");
            this.requirePermission(Permissions.HFPermissions.RANK_ADMIN.getPermission());
            this.hytaleFoundations = hytaleFoundations;
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            RankManager rankManager = hytaleFoundations.getRankManager();
            if(rankManager == null) return;
            if(rankManager.getRankMap() == null || rankManager.getRankMap().isEmpty()) return;
            String playerUsername = commandContext.sender().getDisplayName();
            commandContext.sender().sendMessage(langManager.getMessage(playerUsername, LangKey.GENERAL_LIST, false, "rank(s)"));
            for(Map.Entry<String, RankManager.Rank> entry : rankManager.getRankMap().entrySet()) {
                String rankPrefix = hytaleFoundations.getChatManager().getAffixManager().getAffixDisplay(entry.getValue().getChatPrefixId());
                commandContext.sender().sendMessage(langManager.getMessage(playerUsername, LangKey.GENERAL_LIST_ITEM, false,
                        rankPrefix + " &r&e&l[ID: " + entry.getKey() + "]"));
            }
        }
    }
    public static class RankPromoteCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        public RankPromoteCommand(HytaleFoundations hytaleFoundations) {
            super("set", "Promote the target to the next rank in the rank chain.");
            this.requirePermission(Permissions.HFPermissions.RANK_ADMIN.getPermission());
            this.hytaleFoundations = hytaleFoundations;
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

        }
    }
    public static class RankDemoteCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        public RankDemoteCommand(HytaleFoundations hytaleFoundations) {
            super("list", "Set the rank of a target player.");
            this.requirePermission(Permissions.HFPermissions.RANK_ADMIN.getPermission());
            this.hytaleFoundations = hytaleFoundations;
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

        }
    }
    public static class RankGroupCommands extends AbstractCommandCollection {
        private final HytaleFoundations hytaleFoundations;
        public RankGroupCommands(HytaleFoundations hytaleFoundations) {
            super("group", "Sub-Argument for rank group related commands.");
            this.hytaleFoundations = hytaleFoundations;
            this.requirePermission(Permissions.HFPermissions.RANK_ADMIN.getPermission());
            this.addSubCommand(new RankGroupCreateCommand(hytaleFoundations));
            this.addSubCommand(new RankGroupShowCommand(hytaleFoundations));
            this.addSubCommand(new RankGroupAppendCommand(hytaleFoundations));
            this.addSubCommand(new RankGroupRemoveCommand(hytaleFoundations));
            this.addSubCommand(new RankGroupDeleteCommand(hytaleFoundations));
        }
        public static class RankGroupCreateCommand extends AbstractPlayerCommand {
            private final HytaleFoundations hytaleFoundations;
            public RankGroupCreateCommand(HytaleFoundations hytaleFoundations) {
                super("create", "Create an empty rank group.");
                this.hytaleFoundations = hytaleFoundations;
            }
            @Override
            protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

            }
        }
        public static class RankGroupShowCommand extends AbstractPlayerCommand {
            private final HytaleFoundations hytaleFoundations;
            public RankGroupShowCommand(HytaleFoundations hytaleFoundations) {
                super("show", "Check out a rank group.");
                this.hytaleFoundations = hytaleFoundations;
            }
            @Override
            protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

            }
        }
        public static class RankGroupAppendCommand extends AbstractPlayerCommand {
            private final HytaleFoundations hytaleFoundations;
            public RankGroupAppendCommand(HytaleFoundations hytaleFoundations) {
                super("append", "Add a new rank to the rank group.");
                this.hytaleFoundations = hytaleFoundations;
            }
            @Override
            protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

            }
        }
        public static class RankGroupRemoveCommand extends AbstractPlayerCommand {
            private final HytaleFoundations hytaleFoundations;
            public RankGroupRemoveCommand(HytaleFoundations hytaleFoundations) {
                super("remove", "Remove an existing rank from the rank group.");
                this.hytaleFoundations = hytaleFoundations;
            }
            @Override
            protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

            }
        }
        public static class RankGroupMoveCommand extends AbstractPlayerCommand {
            private final HytaleFoundations hytaleFoundations;
            public RankGroupMoveCommand(HytaleFoundations hytaleFoundations) {
                super("move", "Move an existing rank in the rank group.");
                this.hytaleFoundations = hytaleFoundations;
            }
            @Override
            protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

            }
        }
        public static class RankGroupDeleteCommand extends AbstractPlayerCommand {
            private final HytaleFoundations hytaleFoundations;
            public RankGroupDeleteCommand(HytaleFoundations hytaleFoundations) {
                super("move", "Move an existing rank in the rank group.");
                this.hytaleFoundations = hytaleFoundations;
            }
            @Override
            protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

            }
        }
    }

}
