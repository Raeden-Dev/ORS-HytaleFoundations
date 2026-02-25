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
import com.raeden.hytale.core.player.PlayerProfile;
import com.raeden.hytale.core.utils.Permissions;
import com.raeden.hytale.lang.LangKey;
import com.raeden.hytale.modules.chat.AffixManager;

import javax.annotation.Nonnull;

import static com.raeden.hytale.HytaleFoundations.langManager;

public class PrefixCommand extends AbstractCommandCollection {
    public PrefixCommand(HytaleFoundations hytaleFoundations) {
        super("prefix", "Argument for all affix related command");
        this.addAliases("pfx");
        this.requirePermission(Permissions.HFPermissions.AFFIX.getPermission());
        this.addSubCommand(new PrefixAddCommand(hytaleFoundations));
        this.addSubCommand(new PrefixClearCommand(hytaleFoundations));
    }
    public static class PrefixAddCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> targetPlayer;
        private final RequiredArg<String> affixId;
        public PrefixAddCommand(HytaleFoundations hytaleFoundations) {
            super("add", "Add a prefix for target player");
            this.requirePermission(Permissions.HFPermissions.AFFIX.getPermission());
            this.hytaleFoundations = hytaleFoundations;
            targetPlayer = withRequiredArg("Player", "The target player", ArgTypes.STRING);
            affixId = withRequiredArg("Affix ID", "ID of the affix to set for target", ArgTypes.STRING);
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String senderUsername = commandContext.sender().getDisplayName();
            String targetPlayerName = commandContext.get(this.targetPlayer);
            String affixId = commandContext.get(this.affixId);
            PlayerProfile profile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(targetPlayerName);
            AffixManager affixManager = hytaleFoundations.getChatManager().getAffixManager();
            if(profile == null) {
                commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.PLAYER_NOT_FOUND_MSG,false, targetPlayerName));
                return;
            }
            if(!affixManager.doesAffixExists(affixId)) {
                commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.AFFIX_NOT_FOUND,false, affixId));
                return;
            }
            commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.AFFIX_ADD_SUCCESS, false, affixManager.getAffixDisplay(affixId), targetPlayerName));
            affixManager.addPrefixToPlayer(playerRef, targetPlayerName, affixId, false);
        }
    }
    public static class PrefixClearCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> targetPlayer;
        public PrefixClearCommand(HytaleFoundations hytaleFoundations) {
            super("clear", "Clear prefix of the target");
            this.requirePermission(Permissions.HFPermissions.AFFIX.getPermission());
            this.hytaleFoundations = hytaleFoundations;
            targetPlayer = withRequiredArg("player", "Target whose prefix to clear.", ArgTypes.STRING);
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String senderUsername = commandContext.sender().getDisplayName();
            String targetPlayerName = commandContext.get(this.targetPlayer);
            PlayerProfile profile = hytaleFoundations.getPlayerDataManager().getPlayerProfile(targetPlayerName);
            if(profile == null) {
                commandContext.sender().sendMessage(langManager.getMessage(senderUsername, LangKey.PLAYER_NOT_FOUND_MSG,false, targetPlayerName));
                return;
            }
            profile.clearActivePrefix();
        }
    }

}
