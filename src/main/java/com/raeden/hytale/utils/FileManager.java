package com.raeden.hytale.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.raeden.hytale.lang.LangKey;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static com.raeden.hytale.HytaleFoundations.*;

public class FileManager {
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
        if(Files.exists(filePath)) {
            try {
                String readFile = Files.readString(filePath, StandardCharsets.UTF_8);
                T myObj = GSON.fromJson(readFile, typeOfT);

                if(myObj == null) {
                    if(langManager != null) {
                        myLogger.atSevere().log(langManager.getMessage(LangKey.LOAD_FAILURE_W_LOC, fileName, filePath.toString()).getAnsiMessage());
                    } else {
                        myLogger.atSevere().log("[LOAD] Failed to load " + fileName + " at " + filePath);
                    }
                    return null;
                } else {
                    if(showInfo) {
                        if(langManager != null) {
                            myLogger.atInfo().log(langManager.getMessage(LangKey.LOAD_FILE, fileName).getAnsiMessage());
                        } else {
                            myLogger.atInfo().log("[LOAD] Loaded " + fileName);
                        }
                    }
                    return myObj;
                }
            } catch (IOException | JsonSyntaxException e) {
                if(langManager != null) {
                    myLogger.atSevere().log(langManager.getMessage(LangKey.READ_FAILURE_W_LOC, fileName, filePath.toString()).getAnsiMessage());
                } else {
                    myLogger.atSevere().log("[READ] Failed to read " + fileName + " at " + filePath);
                }
            }
        } else {
            if(langManager != null) {
                myLogger.atWarning().log(langManager.getMessage(LangKey.FILE_NOT_FOUND_W_LOC, fileName, filePath.toString()).getAnsiMessage());
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
        if(Files.exists(filePath)) {
            try {
                String readFile = Files.readString(filePath, StandardCharsets.UTF_8);
                JsonObject jsonObject = JsonParser.parseString(readFile).getAsJsonObject();

                if(jsonObject == null) {
                    if(langManager != null) {
                        myLogger.atSevere().log(langManager.getMessage(LangKey.LOAD_FAILURE_W_LOC, fileName, filePath.toString()).getAnsiMessage());
                    } else {
                        myLogger.atSevere().log("[LOAD] Failed to load " + fileName + " at " + filePath);
                    }
                    return null;
                } else {
                    if(showInfo) {
                        if(langManager != null) {
                            myLogger.atInfo().log(langManager.getMessage(LangKey.LOAD_FILE, fileName).getAnsiMessage());
                        } else {
                            myLogger.atInfo().log("[LOAD] Loaded " + fileName);
                        }
                    }
                    return jsonObject;
                }
            } catch (IOException | JsonSyntaxException e) {
                if(langManager != null) {
                    myLogger.atSevere().log(langManager.getMessage(LangKey.LOAD_FAILURE_W_LOC, fileName, filePath.toString()).getAnsiMessage());
                } else {
                    myLogger.atSevere().log("[LOAD] Failed to load " + fileName + " at " + filePath);
                }
            }
        } else {
            if(langManager != null) {
                myLogger.atWarning().log(langManager.getMessage(LangKey.FILE_NOT_FOUND_W_LOC, fileName, filePath.toString()).getAnsiMessage());
            } else {
                myLogger.atWarning().log("[ERROR] Could not find " + fileName + " at " + filePath);
            }
        }

        return null;
    }
    // Saving a .json file
    public static <T> void saveJsonFile(Path savePath, T dataObject) {
        saveJsonFile(savePath.getFileName().toString(), savePath, dataObject, false, false);
    }
    public static <T> void saveJsonFile(String fileName, Path savePath, T dataObject) {
        saveJsonFile(fileName, savePath, dataObject, false, false);
    }
    public static <T> void saveJsonFile(String fileName, Path savePath, T dataObject, boolean showInfo) {
        saveJsonFile(fileName, savePath, dataObject, showInfo, false);
    }
    public static <T> void saveJsonFile(String fileName, Path savePath, T dataObject, boolean showInfo, boolean additionalSaveSettings) {
        if(dataObject == null) {
            myLogger.atSevere().log(langManager.getMessage(LangKey.SAVE_FAILURE_W_LOC, fileName, savePath.toString()).getAnsiMessage());
            return;
        }
        String toJson = GSON.toJson(dataObject);
        try {
            if(!additionalSaveSettings) {
                Files.writeString(savePath, toJson, StandardCharsets.UTF_8);
            } else {
                Files.writeString(savePath, toJson, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
            if(showInfo) {
                if(langManager != null) {
                    myLogger.atInfo().log(langManager.getMessage(LangKey.SAVE_W_LOC, fileName, savePath.toString()).getAnsiMessage());
                } else {
                    myLogger.atInfo().log("[SAVE] Saved " + fileName + " at " + savePath);
                }
            }
        } catch (IOException e) {
            if(langManager != null) {
                myLogger.atSevere().log(langManager.getMessage(LangKey.SAVE_FAILURE_W_LOC, fileName, savePath.toString()).getAnsiMessage());
            } else {
                myLogger.atSevere().log("[SAVE] Failed to save " + fileName + " at " + savePath);
            }
        }
    }
}
