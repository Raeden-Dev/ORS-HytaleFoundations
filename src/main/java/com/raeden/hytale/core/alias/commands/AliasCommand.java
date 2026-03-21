package com.raeden.hytale.core.alias.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.alias.CommandAliasManager;
import com.raeden.hytale.core.lang.LangKey;
import com.raeden.hytale.core.permission.Permissions;

import javax.annotation.Nonnull;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static com.raeden.hytale.HytaleFoundations.LM;
import static com.raeden.hytale.core.config.ConfigManager.COMMAND_FILENAME;
import static com.raeden.hytale.utils.FileUtils.logError;

public class AliasCommand extends AbstractCommandCollection {
    public AliasCommand(HytaleFoundations hytaleFoundations) {
        super("commandmanager", "Argument for all command management related commands.");
        this.addAliases("cm","alias");
        this.requirePermission(Permissions.ADMIN.getPermission());
        this.addSubCommand(new AliasReloadCommand(hytaleFoundations));
        this.addSubCommand(new AliasResetCommand(hytaleFoundations));
        this.addSubCommand(new AliasListCommand(hytaleFoundations));
        this.addSubCommand(new AliasCreateCommand(hytaleFoundations));
        this.addSubCommand(new AliasDeleteCommand(hytaleFoundations));
        this.addSubCommand(new AliasEditCommand(hytaleFoundations));
    }
    private static class AliasReloadCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        public AliasReloadCommand(HytaleFoundations hytaleFoundations) {
            super("reload", "Create a new alias for a existing command.");
            this.addAliases("rld");
            this.hytaleFoundations = hytaleFoundations;
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            CommandAliasManager commandManager = hytaleFoundations.getCommandManager();
            String playerUsername = commandContext.sender().getDisplayName();
            if(commandManager == null) return;
            try {
                commandManager.loadCommands();
                commandContext.sender().sendMessage(LM.getPlayerMessage(playerUsername, LangKey.RELOAD_SUCCESS, COMMAND_FILENAME));
            } catch (Exception e) {
                logError("AliasReloadCommand", e);
                commandContext.sender().sendMessage(LM.getPlayerMessage(playerUsername, LangKey.RELOAD_FAILURE, COMMAND_FILENAME));
            }
        }
    }
    private static class AliasResetCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        public AliasResetCommand(HytaleFoundations hytaleFoundations) {
            super("reset", "Create a new alias for a existing command.", true);
            this.hytaleFoundations = hytaleFoundations;
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            CommandAliasManager commandManager = hytaleFoundations.getCommandManager();
            if(commandManager == null) return;
            try {
                Files.delete(commandManager.getCommandFilePath());
                commandManager.saveDefaultCommands();
            } catch (Exception e) {
                logError("AliasResetCommand", e);
            }
        }
    }
    private static class AliasListCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        public AliasListCommand(HytaleFoundations hytaleFoundations) {
            super("list", "Create a new alias for a existing command.");
            this.hytaleFoundations = hytaleFoundations;
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            CommandAliasManager commandManager = hytaleFoundations.getCommandManager();
            String playerUsername = commandContext.sender().getDisplayName();
            if(commandManager == null) return;
            if(commandManager.getCommandMap() == null || commandManager.getCommandMap().isEmpty()) {
                commandContext.sender().sendMessage(LM.getPlayerMessage(playerUsername, LangKey.NOTHING_FOUND, "command alias", "Empty map"));
                return;
            }

            commandContext.sender().sendMessage(LM.getPlayerMessage(playerUsername, LangKey.LIST_CONTEXT, "command alias(es)"));
            for(Map.Entry<String, CommandAliasManager.Command> entry : commandManager.getCommandMap().entrySet()) {
                String permission = entry.getValue().getPermission().isEmpty() ? "<None>" : entry.getValue().getPermission();
                String aliases = entry.getValue().getAliases().length == 0 ? "<None>" : String.join(", ", entry.getValue().getAliases());
                commandContext.sender().sendMessage(LM.getPlayerMessage(playerUsername, LangKey.LIST_ITEM,
                        "&r&e&lID: &r&b&l" + entry.getKey()
                                + "&r&7&l [Target: &r&a" + entry.getValue().getTargetCommand() + "&r&7&l| "
                                + "&r&7&l [Usage: &r&e" + entry.getValue().getUsage() + "&r&7&l| "
                                + "&r&7&l [Permission: &r&c" + permission + "&r&7&l| "
                                + "&r&7&l [Alias(es): &r&f" + aliases + "&r&7&l] "));
            }
        }
    }
    private static class AliasCreateCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> id;
        private final RequiredArg<String> targetCommand;
        private final RequiredArg<String> usageCommand;
        private final OptionalArg<String> permission;
        private final OptionalArg<List<String>> aliases;
        public AliasCreateCommand(HytaleFoundations hytaleFoundations) {
            super("create", "Create a new alias for a existing command.");
            this.addAliases("crt");
            this.hytaleFoundations = hytaleFoundations;
            this.id = withRequiredArg("ID", "ID of the new command alias.", ArgTypes.STRING);
            this.targetCommand = withRequiredArg("Target Command", "Command (with/without argument) that the alias is for.", ArgTypes.STRING);
            this.usageCommand = withRequiredArg("Usage Command", "New string that will be used to execute the target command.", ArgTypes.STRING);
            this.permission = withOptionalArg("Permission", "Permission required to execute the new dynamic command.", ArgTypes.STRING);
            this.aliases = withListOptionalArg("Aliases", "Additional aliases for the new alias command.", ArgTypes.STRING);
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            CommandAliasManager commandManager = hytaleFoundations.getCommandManager();
            String playerUsername = commandContext.sender().getDisplayName();
            String commandID = commandContext.get(this.id);
            String targetCommand = commandContext.get(this.targetCommand);
            if(commandManager == null) return;
            if(!commandManager.getCommandMap().containsKey(commandID)) {
                commandContext.sender().sendMessage(LM.getPlayerMessage(playerUsername, LangKey.COMMAND_ID_NOT_FOUND, commandID));
                return;
            }
            if(!commandManager.getRegisteredCommands().containsKey(targetCommand)) {
                commandContext.sender().sendMessage(LM.getPlayerMessage(playerUsername, LangKey.COMMAND_NOT_FOUND, targetCommand));
                return;
            }
            try {
                List<String> aliases = commandContext.get(this.aliases);
                CommandAliasManager.Command command = new CommandAliasManager.Command(targetCommand,
                        commandContext.get(this.usageCommand),
                        commandContext.get(this.permission),
                        aliases.toArray(String[]::new));
                commandManager.createAliasCommand(commandID, command);
                commandContext.sender().sendMessage(LM.getPlayerMessage(playerUsername, LangKey.COMMAND_ALIAS_CREATE, commandID));
            } catch (Exception e) {
                logError("AliasCreateCommand", e);
            }
        }
    }
    private static class AliasDeleteCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> id;
        public AliasDeleteCommand(HytaleFoundations hytaleFoundations) {
            super("delete", "Delete an existing alias for a command.");
            this.addAliases("dlt");
            this.hytaleFoundations = hytaleFoundations;
            this.id = withRequiredArg("ID", "ID of the new command alias.", ArgTypes.STRING);
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            CommandAliasManager commandManager = hytaleFoundations.getCommandManager();
            String playerUsername = commandContext.sender().getDisplayName();
            String commandID = commandContext.get(this.id);
            if(commandManager == null) return;
            if(!commandManager.getCommandMap().containsKey(commandID)) {
                commandContext.sender().sendMessage(LM.getPlayerMessage(playerUsername, LangKey.COMMAND_ID_NOT_FOUND, commandID));
                return;
            }
            try {
                commandManager.deleteAliasCommand(commandID);
                commandContext.sender().sendMessage(LM.getPlayerMessage(playerUsername, LangKey.COMMAND_ALIAS_CREATE, commandID));
            } catch (Exception e) {
                logError("AliasDeleteCommand", e);
            }

        }
    }
    private static class AliasEditCommand extends AbstractPlayerCommand {
        private final HytaleFoundations hytaleFoundations;
        private final RequiredArg<String> id;
        private final OptionalArg<String> targetCommand;
        private final OptionalArg<String> usageCommand;
        private final OptionalArg<String> permission;
        private final OptionalArg<List<String>> aliases;
        public AliasEditCommand(HytaleFoundations hytaleFoundations) {
            super("edit", "Edit an existing alias for a command.");
            this.hytaleFoundations = hytaleFoundations;
            this.id = withRequiredArg("ID", "ID of the new command alias.", ArgTypes.STRING);
            this.targetCommand = withOptionalArg("Target Command", "Command (with/without argument) that the alias is for.", ArgTypes.STRING);
            this.usageCommand = withOptionalArg("Usage Command", "New string that will be used to execute the target command.", ArgTypes.STRING);
            this.permission = withOptionalArg("Permission", "Permission required to execute the new dynamic command.", ArgTypes.STRING);
            this.aliases = withListOptionalArg("Aliases", "Additional aliases for the new alias command.", ArgTypes.STRING);
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            CommandAliasManager commandManager = hytaleFoundations.getCommandManager();
            String commandID = commandContext.get(this.id);
            if(commandManager == null) return;
            if(!commandManager.getCommandMap().containsKey(commandID)) {
                commandContext.sender().sendMessage(LM.getPlayerMessage(commandContext.sender().getDisplayName(),
                        LangKey.COMMAND_ID_NOT_FOUND, commandID));
                return;
            }
            try {
                CommandAliasManager.Command oldCommand = commandManager.getCommandMap().get(commandID);
                List<String> aliases = commandContext.get(this.aliases);
                CommandAliasManager.Command updatedCommand = new CommandAliasManager.Command(
                        commandContext.get(this.targetCommand),
                        commandContext.get(this.usageCommand),
                        commandContext.get(this.permission),
                        aliases.toArray(String[]::new));
                commandManager.editAliasCommand(commandID, updatedCommand);
                if(!commandManager.isSameCommand(oldCommand, updatedCommand)) commandContext.sender()
                        .sendMessage(LM.getPlayerMessage(commandContext.sender().getDisplayName(),
                                LangKey.COMMAND_ALIAS_EDIT, commandID));
            } catch (Exception e) {
                logError("AliasEditCommand", e);
            }
        }
    }
}
