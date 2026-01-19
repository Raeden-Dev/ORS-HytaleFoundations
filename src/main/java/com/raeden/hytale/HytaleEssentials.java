package com.raeden.hytale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.raeden.hytale.core.commands.EssentialsCommand;
import com.raeden.hytale.core.config.ConfigManager;
import com.raeden.hytale.core.data.PlayerDataManager;
import com.raeden.hytale.core.events.playerEvents.playerDeathEvent;
import com.raeden.hytale.core.events.playerEvents.playerServerDisconnectEvent;
import com.raeden.hytale.core.events.playerEvents.playerServerJoinEvent;
import com.raeden.hytale.lang.LangManager;
import com.raeden.hytale.modules.admin.commands.AnnounceCommand;
import com.raeden.hytale.modules.admin.commands.TitleCommand;
import com.raeden.hytale.modules.admin.commands.VanishCommand;
import com.raeden.hytale.modules.chatcontrol.commands.ClearChatCommand;
import com.raeden.hytale.modules.utility.commands.PlayerInfoCommand;

import javax.annotation.Nonnull;

public class HytaleEssentials extends JavaPlugin {
    public static final HytaleLogger myLogger = HytaleLogger.forEnclosingClass();
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    private ConfigManager configManager;
    private LangManager langManager;
    private PlayerDataManager playerDataManager;

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
        langManager = new LangManager(this);
        configManager = new ConfigManager(this);
        configManager.loadConfigs();
        playerDataManager = new PlayerDataManager(this);

    }

    private void registerEvents() {
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, playerReadyEvent -> {
            playerServerJoinEvent.onPlayerJoin(playerReadyEvent, this);
        });
        this.getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, playerDisconnectEvent -> {
            playerServerDisconnectEvent.onPlayerDisconnect(playerDisconnectEvent, this);
        });

        playerDeathEvent playerDeathEvent = new playerDeathEvent(this);
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
    public LangManager getLangManager() {return langManager;}

    public PlayerDataManager getPlayerDataManager() {return playerDataManager;}
}
