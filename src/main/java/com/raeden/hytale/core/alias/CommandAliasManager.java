package com.raeden.hytale.core.alias;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
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
import static com.raeden.hytale.core.config.ConfigManager.*;
import static com.raeden.hytale.utils.FileUtils.*;

public class CommandAliasManager {
    private final HytaleFoundations hytaleFoundations;
    private final CommandRegistry commandRegistry;
    private final Map<String, Command> commandMap;
    private final Map<String, String> registeredCommands;
    private final Set<String> registeredCommandAliases;

    private final boolean debug;
    private CommandFile commandFile;

    public CommandAliasManager(HytaleFoundations hytaleFoundations, CommandRegistry commandRegistry) {
        this.hytaleFoundations = hytaleFoundations;
        this.commandRegistry = commandRegistry;
        commandMap = new ConcurrentHashMap<>();
        registeredCommands = new ConcurrentHashMap<>();
        registeredCommandAliases = ConcurrentHashMap.newKeySet();

        debug = hytaleFoundations.getConfigManager().getDefaultConfig().isDebugMode();

        initializeCommandAliasManager();
    }

    public void initializeCommandAliasManager() {
        commandMap.putAll(getDefaultCommandMap());
        if(Files.exists(COMMAND_FILE_PATH)) {
            loadCommands();
        } else {
            saveDefaultCommands();
        }
    }

    // Loading and Saving
    public void saveCommandFile() {
        commandFile = new CommandFile();
        commandFile.setAliasMap(commandMap);
        saveJsonFile(COMMAND_FILE_NAME, COMMAND_FILE_PATH, commandFile, false);
        registerDynamicCommands();
    }

    public void saveDefaultCommands() {
        commandFile = new CommandFile();
        Map<String, Command> cmap = new LinkedHashMap<>(getDefaultCommandMap());
        if(hytaleFoundations.getConfigManager().getDefaultConfig().isGenerateDefaultData()) commandFile.setAliasMap(cmap);
        saveJsonFile(COMMAND_FILE_NAME, COMMAND_FILE_PATH, commandFile, true);
        myLogger.atInfo().log(LM.getConsoleMessage(LangKey.LOAD_SUCCESS, commandMap.size() + " default command(s).").getAnsiMessage());
        registerDynamicCommands();
    }

    public void loadCommands() {
        Type type = new TypeToken<CommandFile>(){}.getType();
        CommandFile loadedCommandFile = loadJsonFile(COMMAND_FILE_NAME, COMMAND_FILE_PATH, type, true);
        Map<String, Command> tempCommandMap = new ConcurrentHashMap<>(commandMap);
        commandMap.clear();
        if(loadedCommandFile != null && loadedCommandFile.getCommandMap() != null) {
            int newCommands = 0;
            int updatedCommands = 0;
            int removedCommands = 0;
            for(Map.Entry<String, Command> entry : tempCommandMap.entrySet()) {
                if(!loadedCommandFile.getCommandMap().containsKey(entry.getKey())) {
                    removedCommands++;
                }
            }
            for(Map.Entry<String, Command> entry : loadedCommandFile.getCommandMap().entrySet()) {
                String commandID = entry.getKey();
                Command commandData = entry.getValue();
                if(!tempCommandMap.containsKey(commandID)) newCommands++; // new
                // updated
                if(tempCommandMap.containsKey(commandID)) {
                    Command oldCommand = tempCommandMap.get(commandID);
                    if(oldCommand != null) {
                        List<String> discrepancies = compareCommands(oldCommand, commandData);
                        if(!discrepancies.isEmpty()) {
                            if(discrepancies.size() == 1) {
                                String issue = discrepancies.getFirst();
                                switch (issue) {
                                    case "target_command":
                                        if(debug) myLogger.atInfo().log(LM.getConsoleMessage(LangKey.COMMAND_UPDATE_TARGET,
                                                commandID, commandData.getTargetCommand(), oldCommand.getTargetCommand()).getAnsiMessage());
                                        break;
                                    case "name_alias":
                                        if(debug) myLogger.atInfo().log(LM.getConsoleMessage(LangKey.COMMAND_UPDATE_NAME,
                                                commandID, commandData.getUsage(), oldCommand.getUsage()).getAnsiMessage());
                                        break;
                                    case "permission":
                                        if(debug) myLogger.atInfo().log(LM.getConsoleMessage(LangKey.COMMAND_UPDATE_PERM,
                                                commandID, commandData.getPermission(), oldCommand.getPermission()).getAnsiMessage());
                                        break;
                                    case "added_alias":
                                        if(debug) myLogger.atInfo().log(LM.getConsoleMessage(LangKey.COMMAND_ADD_ALIASES,
                                                commandID).getAnsiMessage());
                                        break;
                                    case "removed_alias":
                                        if(debug) myLogger.atInfo().log(LM.getConsoleMessage(LangKey.COMMAND_REMOVE_ALIASES,
                                                commandID).getAnsiMessage());
                                        break;
                                    case "changed_alias":
                                        if(debug) myLogger.atInfo().log(LM.getConsoleMessage(LangKey.COMMAND_CHANGE_ALIASES,
                                                commandID).getAnsiMessage());
                                        break;
                                    default:
                                        if(debug) myLogger.atInfo().log(LM.getConsoleMessage(LangKey.COMMAND_UPDATE_MULTIPLE,
                                                commandID, String.join(", ", discrepancies)).getAnsiMessage());
                                }
                            } else {
                                if(debug) myLogger.atInfo().log(LM.getConsoleMessage(LangKey.COMMAND_UPDATE_MULTIPLE,
                                        commandID, String.join(", ", discrepancies)).getAnsiMessage());
                            }
                            updatedCommands++;
                        }
                    }
                }
                commandMap.put(entry.getKey(), entry.getValue());
            }
            if(newCommands > 0) myLogger.atInfo().log(LM.getConsoleMessage(LangKey.LOAD_SUCCESS, newCommands + " new command(s).").getAnsiMessage());
            if(updatedCommands > 0) myLogger.atInfo().log(LM.getConsoleMessage(LangKey.COMMANDS_UPDATED, String.valueOf(updatedCommands)).getAnsiMessage());
            if(removedCommands > 0) myLogger.atInfo().log(LM.getConsoleMessage(LangKey.REMOVE_SUCCESS, removedCommands + " command(s).").getAnsiMessage());
            commandFile = loadedCommandFile;
            registerDynamicCommands();
        } else {
            saveDefaultCommands();
        }
    }

    private void registerDynamicCommands() {
        if(commandRegistry == null) return;
        int commandCount = 0;
        int commandAliasCount = 0;
        for(Map.Entry<String, Command> entry : commandMap.entrySet()) {
            Command command = entry.getValue();
            String commandID = entry.getKey();
            if(command == null) continue;

            String commandName = command.getUsage().toLowerCase();
            String commandTarget = command.getTargetCommand().toLowerCase();
            try {
                if(!registeredCommands.containsKey(commandName)) {
                    DynamicCommand newCommand = new DynamicCommand(this, commandID, command);
                    commandRegistry.registerCommand(newCommand);
                    registeredCommands.put(commandName, commandTarget);
                    commandCount++;
                    if(command.getAliases() != null) {
                        for(String alias : command.getAliases()) {
                            registeredCommandAliases.add(alias.toLowerCase());
                        }
                    }
                } else {
                    // mismatch Target
                    if(!registeredCommands.get(commandName).equalsIgnoreCase(commandTarget)) {
                        if(debug) myLogger.atWarning().log(LM.getConsoleMessage(LangKey.COMMANDS_MISMATCH_TARGET, commandID,
                                registeredCommands.get(commandName), commandTarget).getAnsiMessage());
                        registeredCommands.remove(commandName);
                        registeredCommands.put(commandName, commandTarget);
                        DynamicCommand newCommand = new DynamicCommand(this, commandID, command);
                        commandRegistry.registerCommand(newCommand);
                    }
                }
                // Register new aliases
                if (command.getAliases() != null) {
                    for (String alias : command.getAliases()) {
                        String aliasLower = alias.toLowerCase();
                        if (!registeredCommandAliases.contains(aliasLower)) {
                            DynamicCommand newAliasCmd = new DynamicCommand(this, commandID, command);
                            commandRegistry.registerCommand(newAliasCmd);
                            registeredCommandAliases.add(aliasLower);
                            commandAliasCount++;
                        }
                    }
                }
            } catch (Exception e) {
                logError("registerDynamicCommands", e);
            }
        }
        if(commandCount > 0) myLogger.atInfo().log(LM.getConsoleMessage(LangKey.COMMAND_REGISTER, String.valueOf(commandCount)).getAnsiMessage());
        if(commandAliasCount > 0) myLogger.atInfo().log(LM.getConsoleMessage(LangKey.COMMAND_REGISTER_ALIAS, String.valueOf(commandAliasCount)).getAnsiMessage());
    }

    // Command methods
    public void createAliasCommand(String commandID, Command command) {
        if(commandRegistry == null) return;
        commandMap.put(commandID, command);
        String commandName = command.getUsage().toLowerCase();
        String commandTarget = command.getTargetCommand().toLowerCase();
        if(!registeredCommands.containsKey(commandName)) {
            DynamicCommand newCommand = new DynamicCommand(this, commandID, command);
            commandRegistry.registerCommand(newCommand);
            registeredCommands.put(commandName, commandTarget);
            if(command.getAliases() != null) {
                for(String alias : command.getAliases()) {
                    registeredCommandAliases.add(alias.toLowerCase());
                }
            }
        }
        saveCommandFile();
    }
    public void deleteAliasCommand(String commandID) {
        if(!commandMap.containsKey(commandID)) return;
        Command command = commandMap.get(commandID);
        List<String> aliases = Arrays.asList(command.getAliases());
        registeredCommands.remove(command.getUsage());
        if(!aliases.isEmpty()) {
            aliases.forEach(registeredCommandAliases::remove);
        }
        saveCommandFile();
    }
    public void editAliasCommand(String commandID, Command updatedCommand) {
        if(!commandMap.containsKey(commandID)) return;
        Command oldCommand = commandMap.get(commandID);
        commandMap.remove(commandID);

        registeredCommands.remove(oldCommand.getUsage());
        List<String> aliases = Arrays.asList(oldCommand.getAliases());
        if(!aliases.isEmpty()) {
            aliases.forEach(registeredCommandAliases::remove);
        }

        registeredCommands.put(updatedCommand.getUsage(), updatedCommand.getTargetCommand());
        registeredCommandAliases.addAll(Arrays.asList(updatedCommand.getAliases()));
        commandMap.put(commandID, updatedCommand);
        saveCommandFile();
    }

    // Helper classes
    private void unknownCommandMessage(String commandName, CommandContext commandContext) {
        if(commandContext.isPlayer()) {
            commandContext.sender().sendMessage(LM.getConsoleMessage(LangKey.DEFAULT_COMMAND_NF, commandName));
        }
    }
    public boolean isSameCommand(Command oldCommand, Command newCommand) {
        List<String> changes = compareCommands(oldCommand, newCommand);
        return changes.isEmpty();
    }
    private List<String> compareCommands(Command oldCommand, Command newCommand) {
        if(oldCommand == null || newCommand == null) return List.of();
        List<String> changes = new ArrayList<>();
        if(!oldCommand.getTargetCommand().equalsIgnoreCase(newCommand.getTargetCommand())) changes.add("target_command");
        if(!oldCommand.getUsage().equalsIgnoreCase(newCommand.getUsage())) changes.add("name_alias");
        if(!oldCommand.getPermission().equalsIgnoreCase(newCommand.getPermission())) changes.add("permission");
        boolean oldHasAliases = oldCommand.getAliases() != null && oldCommand.getAliases().length > 0;
        boolean newHasAliases = newCommand.getAliases() != null && newCommand.getAliases().length > 0;

        if (oldHasAliases && !newHasAliases) {
            changes.add("removed_alias");
        } else if (!oldHasAliases && newHasAliases) {
            changes.add("added_alias");
        } else if (oldHasAliases && newHasAliases) {
            List<String> oldAliases = Arrays.asList(oldCommand.getAliases());
            List<String> newAliases = Arrays.asList(newCommand.getAliases());

            if (oldAliases.size() < newAliases.size()) {
                changes.add("added_alias");
            } else if (oldAliases.size() > newAliases.size()) {
                changes.add("removed_alias");
            } else {
                for (String alias : oldAliases) {
                    if (!newAliases.contains(alias)) {
                        changes.add("changed_alias");
                        break;
                    }
                }
            }
        }
        return changes;
    }

    // Classes and Getters
    public Map<String, String> getRegisteredCommands() { return registeredCommands;}
    public Set<String> getRegisteredCommandAliases() { return registeredCommandAliases;}
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
        private final String commandKey;
        private final String targetCommand;
        public DynamicCommand(CommandAliasManager commandAliasManager, String commandKey, Command command) {
            super(command.getUsage(), "Alias for /" + command.getTargetCommand(), false);
            this.setAllowsExtraArguments(true);
            this.targetCommand = command.getTargetCommand();
            this.commandAliasManager = commandAliasManager;
            this.commandKey = commandKey;

            if (command.getPermission() != null && !command.getPermission().isEmpty()) {
                this.requirePermission(command.getPermission());
            }
            if (command.getAliases() != null && command.getAliases().length > 0) {
                this.addAliases(command.getAliases());
            }
        }
        @Override
        protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
            Command liveCommand = commandAliasManager.getCommandMap().get(commandKey);
            String rawInput = commandContext.getInputString();
            String[] splitInput = rawInput.split(" ", 2);
            String triggerWord = splitInput[0].startsWith("/") ? splitInput[0].substring(1) : splitInput[0];

            boolean isCommandValid = false;
            if(liveCommand != null) {
                if(liveCommand.getUsage().equalsIgnoreCase(triggerWord)) {
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
                if (isCommandValid) {
                    String liveNameLower = liveCommand.getUsage().toLowerCase();
                    if(commandAliasManager.getRegisteredCommands().containsKey(liveNameLower)) {
                        if(!liveCommand.getTargetCommand().equalsIgnoreCase(commandAliasManager.getRegisteredCommands().get(liveNameLower))) {
                            isCommandValid = false;
                            myLogger.atWarning().log(LM.getConsoleMessage(LangKey.COMMANDS_MISMATCH_TARGET, commandKey,
                                    commandAliasManager.getRegisteredCommands().get(liveNameLower), liveCommand.getTargetCommand()).getAnsiMessage());
                        }
                    } else {
                        isCommandValid = false;
                    }
                }
            }

            if(!isCommandValid) {
                commandAliasManager.unknownCommandMessage(triggerWord, commandContext);
                return;
            }

            String extraArgs = splitInput.length > 1 ? splitInput[1].trim() : "";
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
        @SerializedName("usage")
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
        public String getUsage() {return nameAlias;}
        public String[] getAliases() {return aliases;}
        public String getPermission() {return permission;}
    }
    private static class CommandFile {
        @SerializedName("VERSION")
        private final String version = COMMANDS_CONFIG_VERSION;
        @SerializedName("COMMAND_LIST")
        private Map<String, Command> aliasMap = new LinkedHashMap<>();

        public void setAliasMap(Map<String, Command> map) {this.aliasMap = map;}
        public Map<String, Command> getCommandMap() {return aliasMap;}

        public String getVersion() {return version;}
    }
}
