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

public class SuffixCommand extends AbstractCommandCollection {
    public SuffixCommand(HytaleFoundations hytaleFoundations) {
        super("suffix", "Argument for all suffix related command");
        this.addAliases("sfx");
        this.requirePermission(Permissions.HFPermissions.AFFIX.getPermission());
        this.addSubCommand(new SuffixAddCommand(hytaleFoundations));
        this.addSubCommand(new SuffixClearCommand(hytaleFoundations));
    }
    public static class SuffixAddCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> targetPlayer;
        private final RequiredArg<String> affixId;
        public SuffixAddCommand(HytaleFoundations hytaleFoundations) {
            super("add", "Add a suffix for the target player.");
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
            affixManager.addSuffixToPlayer(playerRef, targetPlayerName, affixId);
        }
    }
    public static class SuffixClearCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> targetPlayer;
        public SuffixClearCommand(HytaleFoundations hytaleFoundations) {
            super("clear", "Clear suffix of the target");
            this.requirePermission(Permissions.HFPermissions.AFFIX.getPermission());
            this.hytaleFoundations = hytaleFoundations;
            targetPlayer = withRequiredArg("player", "Target whose suffix to clear.", ArgTypes.STRING);
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
            profile.clearActiveSuffix();
        }
    }

}
