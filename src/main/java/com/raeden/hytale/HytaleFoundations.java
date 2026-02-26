package com.raeden.hytale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.core.commands.CoreCommand;
import com.raeden.hytale.core.config.ConfigManager;
import com.raeden.hytale.core.player.PlayerDataManager;
import com.raeden.hytale.core.events.playerEvents.PlayerDeathListener;
import com.raeden.hytale.core.events.playerEvents.PlayerServerDisconnectListener;
import com.raeden.hytale.core.events.playerEvents.PlayerServerJoinListener;
import com.raeden.hytale.lang.LangManager;
import com.raeden.hytale.modules.admin.commands.AnnounceCommand;
import com.raeden.hytale.modules.admin.commands.TitleCommand;
import com.raeden.hytale.modules.admin.commands.VanishCommand;
import com.raeden.hytale.modules.analytics.pluginactions.PluginActionManager;
import com.raeden.hytale.modules.chat.ChatManager;
import com.raeden.hytale.modules.chat.commands.AffixCommand;
import com.raeden.hytale.modules.chat.commands.PrefixCommand;
import com.raeden.hytale.modules.chat.commands.SuffixCommand;
import com.raeden.hytale.modules.chat.commands.NicknameCommand;
import com.raeden.hytale.modules.mail.MailManager;
import com.raeden.hytale.modules.chat.commands.*;
import com.raeden.hytale.modules.chat.events.PlayerChatListener;
import com.raeden.hytale.modules.mail.commands.MailCommand;
import com.raeden.hytale.modules.rank.RankManager;
import com.raeden.hytale.modules.rank.commands.RankCommand;
import com.raeden.hytale.modules.utility.commands.*;
import com.raeden.hytale.utils.Scheduler;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Random;
import java.util.Scanner;

public class HytaleFoundations extends JavaPlugin {
    public static final HytaleLogger myLogger = HytaleLogger.forEnclosingClass();
    public static final Random random = new Random();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static Path ERROR_LOG_DIRECTORY;

    private Scheduler scheduler;
    private PluginActionManager pluginActionManager;
    private ConfigManager configManager;
    public static LangManager langManager;
    private PlayerDataManager playerDataManager;

    private ChatManager chatManager;
    private MailManager mailManager;
    private RankManager rankManager;

    public HytaleFoundations(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        myLogger.atInfo().log("Hytale Foundations loading...");
        ERROR_LOG_DIRECTORY = this.getDataDirectory().resolve("logs").resolve("error_logs");
        registerManagers();
        registerCommands();
        registerListeners();
    }

    @Override
    protected void start() {
        myLogger.atInfo().log("Hytale Foundations loaded!");
    }

    protected void shutdown() {
        myLogger.atInfo().log("Hytale Foundations is shutting down...");

        if(scheduler != null) {
            scheduler.shutdown();
        }
    }

    public void registerManagers() {
        // Main dependencies
        if(langManager == null) langManager = new LangManager(this);
        if(configManager == null) configManager = new ConfigManager(this);
        langManager.setDefaultLanguage();
        if(scheduler == null) scheduler = new Scheduler(this);
        if(pluginActionManager == null) pluginActionManager = new PluginActionManager(this);
        if(playerDataManager == null) playerDataManager = new PlayerDataManager(this);

        if(configManager.getDefaultConfig().isToggleChatModule()) {
            if(chatManager == null) chatManager = new ChatManager(this, scheduler);
        } else {
            if(chatManager != null) chatManager = null;
        }
        if(configManager.getDefaultConfig().isToggleMailModule()) {
            if(mailManager == null) mailManager = new MailManager(this);
        } else {
            if(mailManager != null) mailManager = null;
        }
        if(configManager.getDefaultConfig().isToggleRankModule()) {
            if(rankManager == null) rankManager = new RankManager(this);
        } else {
            if(rankManager != null) rankManager = null;
        }
    }

    private void registerListeners() {
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, playerReadyEvent -> {
            PlayerServerJoinListener.onPlayerJoin(playerReadyEvent, this);
        });
        this.getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, playerDisconnectEvent -> {
            PlayerServerDisconnectListener.onPlayerDisconnect(playerDisconnectEvent, this);
        });

        if(configManager.getDefaultConfig().isToggleChatModule()) {
            this.getEventRegistry().registerGlobal(PlayerChatEvent.class, playerChatEvent -> {
                PlayerChatListener.onPlayerChat(playerChatEvent, this);
            });
        }

        PlayerDeathListener PlayerDeathListener = new PlayerDeathListener(this);
        EntityStore.REGISTRY.registerSystem(PlayerDeathListener);
    }

    public void registerCommands() {
        this.getCommandRegistry().registerCommand(new CoreCommand(this));

        if(configManager.getDefaultConfig().isToggleAdminModule()) {
            this.getCommandRegistry().registerCommand(new AnnounceCommand(this));
            this.getCommandRegistry().registerCommand(new TitleCommand(this));
            this.getCommandRegistry().registerCommand(new VanishCommand(this));
        }
        if(configManager.getDefaultConfig().isToggleChatModule()) {
            this.getCommandRegistry().registerCommand(new ClearChatCommand(this));
            this.getCommandRegistry().registerCommand(new MessagePlayerCommand(this));
            this.getCommandRegistry().registerCommand(new ReplyPlayerCommand(this));
            this.getCommandRegistry().registerCommand(new BlockPlayerCommand(this));
            this.getCommandRegistry().registerCommand(new UnblockPlayerCommand(this));
            this.getCommandRegistry().registerCommand(new MutePlayerCommand(this));
            this.getCommandRegistry().registerCommand(new UnmutePlayerCommand(this));
            this.getCommandRegistry().registerCommand(new IngorePlayerCommand(this));
            this.getCommandRegistry().registerCommand(new MailCommand(this));
            this.getCommandRegistry().registerCommand(new NicknameCommand(this));
            this.getCommandRegistry().registerCommand(new AffixCommand(this));
            this.getCommandRegistry().registerCommand(new PrefixCommand(this));
            this.getCommandRegistry().registerCommand(new SuffixCommand(this));
        }
        // Admin UI
        if(configManager.getDefaultConfig().isToggleHomesModule()) {
            this.getCommandRegistry().registerCommand(new HomesCommand(this));
        }
        if(configManager.getDefaultConfig().isToggleRankModule()) {
            this.getCommandRegistry().registerCommand(new RankCommand(this));
        }
        // Utility Commands
        this.getCommandRegistry().registerCommand(new PlayerInfoCommand());
        this.getCommandRegistry().registerCommand(new PlaytimeCommand(this));
        this.getCommandRegistry().registerCommand(new AnvilCommand());

    }

    public ConfigManager getConfigManager() {return configManager;}
    public LangManager getLangManager() {return langManager;}
    public PlayerDataManager getPlayerDataManager() {return playerDataManager;}
    public ChatManager getChatManager() {return chatManager;}
    public PluginActionManager getPluginActionManager() {return pluginActionManager;}
    public MailManager getMailManager() {return mailManager;}
    public RankManager getRankManager() {return rankManager;}
}
