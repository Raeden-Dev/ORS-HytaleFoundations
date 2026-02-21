package com.raeden.hytale.modules.chat.commands.affix;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.player.PlayerProfile;
import com.raeden.hytale.core.utils.Permissions;
import com.raeden.hytale.lang.LangKey;

import javax.annotation.Nonnull;

import static com.raeden.hytale.HytaleFoundations.langManager;

public class SuffixClearCommand extends AbstractPlayerCommand {
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
