package com.raeden.hytale;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.raeden.hytale.modules.utility.commands.PlayerInfoCommand;

import javax.annotation.Nonnull;

public class HytaleEssentials extends JavaPlugin {
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public HytaleEssentials(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("[One Raid Studio] Hytale Essentials loaded!");
        registerCommands();
        registerEvents();
    }

    private void registerEvents() {
        this.getCommandRegistry().registerCommand(new PlayerInfoCommand());
    }

    private void registerCommands() {

    }
}
