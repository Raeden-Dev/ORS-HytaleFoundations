package com.raeden.hytale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.raeden.hytale.core.commands.CoreCommand;
import com.raeden.hytale.core.config.ConfigManager;
import com.raeden.hytale.core.data.PlayerDataManager;
import com.raeden.hytale.core.events.playerEvents.PlayerDeathListener;
import com.raeden.hytale.core.events.playerEvents.PlayerServerDisconnectListener;
import com.raeden.hytale.core.events.playerEvents.PlayerServerJoinListener;
import com.raeden.hytale.lang.LangManager;
import com.raeden.hytale.modules.admin.commands.AnnounceCommand;
import com.raeden.hytale.modules.admin.commands.PlayerDatabaseCommand;
import com.raeden.hytale.modules.admin.commands.TitleCommand;
import com.raeden.hytale.modules.admin.commands.VanishCommand;
import com.raeden.hytale.modules.analytics.pluginactions.PluginActionManager;
import com.raeden.hytale.modules.chat.ChatManager;
import com.raeden.hytale.modules.chat.MailManager;
import com.raeden.hytale.modules.chat.commands.*;
import com.raeden.hytale.modules.chat.events.PlayerChatListener;
import com.raeden.hytale.modules.utility.commands.AnvilCommand;
import com.raeden.hytale.modules.utility.commands.PlayerInfoCommand;
import com.raeden.hytale.modules.utility.commands.PlaytimeCommand;
import com.raeden.hytale.utils.Scheduler;

import javax.annotation.Nonnull;

public class HytaleFoundations extends JavaPlugin {
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
    private MailManager mailManager;

    public HytaleFoundations(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        myLogger.atInfo().log("Hytale Foundations loaded!");
        registerManagers();
        registerCommands();
        registerListeners();
    }

    protected void shutdown() {
        myLogger.atInfo().log("Hytale Foundations is shutting down...");

        if(scheduler != null) {
            scheduler.shutdown();
        }
    }

    private void registerManagers() {
        langManager = new LangManager(this);
        configManager = new ConfigManager(this);
        langManager.setDefaultLanguage();

        scheduler = new Scheduler(this);
        pluginActionManager = new PluginActionManager(this);

        playerDataManager = new PlayerDataManager(this);
        chatManager = new ChatManager(this, scheduler);
        mailManager = new MailManager(this);
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
        this.getCommandRegistry().registerCommand(new CoreCommand(this));
        this.getCommandRegistry().registerCommand(new AnnounceCommand(this));
        this.getCommandRegistry().registerCommand(new TitleCommand(this));
        this.getCommandRegistry().registerCommand(new ClearChatCommand(this));
        this.getCommandRegistry().registerCommand(new VanishCommand(this));
        this.getCommandRegistry().registerCommand(new ClearChatCommand(this));
        this.getCommandRegistry().registerCommand(new MessagePlayerCommand(this));
        this.getCommandRegistry().registerCommand(new ReplyPlayerCommand(this));
        this.getCommandRegistry().registerCommand(new BlockPlayerCommand(this));
        this.getCommandRegistry().registerCommand(new UnblockPlayerCommand(this));
        this.getCommandRegistry().registerCommand(new MutePlayerCommand(this));
        this.getCommandRegistry().registerCommand(new UnmutePlayerCommand(this));
        this.getCommandRegistry().registerCommand(new IngorePlayerCommand(this));
        this.getCommandRegistry().registerCommand(new MailCommand(this));

        // Admin UI
        this.getCommandRegistry().registerCommand(new PlayerDatabaseCommand(this));

        // Utility Commands
        this.getCommandRegistry().registerCommand(new PlayerInfoCommand());
        this.getCommandRegistry().registerCommand(new TitleCommand(this));
        this.getCommandRegistry().registerCommand(new PlaytimeCommand(this));
        this.getCommandRegistry().registerCommand(new AnvilCommand());

    }

    public ConfigManager getConfigManager() {return configManager;}
    public LangManager getLangManager() {return langManager;}
    public PlayerDataManager getPlayerDataManager() {return playerDataManager;}
    public ChatManager getChatManager() {return chatManager;}
    public PluginActionManager getPluginActionManager() {return pluginActionManager;}
    public MailManager getMailManager() {return mailManager;}
}
