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
import com.raeden.hytale.modules.utility.commands.HomesCommand;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CoreCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;

    public CoreCommand(HytaleFoundations hytaleFoundations) {
        super("foundation", "Argument for all Hytale Foundations Command.");
        this.hytaleFoundations = hytaleFoundations;
        this.requirePermission(Permissions.HFPermissions.ACCESS.getPermission());
        this.addAliases("hf", "fd");
        this.addSubCommand(new PluginMenuCommand());
        this.addSubCommand(new ReloadPluginCommand(hytaleFoundations));
        this.addSubCommand(new UpdatePluginCommand(hytaleFoundations));
        this.addSubCommand(new TestPlayerCommand(hytaleFoundations));
    }

    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        List<String> messageToSendPlayer = new ArrayList<>(List.of(
                "&7---------- &r&e&l[&r&b&lHYTALE FOUNDATIONS&r&e&l] &r&7----------",
                "&a&lCredits: &r&e&lOne Raid Studio",
                "&a&lDiscord: &r&e&l<LINK>",
                "&a&lSupport us: &r&e&l<LINK",
                "&7------------------------------------------",
                "&e&lCommands:",
                "&71. &r&e&l/hf menu &r&a: Opens Hytale Foundations management menu.",
                "&72. &r&e&l/hf reload &r&a: Reloads all HF configs.",
                "&73. &r&e&l/hf reload <name> &r&a: Reloads a specific HF config."
        ));
        for(String msg : messageToSendPlayer) {
            if(commandContext.isPlayer()) {
                commandContext.sender().sendMessage(hytaleFoundations.getChatManager().getColorEngine().parseText(msg));
            }
        }

    }

}
