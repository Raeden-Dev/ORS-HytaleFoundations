package com.raeden.hytale.core.commands;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.lang.LangKey;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

import static com.raeden.hytale.HytaleFoundations.ERROR_LOG_DIRECTORY;
import static com.raeden.hytale.HytaleFoundations.langManager;
import static com.raeden.hytale.utils.FileManager.logError;

public class ReloadPluginCommand extends AbstractAsyncCommand {
    private final HytaleFoundations hytaleFoundations;
    public ReloadPluginCommand(HytaleFoundations hytaleFoundations) {
        super("reload", "Reloads all configs of the plugin");
        this.addAliases("re");
        this.hytaleFoundations = hytaleFoundations;
    }
    @Nonnull
    @Override
    protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext commandContext) {
        try {
            hytaleFoundations.getConfigManager().reloadPlugin();
            if(commandContext.isPlayer()) {
                commandContext.sender().sendMessage(langManager.getMessage(commandContext.sender().getDisplayName(), LangKey.PLUGIN_RELOAD_SUCCESS, false));
            } else {
                commandContext.sender().sendMessage(langManager.getMessage(LangKey.PLUGIN_RELOAD_SUCCESS, true));
            }
        } catch (Exception e) {
            if(commandContext.isPlayer()) {
                commandContext.sender().sendMessage(langManager.getMessage(commandContext.sender().getDisplayName(), LangKey.PLUGIN_RELOAD_FAILURE, false));
            } else {
                commandContext.sender().sendMessage(langManager.getMessage(LangKey.PLUGIN_RELOAD_FAILURE, true));
            }
            logError(ERROR_LOG_DIRECTORY, "ReloadPluginCommand", e);
        }
        return CompletableFuture.completedFuture(null);
    }
}
