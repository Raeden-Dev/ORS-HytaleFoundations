package com.raeden.hytale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.raeden.hytale.core.commands.EssentialsCommand;
import com.raeden.hytale.core.config.ConfigManager;
import com.raeden.hytale.core.data.PlayerDataManager;
import com.raeden.hytale.core.events.playerEvents.PlayerDeathListener;
import com.raeden.hytale.core.events.playerEvents.PlayerServerDisconnectListener;
import com.raeden.hytale.core.events.playerEvents.PlayerServerJoinListener;
import com.raeden.hytale.lang.LangManager;
import com.raeden.hytale.modules.admin.commands.AnnounceCommand;
import com.raeden.hytale.modules.admin.commands.TitleCommand;
import com.raeden.hytale.modules.admin.commands.VanishCommand;
import com.raeden.hytale.modules.analytics.pluginactions.PluginActionManager;
import com.raeden.hytale.modules.chat.ChatManager;
import com.raeden.hytale.modules.chat.commands.ClearChatCommand;
import com.raeden.hytale.modules.chat.commands.MessagePlayerCommand;
import com.raeden.hytale.modules.chat.commands.ReplyPlayerCommand;
import com.raeden.hytale.modules.chat.events.PlayerChatListener;
import com.raeden.hytale.modules.utility.commands.PlayerInfoCommand;
import com.raeden.hytale.utils.Scheduler;

import javax.annotation.Nonnull;

public class HytaleEssentials extends JavaPlugin {
    public static final HytaleLogger myLogger = HytaleLogger.forEnclosingClass();
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    private Scheduler scheduler;
    private PluginActionManager pluginActionManager;

    private ConfigManager configManager;
    public static LangManager langManager;
    private PlayerDataManager playerDataManager;
    private ChatManager chatManager;

    public HytaleEssentials(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        myLogger.atInfo().log("Hytale Essentials loaded!");
        registerCommands();
        registerListeners();
        registerManagers();
    }

    protected void shutdown() {
        myLogger.atInfo().log("Hytale Essentials is shutting down...");

        if(scheduler != null) {
            scheduler.shutdown();
        }
    }

    private void registerManagers() {
        scheduler = new Scheduler(this);
        pluginActionManager = new PluginActionManager(this);

        langManager = new LangManager(this);
        configManager = new ConfigManager(this);
        configManager.loadConfigs();
        playerDataManager = new PlayerDataManager(this);
        chatManager = new ChatManager(this, scheduler);
    }

    private void registerListeners() {
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, playerReadyEvent -> {
            PlayerServerJoinListener.onPlayerJoin(playerReadyEvent, this);
        });
        this.getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, playerDisconnectEvent -> {
            PlayerServerDisconnectListener.onPlayerDisconnect(playerDisconnectEvent, this);
        });
        this.getEventRegistry().registerGlobal(PlayerChatEvent.class, playerChatEvent -> {
            PlayerChatListener.onPlayerChat(playerChatEvent, this);
        });

        PlayerDeathListener PlayerDeathListener = new PlayerDeathListener(this);
    }

    private void registerCommands() {
        this.getCommandRegistry().registerCommand(new EssentialsCommand(this));
        this.getCommandRegistry().registerCommand(new PlayerInfoCommand());
        this.getCommandRegistry().registerCommand(new AnnounceCommand(this));
        this.getCommandRegistry().registerCommand(new TitleCommand(this));
        this.getCommandRegistry().registerCommand(new ClearChatCommand(this));
        this.getCommandRegistry().registerCommand(new VanishCommand(this));
        this.getCommandRegistry().registerCommand(new ClearChatCommand(this));
        this.getCommandRegistry().registerCommand(new MessagePlayerCommand(this));
        this.getCommandRegistry().registerCommand(new ReplyPlayerCommand(this));
    }

    public ConfigManager getConfigManager() {return configManager;}
    public LangManager getLangManager() {return langManager;}
    public PlayerDataManager getPlayerDataManager() {return playerDataManager;}
    public ChatManager getChatManager() {return chatManager;}
    public PluginActionManager getPluginActionManager() {return pluginActionManager;}
}
