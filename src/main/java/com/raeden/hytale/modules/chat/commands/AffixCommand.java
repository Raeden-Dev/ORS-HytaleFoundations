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
import com.raeden.hytale.lang.LangKey;
import com.raeden.hytale.modules.chat.AffixManager;

import javax.annotation.Nonnull;
import java.util.Map;

import static com.raeden.hytale.HytaleFoundations.LM;

public class AffixCommand extends AbstractCommandCollection {
    public AffixCommand(HytaleFoundations hytaleFoundations) {
        super("affix", "Argument for all affix related command");
        this.requirePermission(PermissionNodes.AFFIX.getPermission());
        this.addSubCommand(new AffixListCommand(hytaleFoundations));
        this.addSubCommand(new AffixClearCommand(hytaleFoundations));
    }
    public static class AffixListCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        public AffixListCommand(HytaleFoundations hytaleFoundations) {
            super("list", "Show list of all available affixes");
            this.hytaleFoundations = hytaleFoundations;
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            AffixManager affixManager = hytaleFoundations.getChatManager().getAffixManager();
            if(affixManager == null) return;
            if(affixManager.getAffixMap() == null || affixManager.getAffixMap().isEmpty()) return;
            String playerUsername = commandContext.sender().getDisplayName();
            commandContext.sender().sendMessage(LM.getMessage(playerUsername, LangKey.GENERAL_LIST, false, "affix(es)"));
            for(Map.Entry<String, AffixManager.PlayerAffix> entry : affixManager.getAffixMap().entrySet()) {
                commandContext.sender().sendMessage(LM.getMessage(playerUsername, LangKey.GENERAL_LIST_ITEM, false,
                        entry.getValue().getDisplayText() + " &r&e&l[ID: " + entry.getKey() + "]"));
            }
        }
    }
    public static class AffixClearCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> targetPlayer;
        public AffixClearCommand(HytaleFoundations hytaleFoundations) {
            super("clear", "Clear all affix of the target");
            this.requirePermission(PermissionNodes.AFFIX.getPermission());
            this.hytaleFoundations = hytaleFoundations;
            targetPlayer = withRequiredArg("player", "Target whose affix to clear.", ArgTypes.STRING);
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String targetPlayerName = commandContext.get(this.targetPlayer);
            AffixManager affixManager = hytaleFoundations.getChatManager().getAffixManager();
            affixManager.removeAllAffixFromPlayer(playerRef, targetPlayerName);
        }
    }
}
