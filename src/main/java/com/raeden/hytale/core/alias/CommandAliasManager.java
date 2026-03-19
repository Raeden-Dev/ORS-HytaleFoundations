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
import java.util.*;
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

    public void unknownCommandMessage(String commandName, CommandContext commandContext) {
        if(commandContext.isPlayer()) {
            commandContext.sender().sendMessage(LM.getConsoleMessage(LangKey.COMMAND_NOT_FOUND, commandName));
        }
    }

    // Loading and Saving
    public void loadCommands() {
        System.out.println(commandRegistry.getRegistrations());
        Type type = new TypeToken<CommandFile>(){}.getType();
        CommandFile loadedCommandFile = loadJsonFile(commandFileName, commandFilePath, type, true);
        Map<String, Command> tempCommandMap = new ConcurrentHashMap<>(commandMap);
        commandMap.clear();
        if(loadedCommandFile != null && loadedCommandFile.getCommandMap() != null) {
            int newCommands = 0;
            int updatedCommands = 0;
            int newAliases = 0;
            for(Map.Entry<String, Command> entry : loadedCommandFile.getCommandMap().entrySet()) {
                boolean isPresentInMap = tempCommandMap.containsKey(entry.getKey());
                Command loadedCommand = tempCommandMap.get(entry.getKey());
                if(!isPresentInMap) {
                    newCommands++;
                }
                if(isPresentInMap) {
                    // Mismatch for target Commands
                    boolean mismatchTargetCommand = loadedCommand.getTargetCommand().equalsIgnoreCase(entry.getValue().getTargetCommand());
                    boolean mismatchNameAlias = loadedCommand.getNameAlias().equalsIgnoreCase(entry.getValue().getNameAlias());
                    boolean mismatchPerms = loadedCommand.getPermission().equalsIgnoreCase(entry.getValue().getPermission());
                    if(!mismatchTargetCommand || !mismatchNameAlias || !mismatchPerms) {
                        updatedCommands++;
                    }
                }
                // Load new aliases
                if(tempCommandMap.containsKey(entry.getKey())) {
                    if(loadedCommand.getAliases().length > 0) {
                        List<String> aliases = Arrays.stream(loadedCommand.getAliases()).toList();
                        for(String alias : entry.getValue().getAliases()) {
                            if(!aliases.contains(alias)) {
                                newAliases++;
                            }
                        }
                    }
                }
                commandMap.put(entry.getKey(), entry.getValue());
            }
            if(newCommands > 0) myLogger.atInfo().log(LM.getConsoleMessage(LangKey.LOAD_SUCCESS, newCommands + " command(s)").getAnsiMessage());
            if(updatedCommands > 0) myLogger.atInfo().log(LM.getConsoleMessage(LangKey.UPDATE_SUCCESS, updatedCommands + " command(s)").getAnsiMessage());
            if(newAliases > 0) myLogger.atInfo().log(LM.getConsoleMessage(LangKey.LOAD_SUCCESS, newAliases + " command alias(es)").getAnsiMessage());
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

            String commandName = command.getNameAlias().toLowerCase();
            if(!registeredCommands.contains(commandName)) {
                DynamicCommand newCommand = new DynamicCommand(this, key, command.getNameAlias(), command.getTargetCommand(),
                        command.getPermission(), command.getAliases());
                try {
                    commandRegistry.registerCommand(newCommand);
                    registeredCommands.add(commandName);
                    commandCount++;
                } catch (Exception e) {
                    logError("registerDynamicCommands", e);
                }
            }
            if (command.getAliases() != null) {
                for (String alias : command.getAliases()) {
                    String aliasLower = alias.toLowerCase();
                    if (!registeredCommands.contains(aliasLower)) {
                        DynamicCommand newAliasCmd = new DynamicCommand(this, key, aliasLower, command.getTargetCommand(),
                                command.getPermission());
                        try {
                            commandRegistry.registerCommand(newAliasCmd);
                            registeredCommands.add(aliasLower);
                            commandCount++;
                        } catch (Exception e) {
                            logError("registerDynamicCommands", e);
                        }
                    }
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
    public boolean isRegisteredCommand(String key) {return registeredCommands.contains(key);}
    public Map<String, Command> getCommandMap() { return commandMap;}
    public Map<String, Command> getDefaultCommandMap() {
        Map<String, Command> map = new LinkedHashMap<>();
        for(DefaultCommands cmd : DefaultCommands.values()) {
            Command value = new Command(cmd.getTargetCommand(), cmd.getName(), cmd.getPermission(), cmd.getAliases());
            map.put(cmd.name().toLowerCase(), value);
        }
        return map;
    }

    public static class DynamicCommand extends AbstractPlayerCommand {
        private final CommandAliasManager commandAliasManager;
        private final String triggerWord;
        private final String commandKey;
        private final String targetCommand;
        public DynamicCommand(CommandAliasManager commandAliasManager, String commandKey, String nameAlias, String targetCommand, String permission, String... aliases) {
            super(nameAlias, "Alias for /" + targetCommand, false);
            this.setAllowsExtraArguments(true);
            this.targetCommand = targetCommand;
            this.commandAliasManager = commandAliasManager;
            this.commandKey = commandKey;
            this.triggerWord = nameAlias;

            if (permission != null && !permission.isEmpty()) {
                this.requirePermission(permission);
            }
            if (aliases != null && aliases.length > 0) {
                this.addAliases(aliases);
            }
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            Command liveCommand = commandAliasManager.getCommandMap().get(commandKey);
            boolean isCommandValid = false;
            if(liveCommand != null) {
                if(liveCommand.getNameAlias().equalsIgnoreCase(triggerWord)) {
                    isCommandValid = true;
                }
                else if (liveCommand.getAliases() != null) {
                    for (String liveAlias : liveCommand.getAliases()) {
                        if (liveAlias.equalsIgnoreCase(triggerWord)) {
                            isCommandValid = true;
                            break;
                        }
                    }
                }
            }
            if(!isCommandValid) {
                commandAliasManager.unknownCommandMessage(commandContext.getInputString(), commandContext);
                return;
            }

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
        public Map<String, Command> getCommandMap() {return aliasMap;}
    }
}
