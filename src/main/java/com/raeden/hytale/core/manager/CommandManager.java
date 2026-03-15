package com.raeden.hytale.core.manager;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.lang.LangKey;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.raeden.hytale.HytaleFoundations.LM;
import static com.raeden.hytale.HytaleFoundations.myLogger;
import static com.raeden.hytale.core.config.ConfigManager.COMMAND_FILENAME;
import static com.raeden.hytale.utils.FileUtils.loadJsonFile;
import static com.raeden.hytale.utils.FileUtils.saveJsonFile;

public class CommandManager {
    private final HytaleFoundations hytaleFoundations;
    private final Map<String, Command> commandMap;

    private final String commandFileName = COMMAND_FILENAME;
    private final Path commandFilePath;

    private CommandFile commandFile;

    public CommandManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        commandMap = new ConcurrentHashMap<>();
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
        } else {
            saveDefaultCommands();
        }
    }

    public void saveCommands() {
        commandFile = new CommandFile();
        commandFile.setAliasMap(commandMap);
        saveJsonFile(commandFileName, commandFilePath, commandFile, false);
    }

    private void saveDefaultCommands() {
        commandFile = new CommandFile();
        commandFile.setAliasMap(commandMap);
        saveJsonFile(commandFileName, commandFilePath, commandFile, true);
    }

    // Classes and Getters
    public String getCommandName(String commandKey) {
        if (commandMap == null || !commandMap.containsKey(commandKey)) return "";
        return commandMap.get(commandKey).getName();
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
        Map<String, Command> map = new ConcurrentHashMap<>();
        for(DefaultCommands cmd : DefaultCommands.values()) {
            Command value = new Command(cmd.getName(), cmd.getPermission(), cmd.getAliases());
            map.put(cmd.name(), value);
        }
        return map;
    }

    public static class Command {
        private String name;
        private String permission;
        private String[] aliases;
        public Command(){}
        public Command(String name, String permission, String... aliases) {
            this.name = name;
            this.permission = permission;
            this.aliases = aliases;
        }
        public String getName() {return name;}
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
