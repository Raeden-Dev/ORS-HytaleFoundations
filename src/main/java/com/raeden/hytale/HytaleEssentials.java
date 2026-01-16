package com.raeden.hytale;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.raeden.hytale.core.commands.EssentialsCommand;
import com.raeden.hytale.core.config.ConfigManager;
import com.raeden.hytale.lang.LangManager;
import com.raeden.hytale.modules.admin.commands.AnnounceCommand;
import com.raeden.hytale.modules.admin.commands.TitleCommand;
import com.raeden.hytale.modules.admin.commands.VanishCommand;
import com.raeden.hytale.modules.chatcontrol.commands.ClearChatCommand;
import com.raeden.hytale.modules.utility.commands.PlayerInfoCommand;

import javax.annotation.Nonnull;

public class HytaleEssentials extends JavaPlugin {
    public static final HytaleLogger myLogger = HytaleLogger.forEnclosingClass();

    private ConfigManager configManager;
    private LangManager langManager;

    public HytaleEssentials(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        myLogger.atInfo().log("[One Raid Studio] Hytale Essentials loaded!");
        registerCommands();
        registerEvents();
        registerManagers();
    }

    private void registerManagers() {
        configManager = new ConfigManager(this, myLogger);
        configManager.loadConfig();
        langManager = new LangManager(this);
    }

    private void registerEvents() {

    }

    private void registerCommands() {
        this.getCommandRegistry().registerCommand(new EssentialsCommand(this));
        this.getCommandRegistry().registerCommand(new PlayerInfoCommand());
        this.getCommandRegistry().registerCommand(new AnnounceCommand(this));
        this.getCommandRegistry().registerCommand(new TitleCommand(this));
        this.getCommandRegistry().registerCommand(new ClearChatCommand(this));
        this.getCommandRegistry().registerCommand(new VanishCommand(this));
    }

    public ConfigManager getConfigManager() {return configManager;}
}
