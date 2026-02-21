package com.raeden.hytale.modules.chat.commands.affix;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.utils.Permissions;

import javax.annotation.Nonnull;

public class AffixCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;
    public AffixCommand(HytaleFoundations hytaleFoundations) {
        super("affix", "Argument for all affix related command");
        this.requirePermission(Permissions.HFPermissions.AFFIX.getPermission());
        this.hytaleFoundations = hytaleFoundations;
        this.addSubCommand(new AffixListCommand(hytaleFoundations));
        this.addSubCommand(new AffixClearCommand(hytaleFoundations));
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

    }
}
