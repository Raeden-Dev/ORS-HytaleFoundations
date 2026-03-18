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
import com.raeden.hytale.core.alias.CommandAliasManager;
import com.raeden.hytale.core.commands.CoreCommand;
import com.raeden.hytale.core.config.ConfigManager;
import com.raeden.hytale.core.events.playerEvents.*;
import com.raeden.hytale.core.player.PlayerDataManager;
import com.raeden.hytale.core.utils.PermissionManager;
import com.raeden.hytale.core.lang.LangManager;
import com.raeden.hytale.modules.admin.commands.AnnounceCommand;
import com.raeden.hytale.modules.admin.commands.ReportCommand;
import com.raeden.hytale.modules.admin.commands.TitleCommand;
import com.raeden.hytale.modules.admin.commands.VanishCommand;
import com.raeden.hytale.modules.analytics.pluginactions.PluginActionManager;
import com.raeden.hytale.modules.chat.ChatManager;
import com.raeden.hytale.modules.chat.commands.AffixCommand;
import com.raeden.hytale.modules.chat.commands.PrefixCommand;
import com.raeden.hytale.modules.chat.commands.SuffixCommand;
import com.raeden.hytale.modules.chat.commands.NicknameCommand;
import com.raeden.hytale.modules.home.commands.HomeCommand;
import com.raeden.hytale.modules.home.commands.HomesCommand;
import com.raeden.hytale.modules.mail.MailManager;
import com.raeden.hytale.modules.chat.commands.*;
import com.raeden.hytale.modules.chat.events.PlayerChatListener;
import com.raeden.hytale.modules.mail.commands.MailCommand;
import com.raeden.hytale.modules.utility.commands.NafsmunCommand;
import com.raeden.hytale.modules.rank.RankManager;
import com.raeden.hytale.modules.rank.commands.RankCommand;
import com.raeden.hytale.modules.utility.commands.*;
import com.raeden.hytale.utils.SchedulerUtils;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Random;

public class HytaleFoundations extends JavaPlugin {
    public static final HytaleLogger myLogger = HytaleLogger.forEnclosingClass();
    public static final Random random = new Random();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static Path ERROR_LOG_DIRECTORY;

    private SchedulerUtils schedulerUtils;
    private PluginActionManager pluginActionManager;
    private ConfigManager configManager;
    public static LangManager LM;
    private PlayerDataManager playerDataManager;
    private PermissionManager permissionManager;
    private CommandAliasManager commandAliasManager;

    private PlayerMovementListener playerMovementListener;

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

        if(schedulerUtils != null) {
            schedulerUtils.shutdown();
        }
    }

    public void registerManagers() {
        // Main dependencies
        if(configManager == null) configManager = new ConfigManager(this);
        if(permissionManager == null) permissionManager = new PermissionManager(this);
        if(LM == null) LM = new LangManager(this);
        LM.setDefaultLanguage();
        if(schedulerUtils == null) schedulerUtils = new SchedulerUtils(this);
        if(pluginActionManager == null) pluginActionManager = new PluginActionManager(this);
        if(playerDataManager == null) playerDataManager = new PlayerDataManager(this);
        if(commandAliasManager == null) commandAliasManager = new CommandAliasManager(this, this.getCommandRegistry());

        if(configManager.getDefaultConfig().isToggleChatModule()) {
            if(chatManager == null) chatManager = new ChatManager(this, schedulerUtils);
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

        playerMovementListener = new PlayerMovementListener(this, schedulerUtils);

        PlayerDeathListener deathListener = new PlayerDeathListener(this);
        PlayerBlockBreakListener blockBreakListener = new PlayerBlockBreakListener(this);
        PlayerBlockPlaceListener blockPlaceListener = new PlayerBlockPlaceListener(this);
        PlayerMobKillListener mobKillListener = new PlayerMobKillListener(this);
        PlayerKillListener killListener = new PlayerKillListener(this);
        PlayerDamageListener damageListener = new PlayerDamageListener(this);
        PlayerItemCraftListener craftListener = new PlayerItemCraftListener(this);
        EntityStore.REGISTRY.registerSystem(deathListener);
        EntityStore.REGISTRY.registerSystem(blockBreakListener);
        EntityStore.REGISTRY.registerSystem(blockPlaceListener);
        EntityStore.REGISTRY.registerSystem(mobKillListener);
        EntityStore.REGISTRY.registerSystem(killListener);
        EntityStore.REGISTRY.registerSystem(damageListener);
        EntityStore.REGISTRY.registerSystem(craftListener);
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
        this.getCommandRegistry().registerCommand(new PlayerInfoCommand(this));
        this.getCommandRegistry().registerCommand(new PlaytimeCommand(this));

        this.getCommandRegistry().registerCommand(new HomesCommand(this));
        this.getCommandRegistry().registerCommand(new HomeCommand(this));
        this.getCommandRegistry().registerCommand(new ReportCommand(this));

        this.getCommandRegistry().registerCommand(new AnvilCommand());
        this.getCommandRegistry().registerCommand(new NafsmunCommand());
    }

    public ConfigManager getConfigManager() {return configManager;}
    public LangManager getLangManager() {return LM;}
    public PlayerDataManager getPlayerDataManager() {return playerDataManager;}
    public PermissionManager getPermissionManager() {return permissionManager;}
    public CommandAliasManager getCommandManager() {return commandAliasManager;}
    public ChatManager getChatManager() {return chatManager;}
    public PluginActionManager getPluginActionManager() {return pluginActionManager;}
    public MailManager getMailManager() {return mailManager;}
    public RankManager getRankManager() {return rankManager;}

    public PlayerMovementListener getPlayerMovementListener() {return playerMovementListener;}

}
