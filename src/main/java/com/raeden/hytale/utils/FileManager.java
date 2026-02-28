package com.raeden.hytale.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.raeden.hytale.lang.LangKey;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

import static com.raeden.hytale.HytaleFoundations.*;

public class FileManager {
    // Creating a directory
    public static void createDirectory(Path directoryPath, boolean showInfo) {
        if (Files.exists(directoryPath)) return;

        String fileName = "[" + directoryPath.getFileName().toString() + "]";
        try {
            Files.createDirectories(directoryPath);
            if (showInfo) {
                if (LM != null) {
                    myLogger.atInfo().log(LM.getMessage(LangKey.DIR_CREATE_SUCCESS_LOC,true, fileName, directoryPath.toString()).getAnsiMessage());
                } else {
                    myLogger.atInfo().log("[DIR] Created %s directory at %s", fileName, directoryPath.toString());
                }
            }
        } catch (IOException e) {
            logError("createDirectory", e);
            if (LM != null) {
                myLogger.atSevere().log(LM.getMessage(LangKey.DIR_CREATE_FAIL_LOC,true, fileName, directoryPath.toString()).getAnsiMessage());
            } else {
                myLogger.atSevere().log("[DIR] Failed to create %s directory at %s", fileName, directoryPath.toString());
            }
        }
    }

    // Loading a .json file
    public static <T> T loadJsonFile(Path filePath, Type typeOfT) {
        return loadJsonFile(filePath.getFileName().toString(), filePath, typeOfT, false);
    }

    public static <T> T loadJsonFile(String fileName, Path filePath, Type typeOfT) {
        return loadJsonFile(fileName, filePath, typeOfT, false);
    }

    public static <T> T loadJsonFile(Path filePath, Type typeOfT, boolean showInfo) {
        return loadJsonFile(filePath.getFileName().toString(), filePath, typeOfT, showInfo);
    }

    public static <T> T loadJsonFile(String fileName, Path filePath, Type typeOfT, boolean showInfo) {
        if (Files.exists(filePath)) {
            try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
                T myObj = GSON.fromJson(reader, typeOfT);

                if (myObj == null) {
                    if (LM != null) {
                        myLogger.atSevere().log(LM.getMessage(LangKey.LOAD_FAILURE_LOC,true, fileName, filePath.toString()).getAnsiMessage());
                    } else {
                        myLogger.atSevere().log("[LOAD] Failed to load " + fileName + " at " + filePath);
                    }
                    return null;
                } else {
                    if (showInfo) {
                        if (LM != null) {
                            myLogger.atInfo().log(LM.getMessage(LangKey.LOAD_SUCCESS,true, fileName).getAnsiMessage());
                        } else {
                            myLogger.atInfo().log("[LOAD] Loaded " + fileName);
                        }
                    }
                    return myObj;
                }
            } catch (IOException | JsonSyntaxException e) {
                logError("loadJsonFile", e);
                if (LM != null) {
                    myLogger.atSevere().log(LM.getMessage(LangKey.READ_FAILURE_LOC,true, fileName, filePath.toString()).getAnsiMessage());
                } else {
                    myLogger.atSevere().log("[READ] Failed to read " + fileName + " at " + filePath);
                }
            }
        } else {
            if (LM != null) {
                myLogger.atWarning().log(LM.getMessage(LangKey.FILE_NOT_FOUND_LOC,true, fileName, filePath.toString()).getAnsiMessage());
            } else {
                myLogger.atWarning().log("[ERROR] Could not find " + fileName + " at " + filePath);
            }
        }
        return null;
    }

    // Getting a .json object
    public static JsonObject getJsonObject(Path filePath) {
        return getJsonObject(filePath.getFileName().toString(), filePath, false);
    }

    public static JsonObject getJsonObject(Path filePath, boolean showInfo) {
        return getJsonObject(filePath.getFileName().toString(), filePath, showInfo);
    }

    public static JsonObject getJsonObject(String fileName, Path filePath, boolean showInfo) {
        if (Files.exists(filePath)) {
            try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

                if (jsonObject == null) {
                    if (LM != null) {
                        myLogger.atSevere().log(LM.getMessage(LangKey.LOAD_FAILURE_LOC,true, fileName, filePath.toString()).getAnsiMessage());
                    } else {
                        myLogger.atSevere().log("[LOAD] Failed to load " + fileName + " at " + filePath);
                    }
                    return null;
                } else {
                    if (showInfo) {
                        if (LM != null) {
                            myLogger.atInfo().log(LM.getMessage(LangKey.LOAD_SUCCESS,true, fileName).getAnsiMessage());
                        } else {
                            myLogger.atInfo().log("[LOAD] Loaded " + fileName);
                        }
                    }
                    return jsonObject;
                }
            } catch (IOException | JsonSyntaxException e) {
                logError("getJsonObject", e);
                if (LM != null) {
                    myLogger.atSevere().log(LM.getMessage(LangKey.LOAD_FAILURE_LOC,true, fileName, filePath.toString()).getAnsiMessage());
                } else {
                    myLogger.atSevere().log("[LOAD] Failed to load " + fileName + " at " + filePath);
                }
            }
        } else {
            if (LM != null) {
                myLogger.atWarning().log(LM.getMessage(LangKey.FILE_NOT_FOUND_LOC,true, fileName, filePath.toString()).getAnsiMessage());
            } else {
                myLogger.atWarning().log("[ERROR] Could not find " + fileName + " at " + filePath);
            }
        }
        return null;
    }

    // Saving a .json file
    public static <T> void saveJsonFile(Path savePath, T dataObject) {
        saveJsonFile(savePath.getFileName().toString(), savePath, dataObject, false);
    }

    public static <T> void saveJsonFile(String fileName, Path savePath, T dataObject) {
        saveJsonFile(fileName, savePath, dataObject, false);
    }

    public static <T> void saveJsonFile(String fileName, Path savePath, T dataObject, boolean showInfo) {
        if (dataObject == null) {
            if (LM != null) {
                myLogger.atSevere().log(LM.getMessage(LangKey.SAVE_FAILURE_LOC,true, fileName, savePath.toString()).getAnsiMessage());
            } else {
                myLogger.atSevere().log("[SAVE] Failed to save " + fileName + " at " + savePath);
            }
            return;
        }
        try (BufferedWriter writer = Files.newBufferedWriter(savePath, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            GSON.toJson(dataObject, writer);
            if (showInfo) {
                if (LM != null) {
                    myLogger.atInfo().log(LM.getMessage(LangKey.SAVE_SUCCESS_LOC,true, fileName, savePath.toString()).getAnsiMessage());
                } else {
                    myLogger.atInfo().log("[SAVE] Saved " + fileName + " at " + savePath);
                }
            }
        } catch (IOException e) {
            logError("saveJsonFile", e);
            if (LM != null) {
                myLogger.atSevere().log(LM.getMessage(LangKey.SAVE_FAILURE_LOC,true, fileName, savePath.toString()).getAnsiMessage());
            } else {
                myLogger.atSevere().log("[SAVE] Failed to save " + fileName + " at " + savePath);
            }
        }
    }

    // Updating a .json file
    public static <T> void updateJsonFile(Path filePath, T defaultDataObject, boolean showInfo) {
        updateJsonFile(filePath.getFileName().toString(), filePath, defaultDataObject, showInfo);
    }
    public static <T> void updateJsonFile(String fileName, Path filePath, T defaultDataObject, boolean showInfo) {
        if(!Files.exists(filePath)) {
            saveJsonFile(fileName, filePath, defaultDataObject, showInfo);
            return;
        }
        try {
            JsonElement existingJsonTree = getJsonObject(fileName, filePath, false);
            JsonElement defaultJsonTree = GSON.toJsonTree(defaultDataObject);

            if(existingJsonTree == null || !existingJsonTree.isJsonObject() || !defaultJsonTree.isJsonObject()) {
                saveJsonFile(fileName, filePath, defaultDataObject, showInfo);
                return;
            }

            boolean changed = syncJsonObjects(existingJsonTree.getAsJsonObject(), defaultJsonTree.getAsJsonObject());
            if(changed) {
                saveJsonFile(fileName, filePath, existingJsonTree, false);
                if(showInfo) {
                    if(LM != null) {
                        myLogger.atInfo().log(LM.getMessage(LangKey.UPDATE_SUCCESS, true, fileName).getAnsiMessage());
                    } else {
                        myLogger.atInfo().log("[UPDATE] Updated " + fileName);
                    }
                }
            }
        } catch (Exception e) {
            logError("updateJsonFile", e);
            if(LM != null) {
                myLogger.atSevere().log(LM.getMessage(LangKey.UPDATE_FAILURE, true, fileName).getAnsiMessage());
            } else {
                myLogger.atInfo().log("[UPDATE] Failed to update " + fileName);
            }
        }
    }

    public static boolean syncJsonObjects(JsonObject existingObj, JsonObject targetObj) {
        boolean changed = false;
        var existingIterator = existingObj.entrySet().iterator();
        while (existingIterator.hasNext()) {
            var entry = existingIterator.next();
            if (!targetObj.has(entry.getKey())) {
                existingIterator.remove();
                changed = true;
            }
        }
        for (var entry : targetObj.entrySet()) {
            String key = entry.getKey();
            JsonElement targetVal = entry.getValue();
            if (!existingObj.has(key)) {
                existingObj.add(key, targetVal);
                changed = true;
                continue;
            }
            JsonElement existingVal = existingObj.get(key);
            if (existingVal.isJsonObject() && targetVal.isJsonObject()) {
                if (syncJsonObjects(existingVal.getAsJsonObject(), targetVal.getAsJsonObject())) {
                    changed = true;
                }
            }
            else if (!isSameJsonType(existingVal, targetVal)) {
                existingObj.add(key, targetVal);
                changed = true;
            }
        }
        return changed;
    }

    private static boolean isSameJsonType(JsonElement elementA, JsonElement elementB) {
        if (elementA.isJsonObject() && elementB.isJsonObject()) return true;
        if (elementA.isJsonArray() && elementB.isJsonArray()) return true;
        if (elementA.isJsonPrimitive() && elementB.isJsonPrimitive()) return true;
        return elementA.isJsonNull() && elementB.isJsonNull();
    }

    // Catching errors
    public static void logError(Exception e) {
        logError(ERROR_LOG_DIRECTORY, "undefined", e);
    }

    public static void logError(String at, Exception e) {
        logError(ERROR_LOG_DIRECTORY, at, e);
    }

    public static void logError(Path exportPath, String at, Exception e) {
        if (!Files.exists(exportPath)) {
            try {
                Files.createDirectories(exportPath);
            } catch (IOException ignored) {
            }
        }
        int ID = random.nextInt(900) + 100;
        String fileName = TimeUtils.getFileSafeTime() + "-" + at + "-log_" + ID + ".txt";
        Path logFile = exportPath.resolve(fileName);
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(logFile, StandardCharsets.UTF_8))) {
            writer.println("========== ERROR LOG ==========");
            writer.println("Timestamp: " + TimeUtils.getTimeNow());
            writer.println("Location:  " + at);
            writer.println("Error Msg: " + e.getMessage());
            writer.println();
            writer.println("--- Full Stack Trace ---");
            e.printStackTrace(writer);
            writer.println("===============================");

            if (LM != null) {
                myLogger.atWarning().log(LM.getMessage(LangKey.SAVE_SUCCESS_LOC,true, fileName, logFile.toString()).getAnsiMessage());
            } else {
                System.out.println("Saved error log to: " + logFile.toAbsolutePath());
            }
        } catch (IOException writeEx) {
            if (LM != null) {
                myLogger.atSevere().log(LM.getMessage(LangKey.SAVE_FAILURE, true, "error log").getAnsiMessage());
            } else {
                myLogger.atSevere().log("Failed to save error log.");
            }
            writeEx.printStackTrace();
        }
    }
    private int getTotalFilesInDir(Path location) {
        int count = 0;
        if(Files.exists(location)) {
            for(File file : Objects.requireNonNull(location.toFile().listFiles())) {
                if(!file.isDirectory()) {
                    count++;
                }
            }
            return count;
        } else {
            return 0;
        }
    }
}
