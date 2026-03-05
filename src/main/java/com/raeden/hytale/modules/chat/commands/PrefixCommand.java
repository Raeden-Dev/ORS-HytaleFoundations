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
import com.raeden.hytale.core.utils.Permissions;
import com.raeden.hytale.modules.chat.AffixManager;

import javax.annotation.Nonnull;

public class PrefixCommand extends AbstractCommandCollection {
    public PrefixCommand(HytaleFoundations hytaleFoundations) {
        super("prefix", "Argument for all affix related command");
        this.addAliases("pfx");
        this.requirePermission(Permissions.AFFIX.getPermission());
        this.addSubCommand(new PrefixAddCommand(hytaleFoundations));
        this.addSubCommand(new PrefixForceAddCommand(hytaleFoundations));
        this.addSubCommand(new PrefixRemoveCommand(hytaleFoundations));
        this.addSubCommand(new PrefixClearCommand(hytaleFoundations));
    }
    public static class PrefixAddCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> targetPlayer;
        private final RequiredArg<String> affixId;
        public PrefixAddCommand(HytaleFoundations hytaleFoundations) {
            super("add", "Add a prefix for target player");
            this.requirePermission(Permissions.AFFIX.getPermission());
            this.hytaleFoundations = hytaleFoundations;
            targetPlayer = withRequiredArg("Player", "The target player", ArgTypes.STRING);
            affixId = withRequiredArg("Affix ID", "ID of the affix to set for target", ArgTypes.STRING);
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String targetPlayerName = commandContext.get(this.targetPlayer);
            String affixId = commandContext.get(this.affixId);
            AffixManager affixManager = hytaleFoundations.getChatManager().getAffixManager();
            affixManager.addPrefixToPlayer(playerRef, targetPlayerName, affixId, false);
        }
    }
    public static class PrefixForceAddCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> targetPlayer;
        private final RequiredArg<String> affixId;
        public PrefixForceAddCommand(HytaleFoundations hytaleFoundations) {
            super("forceadd", "Force add a prefix for target player");
            this.requirePermission(Permissions.AFFIX.getPermission());
            this.hytaleFoundations = hytaleFoundations;
            targetPlayer = withRequiredArg("Player", "The target player", ArgTypes.STRING);
            affixId = withRequiredArg("Affix ID", "ID of the affix to set for target", ArgTypes.STRING);
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String targetPlayerName = commandContext.get(this.targetPlayer);
            String affixId = commandContext.get(this.affixId);
            AffixManager affixManager = hytaleFoundations.getChatManager().getAffixManager();
            affixManager.addPrefixToPlayer(playerRef, targetPlayerName, affixId, true);
        }
    }
    public static class PrefixRemoveCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> targetPlayer;
        private final RequiredArg<String> affixId;
        public PrefixRemoveCommand(HytaleFoundations hytaleFoundations) {
            super("remove", "Remove a prefix for the target player.");
            this.requirePermission(Permissions.AFFIX.getPermission());
            this.hytaleFoundations = hytaleFoundations;
            targetPlayer = withRequiredArg("Player", "The target player", ArgTypes.STRING);
            affixId = withRequiredArg("Affix ID", "ID of the affix to set for target", ArgTypes.STRING);
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String targetPlayerName = commandContext.get(this.targetPlayer);
            String affixId = commandContext.get(this.affixId);
            AffixManager affixManager = hytaleFoundations.getChatManager().getAffixManager();
            affixManager.removePrefixFromPlayer(playerRef, targetPlayerName, affixId);
        }
    }
    public static class PrefixClearCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> targetPlayer;
        public PrefixClearCommand(HytaleFoundations hytaleFoundations) {
            super("clear", "Clear prefix of the target");
            this.requirePermission(Permissions.AFFIX.getPermission());
            this.hytaleFoundations = hytaleFoundations;
            targetPlayer = withRequiredArg("player", "Target whose prefix to clear.", ArgTypes.STRING);
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String targetPlayerName = commandContext.get(this.targetPlayer);
            AffixManager affixManager = hytaleFoundations.getChatManager().getAffixManager();
            affixManager.removeAllCertainAffixFromPlayer(playerRef, targetPlayerName, true);
        }
    }

}
