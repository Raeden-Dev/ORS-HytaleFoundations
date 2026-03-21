package com.raeden.hytale.core.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.pages.HFMainMenu;
import com.raeden.hytale.core.permission.Permissions;
import com.raeden.hytale.core.lang.LangKey;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.raeden.hytale.HytaleFoundations.ERROR_LOG_DIRECTORY;
import static com.raeden.hytale.HytaleFoundations.LM;
import static com.raeden.hytale.utils.FileUtils.logError;

public class CoreCommand extends AbstractCommandCollection {
    public CoreCommand(HytaleFoundations hytaleFoundations) {
        super("foundation", "Argument for all Hytale Foundations core commandd.");
        this.requirePermission(Permissions.ACCESS.getPermission());
        this.addAliases("hf","fd");

        this.addSubCommand(new PluginMenuCommand());
        this.addSubCommand(new ReloadPluginCommand(hytaleFoundations));
        this.addSubCommand(new UpdatePluginCommand(hytaleFoundations));
        this.addSubCommand(new DebugCommand(hytaleFoundations));
        this.addSubCommand(new TestPlayerCommand(hytaleFoundations));
        this.addSubCommand(new PlayerDataCommand(hytaleFoundations));
        this.addSubCommand(new LangCommand(hytaleFoundations));
        this.addSubCommand(new HFHelpCommand(hytaleFoundations));
        this.addSubCommand(new PermissionCommand(hytaleFoundations));
    }

    private static class HFHelpCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        public HFHelpCommand(HytaleFoundations hytaleFoundations) {
            super("help", "Enquire about how to use Hytale Foundations");
            this.requirePermission(Permissions.ACCESS.getPermission());
            this.hytaleFoundations = hytaleFoundations;
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            List<String> messageToSendPlayer = new ArrayList<>(List.of(
                    "&7---------- &r&e&l[&r&b&lHYTALE FOUNDATIONS&r&e&l] &r&7----------",
                    "&a&lCredits: &r&e&lOne Raid Studio",
                    "&a&lDiscord: &r&e&l<LINK>",
                    "&a&lSupport us: &r&e&l<LINK",
                    "&7------------------------------------------",
                    "&e&lCommands:",
                    "&71. &r&e&l/hf menu &r&a: Opens Hytale Foundations management menu.",
                    "&72. &r&e&l/hf debug &r&a: Toggles debug mode",
                    "&73. &r&e&l/hf reload &r&a: Reloads all HF configs.",
                    "&74. &r&e&l/hf reload <name> &r&a: Reloads a specific HF config.",
                    "&75. &r&e&l/hf playerdata save <target>|saveall|reload <target>|reloadall &r&a: Saves data of all online players.",
                    "&76. &r&e&l/hf colors &r&a: Shows all available color codes."
            ));

            for(String msg : messageToSendPlayer) {
                if(commandContext.isPlayer()) {
                    commandContext.sender().sendMessage(hytaleFoundations.getChatManager().getColorEngine().parseText(msg));
                }
            }
        }
    }

    private static class PluginMenuCommand extends AbstractPlayerCommand {
        public PluginMenuCommand() {
            super("menu", "Opens Hytale Foundations Main Menu.", false);
            this.addAliases("m", "mn");
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            Player player = store.getComponent(ref, Player.getComponentType());
            if (player == null) return;
            HFMainMenu menu = new HFMainMenu(playerRef);
            player.getPageManager().openCustomPage(ref, store, menu);
        }
    }

    private static class DebugCommand extends AbstractAsyncCommand {
        private final HytaleFoundations hytaleFoundations;
        public DebugCommand(HytaleFoundations hytaleFoundations) {
            super("debug", "Toggle debug mode on/off for Hytale Foundations");
            this.addAliases("dbug");
            this.hytaleFoundations = hytaleFoundations;
        }
        @Nonnull
        @Override
        protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext commandContext) {
            try {
                hytaleFoundations.getConfigManager().getDefaultConfig().setDebugMode(!hytaleFoundations.getConfigManager().getDefaultConfig().isDebugMode());
                String debugStr = hytaleFoundations.getConfigManager().getDefaultConfig().isDebugMode() ? "On":"Off";
                commandContext.sender().sendMessage(LM.getAbstractMessage(commandContext, LangKey.DEBUG_MODE, debugStr));
            } catch (Exception e) {
                logError(ERROR_LOG_DIRECTORY, "DebugCommand", e);
            }
            return CompletableFuture.completedFuture(null);
        }
    }

    private static class UpdatePluginCommand extends AbstractAsyncCommand {
        private final HytaleFoundations hytaleFoundations;
        public UpdatePluginCommand(HytaleFoundations hytaleFoundations) {
            super("update", "Updates all configs of the plugin");
            this.addAliases("upd");
            this.hytaleFoundations = hytaleFoundations;
        }
        @Nonnull
        @Override
        protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext commandContext) {
            hytaleFoundations.getConfigManager().updateConfigs();
            return CompletableFuture.completedFuture(null);
        }
    }

    private static class PlayerDataCommand extends AbstractCommandCollection {
        public PlayerDataCommand(HytaleFoundations hytaleFoundations) {
            super("playerdata", "Argument for all player data related commands.");
            this.addAliases("pdata");
            this.addSubCommand(new SaveAllDataCommand(hytaleFoundations));
            this.addSubCommand(new SaveTargetDataCommand(hytaleFoundations));
            this.addSubCommand(new ReloadAllDataCommand(hytaleFoundations));
            this.addSubCommand(new ReloadTargetDataCommand(hytaleFoundations));
        }
        public static class SaveAllDataCommand extends AbstractAsyncCommand {
            private final HytaleFoundations hytaleFoundations;
            public SaveAllDataCommand(HytaleFoundations hytaleFoundations) {
                super("saveall","Save data of all players active in the server.");
                this.addAliases("sall");
                this.hytaleFoundations = hytaleFoundations;
            }
            @Nonnull
            @Override
            protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext commandContext) {
                try {
                    hytaleFoundations.getPlayerDataManager().saveAllPlayerData();
                    commandContext.sender().sendMessage(LM.getAbstractMessage(commandContext, LangKey.SAVE_SUCCESS, "all player data"));
                } catch (Exception e) {
                    commandContext.sender().sendMessage(LM.getAbstractMessage(commandContext, LangKey.SAVE_FAILURE, "all player data"));
                    logError(ERROR_LOG_DIRECTORY, "SaveAllDataCommand", e);
                }
                return CompletableFuture.completedFuture(null);
            }
        }
        public static class SaveTargetDataCommand extends AbstractAsyncCommand {
            private final HytaleFoundations hytaleFoundations;
            private final RequiredArg<String> targetPlayer;
            public SaveTargetDataCommand(HytaleFoundations hytaleFoundations) {
                super("save","Save data of target player active in the server.");
                this.hytaleFoundations = hytaleFoundations;
                this.targetPlayer = withRequiredArg("Target", "Target player whose data will be saved.", ArgTypes.STRING);
            }
            @Nonnull
            @Override
            protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext commandContext) {
                try {
                    if(!hytaleFoundations.getPlayerDataManager().doesPlayerExist(commandContext.get(this.targetPlayer))) {
                        commandContext.sender().sendMessage(LM.getAbstractMessage(commandContext, LangKey.PLAYER_NOT_FOUND, commandContext.get(this.targetPlayer)));
                        return CompletableFuture.completedFuture(null);
                    }
                    hytaleFoundations.getPlayerDataManager().saveTargetPlayerData(commandContext.get(this.targetPlayer));
                    commandContext.sender().sendMessage(LM.getAbstractMessage(commandContext, LangKey.SAVE_SUCCESS, "data of player: " + commandContext.get(this.targetPlayer)));
                } catch (Exception e) {
                    if(commandContext.isPlayer()) {
                        commandContext.sender().sendMessage(LM.getPlayerMessage(commandContext.sender().getDisplayName(), LangKey.SAVE_FAILURE, "data of player: " + commandContext.get(this.targetPlayer)));
                    } else {
                        commandContext.sender().sendMessage(LM.getConsoleMessage(LangKey.SAVE_FAILURE, "data of player: " + commandContext.get(this.targetPlayer)));
                    }
                    logError(ERROR_LOG_DIRECTORY, "SaveTargetDataCommand", e);
                }
                return CompletableFuture.completedFuture(null);
            }
        }
        public static class ReloadAllDataCommand extends AbstractAsyncCommand {
            private final HytaleFoundations hytaleFoundations;
            public ReloadAllDataCommand(HytaleFoundations hytaleFoundations) {
                super("reloadall","Reload data of all players active in the server.");
                this.addAliases("rall");
                this.hytaleFoundations = hytaleFoundations;
            }
            @Nonnull
            @Override
            protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext commandContext) {
                try {
                    hytaleFoundations.getPlayerDataManager().reloadAllPlayerData();
                    if(commandContext.isPlayer()) {
                        commandContext.sender().sendMessage(LM.getPlayerMessage(commandContext.sender().getDisplayName(), LangKey.RELOAD_SUCCESS, "all player data"));
                    } else {
                        commandContext.sender().sendMessage(LM.getConsoleMessage(LangKey.RELOAD_SUCCESS, "all player data"));
                    }
                } catch (Exception e) {
                    if(commandContext.isPlayer()) {
                        commandContext.sender().sendMessage(LM.getPlayerMessage(commandContext.sender().getDisplayName(), LangKey.RELOAD_FAILURE, "all player data"));
                    } else {
                        commandContext.sender().sendMessage(LM.getConsoleMessage(LangKey.RELOAD_FAILURE, "all player data"));
                    }
                    logError(ERROR_LOG_DIRECTORY, "ReloadAllDataCommand", e);
                }
                return CompletableFuture.completedFuture(null);
            }
        }
        public static class ReloadTargetDataCommand extends AbstractAsyncCommand {
            private final HytaleFoundations hytaleFoundations;
            private final RequiredArg<String> targetPlayer;
            public ReloadTargetDataCommand(HytaleFoundations hytaleFoundations) {
                super("reload","Reload data of target player active in the server.");
                this.hytaleFoundations = hytaleFoundations;
                this.targetPlayer = withRequiredArg("Target", "Target player whose data will be reloaded.", ArgTypes.STRING);
            }
            @Nonnull
            @Override
            protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext commandContext) {
                try {
                    if(!hytaleFoundations.getPlayerDataManager().doesPlayerExist(commandContext.get(this.targetPlayer))) {
                        if(commandContext.isPlayer()) commandContext.sender().sendMessage(LM.getPlayerMessage(commandContext.sender().getDisplayName(), LangKey.PLAYER_NOT_FOUND_MSG, commandContext.get(this.targetPlayer)));
                        else commandContext.sender().sendMessage(LM.getConsoleMessage(LangKey.PLAYER_NOT_FOUND, commandContext.get(this.targetPlayer)));
                        return CompletableFuture.completedFuture(null);
                    }
                    hytaleFoundations.getPlayerDataManager().reloadTargetPlayerData(commandContext.get(this.targetPlayer));
                    if(commandContext.isPlayer()) {
                        commandContext.sender().sendMessage(LM.getPlayerMessage(commandContext.sender().getDisplayName(), LangKey.RELOAD_PLAYER_DATA, commandContext.get(this.targetPlayer)));
                    }
                } catch (Exception e) {
                    if(commandContext.isPlayer()) {
                        commandContext.sender().sendMessage(LM.getPlayerMessage(commandContext.sender().getDisplayName(), LangKey.RELOAD_FAILURE, "data of player: " + commandContext.get(this.targetPlayer)));
                    } else {
                        commandContext.sender().sendMessage(LM.getConsoleMessage(LangKey.RELOAD_FAILURE, "data of player: " + commandContext.get(this.targetPlayer)));
                    }
                    logError(ERROR_LOG_DIRECTORY, "ReloadTargetDataCommand", e);
                }
                return CompletableFuture.completedFuture(null);
            }
        }
    }

    private static class LangCommand extends AbstractCommandCollection {
        public LangCommand(HytaleFoundations hytaleFoundations) {
            super("lang", "Argument for all language related commands.");
            this.addSubCommand(new ReloadLangCommand(hytaleFoundations));
        }
        private static class ReloadLangCommand extends AbstractAsyncCommand {
            private final HytaleFoundations hytaleFoundations;
            public ReloadLangCommand(HytaleFoundations hytaleFoundations) {
                super("reload", "Reloads all language of the plugin");
                this.addAliases("re");
                this.hytaleFoundations = hytaleFoundations;
            }
            @Nonnull
            @Override
            protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext commandContext) {
                try {
                    hytaleFoundations.getLangManager().reloadLanguages();
                    if(commandContext.isPlayer()) {
                        commandContext.sender().sendMessage(LM.getPlayerMessage(commandContext.sender().getDisplayName(), LangKey.RELOAD_SUCCESS, "Languages"));
                    } else {
                        commandContext.sender().sendMessage(LM.getConsoleMessage(LangKey.RELOAD_SUCCESS, "Languages"));
                    }
                } catch (Exception e) {
                    if(commandContext.isPlayer()) {
                        commandContext.sender().sendMessage(LM.getPlayerMessage(commandContext.sender().getDisplayName(), LangKey.RELOAD_FAILURE, "Languages"));
                    } else {
                        commandContext.sender().sendMessage(LM.getConsoleMessage(LangKey.RELOAD_FAILURE, "Languages"));
                    }
                    logError(ERROR_LOG_DIRECTORY, "ReloadLangCommand", e);
                }
                return CompletableFuture.completedFuture(null);
            }
        }
    }

    private static class ReloadPluginCommand extends AbstractAsyncCommand {
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
                hytaleFoundations.registerManagers();
                //hytaleFoundations.registerCommands();
                if(commandContext.isPlayer()) {
                    commandContext.sender().sendMessage(LM.getPlayerMessage(commandContext.sender().getDisplayName(), LangKey.RELOAD_SUCCESS, "Hytale Foundations"));
                } else {
                    commandContext.sender().sendMessage(LM.getConsoleMessage(LangKey.RELOAD_SUCCESS, "Hytale Foundations"));
                }
            } catch (Exception e) {
                if(commandContext.isPlayer()) {
                    commandContext.sender().sendMessage(LM.getPlayerMessage(commandContext.sender().getDisplayName(), LangKey.RELOAD_FAILURE, "Hytale Foundations"));
                } else {
                    commandContext.sender().sendMessage(LM.getConsoleMessage(LangKey.RELOAD_FAILURE, "Hytale Foundations"));
                }
                logError(ERROR_LOG_DIRECTORY, "ReloadPluginCommand", e);
            }
            return CompletableFuture.completedFuture(null);
        }
    }

    // This class can be used to execute any test function or experimental feature
    private static class TestPlayerCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        public TestPlayerCommand(HytaleFoundations hytaleFoundations) {
            super("test", "Test any experiment feature. (DEV COMMAND)");
            this.hytaleFoundations = hytaleFoundations;
            this.requirePermission(Permissions.ADMIN.getPermission());
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            System.out.println("[TEST-COMMAND] Testing --> Nothing for now!");

        }
    }
}
