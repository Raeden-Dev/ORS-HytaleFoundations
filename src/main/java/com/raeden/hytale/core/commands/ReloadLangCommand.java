package com.raeden.hytale.core.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.lang.LangManager;

import javax.annotation.Nonnull;

public class ReloadLangCommand extends AbstractPlayerCommand {
    private final HytaleFoundations hytaleFoundations;
    public ReloadLangCommand(HytaleFoundations hytaleFoundations) {
        super("lang", "Argument for all language related commands.");
        this.hytaleFoundations = hytaleFoundations;
        this.setAllowsExtraArguments(true);

    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        String[] rawMessage = commandContext.getInputString().split("\\s+", 3);
        if(rawMessage.length <= 2) return;
        String actionString = rawMessage[2];
        if(actionString.isEmpty()) return;
        if(actionString.equalsIgnoreCase("reload")) {
            LangManager langManager = hytaleFoundations.getLangManager();
            langManager.reloadLanguages();
        }
    }
}
