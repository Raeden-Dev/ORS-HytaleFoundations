package com.raeden.hytale.core.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.pages.HFMainMenu;
import com.raeden.hytale.core.utils.Permissions;
import com.raeden.hytale.lang.LangKey;
import com.raeden.hytale.lang.LangManager;
import com.raeden.hytale.modules.chat.AffixManager;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.raeden.hytale.HytaleFoundations.ERROR_LOG_DIRECTORY;
import static com.raeden.hytale.HytaleFoundations.langManager;
import static com.raeden.hytale.utils.FileManager.logError;

public class CoreCommand extends AbstractCommandCollection {
    public CoreCommand(HytaleFoundations hytaleFoundations) {
        super("foundation", "Argument for all Hytale Foundations Command.");
        this.requirePermission(Permissions.HFPermissions.ACCESS.getPermission());
        this.addAliases("hf", "fd");
        this.addSubCommand(new PluginMenuCommand());
        this.addSubCommand(new ReloadPluginCommand(hytaleFoundations));
        this.addSubCommand(new UpdatePluginCommand(hytaleFoundations));
        this.addSubCommand(new DebugCommand(hytaleFoundations));
        this.addSubCommand(new TestPlayerCommand(hytaleFoundations));
        this.addSubCommand(new LangCommand(hytaleFoundations));
        this.addSubCommand(new HFHelpCommand(hytaleFoundations));
    }

    private static class HFHelpCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        public HFHelpCommand(HytaleFoundations hytaleFoundations) {
            super("help", "Enquire about how to use Hytale Foundations");
            this.requirePermission(Permissions.HFPermissions.ACCESS.getPermission());
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
                    "&75. &r&e&l/hf save playerdata &r&a: Saves data of all online players.",
                    "&76. &r&e&l/hf colors &r&a: Shows all available color codes."
            ));
            if(hytaleFoundations.getConfigManager().getDefaultConfig().isToggleDebug()) {
                messageToSendPlayer.add("&75. &r&e&l/hf save playerdata &r&a: Saves data of all online players.");
            }
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
                hytaleFoundations.getConfigManager().getDefaultConfig().setToggleDebug(!hytaleFoundations.getConfigManager().getDefaultConfig().isToggleDebug());
                String debugStr = hytaleFoundations.getConfigManager().getDefaultConfig().isToggleDebug() ? "On":"Off";
                if(commandContext.isPlayer()) {
                    commandContext.sender().sendMessage(langManager.getMessage(commandContext.sender().getDisplayName(), LangKey.DEBUG_MODE, false, debugStr));
                } else {
                    commandContext.sender().sendMessage(langManager.getMessage(LangKey.DEBUG_MODE, true, debugStr));
                }
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
                        commandContext.sender().sendMessage(langManager.getMessage(commandContext.sender().getDisplayName(), LangKey.PLUGIN_RELOAD_SUCCESS, false, "Languages"));
                    } else {
                        commandContext.sender().sendMessage(langManager.getMessage(LangKey.PLUGIN_RELOAD_SUCCESS, true, "Languages"));
                    }
                } catch (Exception e) {
                    if(commandContext.isPlayer()) {
                        commandContext.sender().sendMessage(langManager.getMessage(commandContext.sender().getDisplayName(), LangKey.PLUGIN_RELOAD_FAILURE, false, "Languages"));
                    } else {
                        commandContext.sender().sendMessage(langManager.getMessage(LangKey.PLUGIN_RELOAD_FAILURE, true, "Languages"));
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
                if(commandContext.isPlayer()) {
                    commandContext.sender().sendMessage(langManager.getMessage(commandContext.sender().getDisplayName(), LangKey.PLUGIN_RELOAD_SUCCESS, false, "Hytale Foundations"));
                } else {
                    commandContext.sender().sendMessage(langManager.getMessage(LangKey.PLUGIN_RELOAD_SUCCESS, true, "Hytale Foundations"));
                }
            } catch (Exception e) {
                if(commandContext.isPlayer()) {
                    commandContext.sender().sendMessage(langManager.getMessage(commandContext.sender().getDisplayName(), LangKey.PLUGIN_RELOAD_FAILURE, false, "Hytale Foundations"));
                } else {
                    commandContext.sender().sendMessage(langManager.getMessage(LangKey.PLUGIN_RELOAD_FAILURE, true, "Hytale Foundations"));
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
            this.requirePermission(Permissions.HFPermissions.ADMIN.getPermission());
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            System.out.println("[TEST-COMMAND] Testing --> Nothing for now!");

        }
    }
}
