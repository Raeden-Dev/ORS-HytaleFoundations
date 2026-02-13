package com.raeden.hytale.core.commands;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.raeden.hytale.HytaleFoundations;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class ReloadPluginCommand extends AbstractAsyncCommand {
    private final HytaleFoundations hytaleFoundations;
    public ReloadPluginCommand(HytaleFoundations hytaleFoundations) {
        super("reload", "Reloads all configs of the plugin");
        this.hytaleFoundations = hytaleFoundations;
    }
    @Nonnull
    @Override
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext commandContext) {
        hytaleFoundations.getConfigManager().loadConfigs();
        return CompletableFuture.completedFuture(null);
    }
}
