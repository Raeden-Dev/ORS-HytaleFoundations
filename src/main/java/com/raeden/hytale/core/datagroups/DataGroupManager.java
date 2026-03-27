package com.raeden.hytale.core.datagroups;

import com.hypixel.hytale.server.core.universe.Universe;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.config.containers.Config;
import com.raeden.hytale.core.lang.LangKey;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.raeden.hytale.HytaleFoundations.LM;
import static com.raeden.hytale.HytaleFoundations.myLogger;
import static com.raeden.hytale.utils.FileUtils.createDirectory;
import static com.raeden.hytale.utils.FileUtils.logError;

public class DataGroupManager {
    private final HytaleFoundations hytaleFoundations;
    private final Map<String, String> worldGroupInfo;
    public DataGroupManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        worldGroupInfo = new ConcurrentHashMap<>();
        if (hytaleFoundations.getConfigManager().getDefaultConfig().getDataGroups().isEmpty()) {
            createDefaultDataGroup(hytaleFoundations.getConfigManager().getDefaultConfig());
        }
        loadWorldDataGroups();
    }

    public void loadWorldDataGroups() {
        try {
            int totalDataGroups = 0;
            for(Map.Entry<String, List<String>> group : hytaleFoundations.getConfigManager().getDefaultConfig().getDataGroups().entrySet()) {
                String groupName = group.getKey();
                List<String> worlds = group.getValue();
                if(!worlds.isEmpty()) {
                    totalDataGroups++;
                    if(worlds.size() == 1) {
                        worldGroupInfo.put(worlds.getFirst(), groupName);
                        continue;
                    }
                    for(String world : worlds) {
                        worldGroupInfo.put(world, groupName);
                    }
                }
            }
            if(LM != null) myLogger.atInfo().log(LM.getConsoleMessage(LangKey.LOAD_SUCCESS, totalDataGroups + "data group(s)").getAnsiMessage());
            else myLogger.atInfo().log("[LOAD] Loaded " + totalDataGroups + " data group(s)");
        } catch (Exception e) {
            logError("loadWorldDataGroups", e);
        }
    }

    public void createDefaultDataGroup(Config config) {
        if(config == null) return;
        Universe universe = Universe.get();
        Set<String> worlds = universe.getWorlds().keySet();
        hytaleFoundations.getConfigManager().getDefaultConfig().addDataGroup("default", worlds.stream().toList());
    }

    public void createDataDirectories(String dataType) {
        Set<String> dataGroups = hytaleFoundations.getConfigManager().getDefaultConfig().getDataGroups().keySet();
        if(dataGroups.isEmpty()) {
            createDefaultDataGroup(hytaleFoundations.getConfigManager().getDefaultConfig());
            loadWorldDataGroups();
            dataGroups = hytaleFoundations.getConfigManager().getDefaultConfig().getDataGroups().keySet();
        }
        for(String group : dataGroups) {
            Path dataPath = hytaleFoundations.getDataDirectory().resolve("data").resolve(group).resolve(dataType);
            createDirectory(dataPath, true);
        }
    }

    // Getters and Setters
    public Map<String, Path> getDataDirPaths(String dataType) {
        Map<String, Path> pathInfo = new ConcurrentHashMap<>();
        Set<String> dataGroups = hytaleFoundations.getConfigManager().getDefaultConfig().getDataGroups().keySet();
        if(dataGroups.isEmpty()) return null;
        for(String group : dataGroups) {
            Path dataPath = hytaleFoundations.getDataDirectory().resolve("data").resolve(group).resolve(dataType);
            if(Files.exists(dataPath)) pathInfo.put(group, dataPath);
        }
        return pathInfo;
    }

    public String getDataGroupOfWorld(String worldName) {
        if(worldGroupInfo == null || !worldGroupInfo.containsKey(worldName)) {
            return "default";
        }
        return worldGroupInfo.get(worldName);
    }
}
