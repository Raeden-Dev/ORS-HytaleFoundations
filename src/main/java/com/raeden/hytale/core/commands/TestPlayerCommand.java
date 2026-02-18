package com.raeden.hytale.core.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.utils.Permissions;
import com.raeden.hytale.modules.chat.AffixManager;

import javax.annotation.Nonnull;

// This class can be used to execute any test function or experimental feature

public class TestPlayerCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;
    public TestPlayerCommand(HytaleFoundations hytaleFoundations) {
        super("test", "Test any experiment feature. (DEV COMMAND)");
        this.hytaleFoundations = hytaleFoundations;
        this.requirePermission(Permissions.HFPermissions.ADMIN.getPermission());
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        System.out.println("[TEST-COMMAND] Testing --> Affix System and Nickname system");
        String username = commandContext.sender().getDisplayName();
        AffixManager affixManager = hytaleFoundations.getChatManager().getAffixManager();
        affixManager.addPrefixToPlayer(username, "df_op");
        affixManager.addSuffixToPlayer(username, "df_amaze");
    }
}
