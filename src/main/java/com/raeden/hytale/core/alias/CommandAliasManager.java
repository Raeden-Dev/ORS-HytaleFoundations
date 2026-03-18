package com.raeden.hytale.core.alias;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.command.system.CommandRegistry;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.lang.LangKey;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.raeden.hytale.HytaleFoundations.LM;
import static com.raeden.hytale.HytaleFoundations.myLogger;
import static com.raeden.hytale.core.config.ConfigManager.COMMAND_FILENAME;
import static com.raeden.hytale.utils.FileUtils.*;

public class CommandAliasManager {
    private final HytaleFoundations hytaleFoundations;
    private final CommandRegistry commandRegistry;
    private final Map<String, Command> commandMap;
    private final Set<String> registeredCommands;

    private final String commandFileName = COMMAND_FILENAME;
    private final Path commandFilePath;

    private CommandFile commandFile;

    public CommandAliasManager(HytaleFoundations hytaleFoundations, CommandRegistry commandRegistry) {
        this.hytaleFoundations = hytaleFoundations;
        this.commandRegistry = commandRegistry;
        commandMap = new ConcurrentHashMap<>();
        registeredCommands = ConcurrentHashMap.newKeySet();
        commandFilePath = hytaleFoundations.getDataDirectory().resolve(commandFileName);

        initializeAliasManager();
    }

    public void initializeAliasManager() {
        commandMap.putAll(getDefaultCommandMap());
        if(Files.exists(commandFilePath)) {
            loadCommands();
        } else {
            saveDefaultCommands();
        }
    }

    private void unknownCommandMessage(CommandContext commandContext) {
        if(commandContext.isPlayer()) {
            commandContext.sendMessage(Message.raw("Unknown command. Type /help for help.").color("#FF5555"));
        }
    }

    // Loading and Saving
    public void loadCommands() {
        Type type = new TypeToken<CommandFile>(){}.getType();
        CommandFile loadedCommandFile = loadJsonFile(commandFileName, commandFilePath, type, true);
        if(loadedCommandFile != null && loadedCommandFile.getAliasMap() != null) {
            int newCommands = 0;
            for(Map.Entry<String, Command> entry : loadedCommandFile.getAliasMap().entrySet()) {
                if(!commandMap.containsKey(entry.getKey())) {
                    newCommands++;
                }
                commandMap.put(entry.getKey(), entry.getValue());
            }
            if(newCommands > 0) myLogger.atInfo().log(LM.getConsoleMessage(LangKey.LOAD_SUCCESS, newCommands + " command(s)").getAnsiMessage());
            commandFile = loadedCommandFile;
            registerDynamicCommands();
        } else {
            saveDefaultCommands();
        }
    }

    public void saveCommands() {
        commandFile = new CommandFile();
        commandFile.setAliasMap(commandMap);
        saveJsonFile(commandFileName, commandFilePath, commandFile, false);
        registerDynamicCommands();
    }

    private void saveDefaultCommands() {
        commandFile = new CommandFile();
        commandFile.setAliasMap(commandMap);
        saveJsonFile(commandFileName, commandFilePath, commandFile, true);
        registerDynamicCommands();
    }

    private void registerDynamicCommands() {
        if(commandRegistry == null) return;
        int commandCount = 0;
        for(Map.Entry<String, Command> entry : commandMap.entrySet()) {
            Command command = entry.getValue();
            String key = entry.getKey();

            if(!registeredCommands.contains(command.getTargetCommand())) {
                DynamicCommand newCommand = new DynamicCommand(command.getNameAlias(), command.getTargetCommand(),
                        command.getPermission(), command.getAliases());
                try {
                    commandRegistry.registerCommand(newCommand);
                    registeredCommands.add(command.getTargetCommand());
                    commandCount++;
                } catch (Exception e) {
                    logError("registerDynamicCommands", e);
                }
            }
        }
        myLogger.atInfo().log(LM.getConsoleMessage(LangKey.REGISTER_SUCCESS, commandCount + " command(s)").getAnsiMessage());
    }

    // Classes and Getters
    public String getCommandTargetName(String commandKey) {
        if (commandMap == null || !commandMap.containsKey(commandKey)) return "";
        return commandMap.get(commandKey).getTargetCommand();
    }
    public String getCommandName(String commandKey) {
        if (commandMap == null || !commandMap.containsKey(commandKey)) return "";
        return commandMap.get(commandKey).getNameAlias();
    }
    public String getCommandPermission(String commandKey) {
        if (commandMap == null || !commandMap.containsKey(commandKey)) return "";
        return commandMap.get(commandKey).getPermission();
    }
    public String[] getCommandAliases(String commandKey) {
        if (commandMap == null || !commandMap.containsKey(commandKey)) return null;
        return commandMap.get(commandKey).getAliases();
    }
    public Map<String, Command> getDefaultCommandMap() {
        Map<String, Command> map = new LinkedHashMap<>();
        for(DefaultCommands cmd : DefaultCommands.values()) {
            Command value = new Command(cmd.getTargetCommand(), cmd.getName(), cmd.getPermission(), cmd.getAliases());
            map.put(cmd.name().toLowerCase(), value);
        }
        return map;
    }

    public static class DynamicCommand extends AbstractPlayerCommand {
        private final String targetCommand;
        public DynamicCommand(String triggerName, String targetCommand, String permission, String... aliases) {
            super(triggerName, "Alias for /" + targetCommand, false);
            this.setAllowsExtraArguments(true);
            this.targetCommand = targetCommand;

            if (permission != null && !permission.isEmpty()) {
                this.requirePermission(permission);
            }
            if (aliases != null && aliases.length > 0) {
                this.addAliases(aliases);
            }
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            String rawInput = commandContext.getInputString();
            String extraArgs = "";
            int spaceIdx = rawInput.indexOf(' ');
            if (spaceIdx >= 0) {
                extraArgs = rawInput.substring(spaceIdx + 1).trim();
            }

            String fullCommand = targetCommand;
            if (!extraArgs.isEmpty()) {
                fullCommand += " " + extraArgs;
            }

            try {
                final String commandToRun = fullCommand;
                world.execute(() -> {
                    try {
                        CommandManager.get().handleCommand(playerRef, commandToRun);
                    } catch (Exception e) {
                        logError("DynamicCommand", e);
                        myLogger.atSevere().log(LM.getConsoleMessage(LangKey.PROCESS_FAILURE, "dynamic command: " + commandToRun).getAnsiMessage());
                    }
                });
            } catch (Exception e) {
                logError("DynamicCommand", e);
                myLogger.atSevere().log(LM.getConsoleMessage(LangKey.PROCESS_FAILURE, "dynamic command: " + fullCommand).getAnsiMessage());
            }
        }
    }

    public static class Command {
        @SerializedName("target_command")
        private String targetCommand;
        @SerializedName("name_alias")
        private String nameAlias;
        private String permission;
        private String[] aliases;
        public Command(){}
        public Command(String targetCommand, String nameAlias, String permission, String... aliases) {
            this.targetCommand = targetCommand;
            this.nameAlias = nameAlias;
            this.permission = permission;
            this.aliases = aliases;
        }
        public String getTargetCommand() {return targetCommand;}
        public String getNameAlias() {return nameAlias;}
        public String[] getAliases() {return aliases;}
        public String getPermission() {return permission;}
    }
    private static class CommandFile {
        @SerializedName("COMMAND_LIST")
        private Map<String, Command> aliasMap = new LinkedHashMap<>();

        public Command getAlias(String name) {return aliasMap.get(name);}
        public void setAliasMap(Map<String, Command> map) {this.aliasMap = map;}
        public Map<String, Command> getAliasMap() {return aliasMap;}
    }
}
