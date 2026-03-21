package com.raeden.hytale.modules.rank.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.permission.Permissions;
import com.raeden.hytale.core.lang.LangKey;
import com.raeden.hytale.modules.rank.RankManager;

import javax.annotation.Nonnull;

import java.util.List;
import java.util.Map;

import static com.raeden.hytale.HytaleFoundations.LM;

public class RankCommand extends AbstractCommandCollection {
    public RankCommand(HytaleFoundations hytaleFoundations) {
        super("rank", "Agrument for all rank related commands.");
        this.requirePermission(Permissions.RANK.getPermission());
        this.addSubCommand(new RankSetCommand(hytaleFoundations));
        this.addSubCommand(new RankListCommand(hytaleFoundations));
        this.addSubCommand(new RankGroupListCommand(hytaleFoundations));
        this.addSubCommand(new RankPromoteCommand(hytaleFoundations));
        this.addSubCommand(new RankDemoteCommand(hytaleFoundations));
        this.addSubCommand(new RankGroupCommands(hytaleFoundations));
    }
    public static class RankListCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        public RankListCommand(HytaleFoundations hytaleFoundations) {
            super("list", "Show a list of all available ranks.");
            this.requirePermission(Permissions.RANK_ADMIN.getPermission());
            this.hytaleFoundations = hytaleFoundations;
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            RankManager rankManager = hytaleFoundations.getRankManager();
            String playerUsername = commandContext.sender().getDisplayName();
            if(rankManager == null) return;
            if(rankManager.getRankMap() == null || rankManager.getRankMap().isEmpty()) {
                commandContext.sender().sendMessage(LM.getPlayerMessage(playerUsername, LangKey.NOTHING_FOUND, "rank", "Empty map"));
                return;
            }
            commandContext.sender().sendMessage(LM.getPlayerMessage(playerUsername, LangKey.LIST_CONTEXT, "rank(s)"));
            for(Map.Entry<String, RankManager.Rank> entry : rankManager.getRankMap().entrySet()) {
                String rankPrefix = hytaleFoundations.getChatManager().getAffixManager().getAffixDisplay(entry.getValue().getChatPrefixId());
                commandContext.sender().sendMessage(LM.getPlayerMessage(playerUsername, LangKey.LIST_ITEM,
                        rankPrefix + " &r&e&l[ID: " + entry.getKey() + "]"));
            }
        }
    }
    public static class RankGroupListCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        public RankGroupListCommand(HytaleFoundations hytaleFoundations) {
            super("grouplist", "Show a list of all available rank groups.");
            this.addAliases("glist");
            this.requirePermission(Permissions.RANK_ADMIN.getPermission());
            this.hytaleFoundations = hytaleFoundations;
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            RankManager rankManager = hytaleFoundations.getRankManager();
            String playerUsername = commandContext.sender().getDisplayName();
            if(rankManager == null) return;
            if(rankManager.getRankGroupMap() == null || rankManager.getRankGroupMap().isEmpty()) {
                commandContext.sender().sendMessage(LM.getPlayerMessage(playerUsername, LangKey.NOTHING_FOUND, "rank group", "Empty map"));
                return;
            }
            commandContext.sender().sendMessage(LM.getPlayerMessage(playerUsername, LangKey.LIST_CONTEXT, "rank group(s)"));
            for(Map.Entry<String, List<String>> entry : rankManager.getRankGroupMap().entrySet()) {
                commandContext.sender().sendMessage(LM.getPlayerMessage(playerUsername, LangKey.LIST_ITEM,
                        "&e&l" + entry.getKey() + " &r&7[Chain: " + hytaleFoundations.getRankManager().getRankChainText(entry.getKey()) + "]"));
            }
        }
    }
    public static class RankSetCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> targetPlayer;
        private final RequiredArg<String> rankId;
        public RankSetCommand(HytaleFoundations hytaleFoundations) {
            super("set", "Set the rank of a target player.");
            this.requirePermission(Permissions.RANK_ADMIN.getPermission());
            this.hytaleFoundations = hytaleFoundations;
            targetPlayer = withRequiredArg("target", "Name of the target player.", ArgTypes.STRING);
            rankId = withRequiredArg("rank ID", "ID of the rank that will be set.", ArgTypes.STRING);
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String targetUsername = commandContext.get(this.targetPlayer);
            if(targetUsername == null) {
                commandContext.sender().sendMessage(LM.getPlayerMessage(commandContext.sender().getDisplayName(), LangKey.PLAYER_NOT_FOUND_MSG, commandContext.get(this.targetPlayer)));
                return;
            }
            hytaleFoundations.getRankManager().setRankOfTarget(playerRef, commandContext.get(this.targetPlayer), commandContext.get(this.rankId));
        }
    }
    public static class RankPromoteCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> targetPlayer;
        public RankPromoteCommand(HytaleFoundations hytaleFoundations) {
            super("promote", "Promote the target to the next rank in the rank chain.");
            this.requirePermission(Permissions.RANK_ADMIN.getPermission());
            this.hytaleFoundations = hytaleFoundations;
            targetPlayer = withRequiredArg("target", "Name of the target player.", ArgTypes.STRING);
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String targetUsername = commandContext.get(this.targetPlayer);
            if(targetUsername == null) {
                commandContext.sender().sendMessage(LM.getPlayerMessage(commandContext.sender().getDisplayName(), LangKey.PLAYER_NOT_FOUND_MSG, commandContext.get(this.targetPlayer)));
                return;
            }
            hytaleFoundations.getRankManager().promotePlayer(playerRef, commandContext.get(this.targetPlayer));
        }
    }
    public static class RankDemoteCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> targetPlayer;
        public RankDemoteCommand(HytaleFoundations hytaleFoundations) {
            super("demote", "Set the rank of a target player.");
            this.requirePermission(Permissions.RANK_ADMIN.getPermission());
            this.hytaleFoundations = hytaleFoundations;
            targetPlayer = withRequiredArg("target", "Name of the target player.", ArgTypes.STRING);
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String targetUsername = commandContext.get(this.targetPlayer);
            if(targetUsername == null) {
                commandContext.sender().sendMessage(LM.getPlayerMessage(commandContext.sender().getDisplayName(), LangKey.PLAYER_NOT_FOUND_MSG, commandContext.get(this.targetPlayer)));
                return;
            }
            hytaleFoundations.getRankManager().demotePlayer(playerRef, commandContext.get(this.targetPlayer));
        }
    }
    public static class RankGroupCommands extends AbstractCommandCollection {
        public RankGroupCommands(HytaleFoundations hytaleFoundations) {
            super("group", "Sub-Argument for rank group related commands.");
            this.requirePermission(Permissions.RANK_ADMIN.getPermission());
            this.addSubCommand(new RankGroupCreateCommand(hytaleFoundations));
            this.addSubCommand(new RankGroupShowCommand(hytaleFoundations));
            this.addSubCommand(new RankGroupAppendCommand(hytaleFoundations));
            this.addSubCommand(new RankGroupRemoveCommand(hytaleFoundations));
            this.addSubCommand(new RankGroupMoveCommand(hytaleFoundations));
            this.addSubCommand(new RankGroupDeleteCommand(hytaleFoundations));
        }
        public static class RankGroupCreateCommand extends AbstractPlayerCommand {
            private final HytaleFoundations hytaleFoundations;
            private final RequiredArg<String> groupName;
            private final RequiredArg<List<String>> rankIdList;
            public RankGroupCreateCommand(HytaleFoundations hytaleFoundations) {
                super("create", "Create an empty rank group.");
                this.hytaleFoundations = hytaleFoundations;
                this.setAllowsExtraArguments(true);
                groupName = withRequiredArg("Group Name", "Name of the rank group.", ArgTypes.STRING);
                rankIdList = withListRequiredArg("Rank Ids", "List of all ranks to add to group.", ArgTypes.STRING);
            }
            @Override
            protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
                hytaleFoundations.getRankManager().createRankGroup(playerRef, commandContext.get(this.groupName), commandContext.get(this.rankIdList));
            }
        }
        public static class RankGroupShowCommand extends AbstractPlayerCommand {
            private final HytaleFoundations hytaleFoundations;
            private final RequiredArg<String> groupName;
            public RankGroupShowCommand(HytaleFoundations hytaleFoundations) {
                super("show", "Check out a rank group.");
                this.hytaleFoundations = hytaleFoundations;
                groupName = withRequiredArg("Group Name", "Name of the rank group.", ArgTypes.STRING);
            }
            @Override
            protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
                hytaleFoundations.getRankManager().showRankChain(playerRef, commandContext.get(this.groupName));
            }
        }
        public static class RankGroupAppendCommand extends AbstractPlayerCommand {
            private final HytaleFoundations hytaleFoundations;
            private final RequiredArg<String> groupName;
            private final RequiredArg<String> rankId;
            private final RequiredArg<Integer> position;
            public RankGroupAppendCommand(HytaleFoundations hytaleFoundations) {
                super("append", "Add a new rank to the rank group.");
                this.hytaleFoundations = hytaleFoundations;
                groupName = withRequiredArg("Group Name", "Name of the rank group.", ArgTypes.STRING);
                rankId = withRequiredArg("Rank ID", "Rank to add inside the group.", ArgTypes.STRING);
                position = withRequiredArg("Position", "Position where rank will be added.", ArgTypes.INTEGER);
            }
            @Override
            protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
                hytaleFoundations.getRankManager().addRankToGroup(playerRef, commandContext.get(this.groupName),
                        commandContext.get(this.rankId), commandContext.get(this.position));
            }
        }
        public static class RankGroupRemoveCommand extends AbstractPlayerCommand {
            private final HytaleFoundations hytaleFoundations;
            private final RequiredArg<String> groupName;
            private final RequiredArg<String> rankId;
            public RankGroupRemoveCommand(HytaleFoundations hytaleFoundations) {
                super("remove", "Remove an existing rank from the rank group.");
                this.hytaleFoundations = hytaleFoundations;
                groupName = withRequiredArg("Group Name", "Name of the rank group.", ArgTypes.STRING);
                rankId = withRequiredArg("Rank ID", "Rank to move inside the group.", ArgTypes.STRING);
            }
            @Override
            protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
                hytaleFoundations.getRankManager().removeRankFromGroup(playerRef, commandContext.get(this.groupName),
                        commandContext.get(this.rankId));
            }
        }
        public static class RankGroupMoveCommand extends AbstractPlayerCommand {
            private final HytaleFoundations hytaleFoundations;
            private final RequiredArg<String> groupName;
            private final RequiredArg<String> rankId;
            private final RequiredArg<Integer> position;
            public RankGroupMoveCommand(HytaleFoundations hytaleFoundations) {
                super("move", "Move an existing rank in the rank group.");
                this.hytaleFoundations = hytaleFoundations;
                groupName = withRequiredArg("Group Name", "Name of the rank group.", ArgTypes.STRING);
                rankId = withRequiredArg("Rank ID", "Rank to move inside the group.", ArgTypes.STRING);
                position = withRequiredArg("Position", "Position where rank will be moved.", ArgTypes.INTEGER);
            }
            @Override
            protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
                hytaleFoundations.getRankManager().moveRankInGroup(playerRef, commandContext.get(this.groupName),
                        commandContext.get(this.rankId), commandContext.get(this.position));
            }
        }
        public static class RankGroupDeleteCommand extends AbstractPlayerCommand {
            private final HytaleFoundations hytaleFoundations;
            private final RequiredArg<String> groupName;
            public RankGroupDeleteCommand(HytaleFoundations hytaleFoundations) {
                super("delete", "Move an existing rank in the rank group.");
                this.hytaleFoundations = hytaleFoundations;
                groupName = withRequiredArg("Group Name", "Name of the rank group.", ArgTypes.STRING);
            }
            @Override
            protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
                hytaleFoundations.getRankManager().deleteRankGroup(playerRef, commandContext.get(this.groupName));
            }
        }
    }

}
