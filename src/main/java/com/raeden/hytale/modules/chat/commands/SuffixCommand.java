package com.raeden.hytale.modules.chat.commands;

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
import com.raeden.hytale.modules.chat.AffixManager;

import javax.annotation.Nonnull;

public class SuffixCommand extends AbstractCommandCollection {
    public SuffixCommand(HytaleFoundations hytaleFoundations) {
        super("suffix", "Argument for all suffix related command");
        this.addAliases("sfx");
        this.requirePermission(PermissionNodes.AFFIX.getPermission());
        this.addSubCommand(new SuffixAddCommand(hytaleFoundations));
        this.addSubCommand(new SuffixForceAddCommand(hytaleFoundations));
        this.addSubCommand(new SuffixRemoveCommand(hytaleFoundations));
        this.addSubCommand(new SuffixClearCommand(hytaleFoundations));
    }
    public static class SuffixAddCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> targetPlayer;
        private final RequiredArg<String> affixId;
        public SuffixAddCommand(HytaleFoundations hytaleFoundations) {
            super("add", "Add a suffix for the target player.");
            this.requirePermission(PermissionNodes.AFFIX.getPermission());
            this.hytaleFoundations = hytaleFoundations;
            targetPlayer = withRequiredArg("Player", "The target player", ArgTypes.STRING);
            affixId = withRequiredArg("Affix ID", "ID of the affix to set for target", ArgTypes.STRING);
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String targetPlayerName = commandContext.get(this.targetPlayer);
            String affixId = commandContext.get(this.affixId);
            AffixManager affixManager = hytaleFoundations.getChatManager().getAffixManager();
            affixManager.addSuffixToPlayer(playerRef, targetPlayerName, affixId, false);
        }
    }
    public static class SuffixForceAddCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> targetPlayer;
        private final RequiredArg<String> affixId;
        public SuffixForceAddCommand(HytaleFoundations hytaleFoundations) {
            super("forceadd", "Force add a suffix for the target player.");
            this.requirePermission(PermissionNodes.AFFIX.getPermission());
            this.hytaleFoundations = hytaleFoundations;
            targetPlayer = withRequiredArg("Player", "The target player", ArgTypes.STRING);
            affixId = withRequiredArg("Affix ID", "ID of the affix to set for target", ArgTypes.STRING);
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String targetPlayerName = commandContext.get(this.targetPlayer);
            String affixId = commandContext.get(this.affixId);
            AffixManager affixManager = hytaleFoundations.getChatManager().getAffixManager();
            affixManager.addSuffixToPlayer(playerRef, targetPlayerName, affixId, true);
        }
    }
    public static class SuffixRemoveCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> targetPlayer;
        private final RequiredArg<String> affixId;
        public SuffixRemoveCommand(HytaleFoundations hytaleFoundations) {
            super("remove", "Remove a suffix for the target player.");
            this.requirePermission(PermissionNodes.AFFIX.getPermission());
            this.hytaleFoundations = hytaleFoundations;
            targetPlayer = withRequiredArg("Player", "The target player", ArgTypes.STRING);
            affixId = withRequiredArg("Affix ID", "ID of the affix to set for target", ArgTypes.STRING);
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String targetPlayerName = commandContext.get(this.targetPlayer);
            String affixId = commandContext.get(this.affixId);
            AffixManager affixManager = hytaleFoundations.getChatManager().getAffixManager();
            affixManager.removeSuffixFromPlayer(playerRef, targetPlayerName, affixId);
        }
    }
    public static class SuffixClearCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> targetPlayer;
        public SuffixClearCommand(HytaleFoundations hytaleFoundations) {
            super("clear", "Clear suffix of the target");
            this.requirePermission(PermissionNodes.AFFIX.getPermission());
            this.hytaleFoundations = hytaleFoundations;
            targetPlayer = withRequiredArg("player", "Target whose suffix to clear.", ArgTypes.STRING);
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String targetPlayerName = commandContext.get(this.targetPlayer);
            AffixManager affixManager = hytaleFoundations.getChatManager().getAffixManager();
            affixManager.removeAllCertainAffixFromPlayer(playerRef, targetPlayerName, false);
        }
    }

}
