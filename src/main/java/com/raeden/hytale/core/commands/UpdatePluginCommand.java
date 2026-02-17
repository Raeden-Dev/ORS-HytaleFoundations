package com.raeden.hytale.core.commands;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.raeden.hytale.HytaleFoundations;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class UpdatePluginCommand extends AbstractAsyncCommand {
    private final HytaleFoundations hytaleFoundations;
    public UpdatePluginCommand(HytaleFoundations hytaleFoundations) {
        super("update", "Updates all configs of the plugin");
        this.addAliases("upd");
        this.hytaleFoundations = hytaleFoundations;
    }
    @Nonnull
    @Override
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext commandContext) {
        hytaleFoundations.getConfigManager().updateConfigs();
        return CompletableFuture.completedFuture(null);
    }
}

