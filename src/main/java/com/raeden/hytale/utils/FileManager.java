package com.raeden.hytale.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.raeden.hytale.lang.LangKey;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static com.raeden.hytale.HytaleFoundations.*;

public class FileManager {
    // Creating a directory
    public static void createDirectory(Path directoryPath, boolean showInfo) {
        if(Files.exists(directoryPath)) {
            return;
        }
        String fileName = directoryPath.getFileName().toString();
        try {
            Files.createDirectories(directoryPath);
            if (showInfo)  myLogger.atInfo().log(langManager.getMessage(LangKey.DIR_CREATE_SUCCESS_LOC, fileName, directoryPath.toString()).getAnsiMessage());
        } catch (IOException e) {
            logExceptionError(fileName, e);
            myLogger.atInfo().log(langManager.getMessage(LangKey.DIR_CREATE_FAIL_LOC, fileName, directoryPath.toString()).getAnsiMessage());
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
        if(Files.exists(filePath)) {
            try {
                String readFile = Files.readString(filePath, StandardCharsets.UTF_8);
                T myObj = GSON.fromJson(readFile, typeOfT);

                if(myObj == null) {
                    if(langManager != null) {
                        myLogger.atSevere().log(langManager.getMessage(LangKey.LOAD_FAILURE_LOC, fileName, filePath.toString()).getAnsiMessage());
                    } else {
                        myLogger.atSevere().log("[LOAD] Failed to load " + fileName + " at " + filePath);
                    }
                    return null;
                } else {
                    if(showInfo) {
                        if(langManager != null) {
                            myLogger.atInfo().log(langManager.getMessage(LangKey.LOAD_SUCCESS, fileName).getAnsiMessage());
                        } else {
                            myLogger.atInfo().log("[LOAD] Loaded " + fileName);
                        }
                    }
                    return myObj;
                }
            } catch (IOException | JsonSyntaxException e) {
                logExceptionError("loadJsonFile", e);
                if(langManager != null) {
                    myLogger.atSevere().log(langManager.getMessage(LangKey.READ_FAILURE_LOC, fileName, filePath.toString()).getAnsiMessage());
                } else {
                    myLogger.atSevere().log("[READ] Failed to read " + fileName + " at " + filePath);
                }
            }
        } else {
            if(langManager != null) {
                myLogger.atWarning().log(langManager.getMessage(LangKey.FILE_NOT_FOUND_LOC, fileName, filePath.toString()).getAnsiMessage());
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
                        myLogger.atSevere().log(langManager.getMessage(LangKey.LOAD_FAILURE_LOC, fileName, filePath.toString()).getAnsiMessage());
                    } else {
                        myLogger.atSevere().log("[LOAD] Failed to load " + fileName + " at " + filePath);
                    }
                    return null;
                } else {
                    if(showInfo) {
                        if(langManager != null) {
                            myLogger.atInfo().log(langManager.getMessage(LangKey.LOAD_SUCCESS, fileName).getAnsiMessage());
                        } else {
                            myLogger.atInfo().log("[LOAD] Loaded " + fileName);
                        }
                    }
                    return jsonObject;
                }
            } catch (IOException | JsonSyntaxException e) {
                logExceptionError("getJsonObject", e);
                if(langManager != null) {
                    myLogger.atSevere().log(langManager.getMessage(LangKey.LOAD_FAILURE_LOC, fileName, filePath.toString()).getAnsiMessage());
                } else {
                    myLogger.atSevere().log("[LOAD] Failed to load " + fileName + " at " + filePath);
                }
            }
        } else {
            if(langManager != null) {
                myLogger.atWarning().log(langManager.getMessage(LangKey.FILE_NOT_FOUND_LOC, fileName, filePath.toString()).getAnsiMessage());
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
            myLogger.atSevere().log(langManager.getMessage(LangKey.SAVE_FAILURE_LOC, fileName, savePath.toString()).getAnsiMessage());
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
                    myLogger.atInfo().log(langManager.getMessage(LangKey.SAVE_SUCCESS_LOC, fileName, savePath.toString()).getAnsiMessage());
                } else {
                    myLogger.atInfo().log("[SAVE] Saved " + fileName + " at " + savePath);
                }
            }
        } catch (IOException e) {
            logExceptionError("saveJsonFile", e);
            if(langManager != null) {
                myLogger.atSevere().log(langManager.getMessage(LangKey.SAVE_FAILURE_LOC, fileName, savePath.toString()).getAnsiMessage());
            } else {
                myLogger.atSevere().log("[SAVE] Failed to save " + fileName + " at " + savePath);
            }
        }
    }

    // Catching errors
    public static void logExceptionError(Exception e) {
        logExceptionError(errorLogDirectory, "undefined", e);
    }
    public static void logExceptionError(String at, Exception e) {
        logExceptionError(errorLogDirectory, at, e);
    }
    public static void logExceptionError(Path exportPath, String at, Exception e) {
        if(Files.exists(exportPath)) {
            String timeNow = TimeUtils.getTimeNow().replace(":", "-");
            String fileName = timeNow + "-" + at + "-log.txt";
            Path logFile = exportPath.resolve(fileName);
            try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(logFile, StandardCharsets.UTF_8))) {
                writer.println("========== ERROR LOG ==========");
                writer.println("Timestamp: " + timeNow);
                writer.println("Location:  " + at);
                writer.println("Error Msg: " + e.getMessage());
                writer.println();
                writer.println("--- Full Stack Trace ---");
                e.printStackTrace(writer);
                writer.println("===============================");
                myLogger.atWarning().log(langManager.getMessage(LangKey.SAVE_SUCCESS_LOC, fileName, logFile.toString()).getAnsiMessage());
                System.out.println("Saved error log to: " + logFile.toAbsolutePath());
            } catch (IOException writeEx) {
                myLogger.atSevere().log(langManager.getMessage(LangKey.SAVE_FAILURE, "error log").getAnsiMessage());
                writeEx.printStackTrace();
            }

        }
    }
}
