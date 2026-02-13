package com.raeden.hytale.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.raeden.hytale.lang.LangKey;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;

import static com.raeden.hytale.HytaleFoundations.*;

public class FileManager {
    // Creating a directory
    public static void createDirectory(Path directoryPath, boolean showInfo) {
        if (Files.exists(directoryPath)) return;

        String fileName = "[" + directoryPath.getFileName().toString() + "]";
        try {
            Files.createDirectories(directoryPath);
            if (showInfo) {
                if (langManager != null) {
                    myLogger.atInfo().log(langManager.getMessage(LangKey.DIR_CREATE_SUCCESS_LOC,true, fileName, directoryPath.toString()).getAnsiMessage());
                } else {
                    myLogger.atInfo().log("[DIR] Created %s directory at %s", fileName, directoryPath.toString());
                }
            }
        } catch (IOException e) {
            logExceptionError("createDirectory", e);
            if (langManager != null) {
                myLogger.atSevere().log(langManager.getMessage(LangKey.DIR_CREATE_FAIL_LOC,true, fileName, directoryPath.toString()).getAnsiMessage());
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
                    if (langManager != null) {
                        myLogger.atSevere().log(langManager.getMessage(LangKey.LOAD_FAILURE_LOC,true, fileName, filePath.toString()).getAnsiMessage());
                    } else {
                        myLogger.atSevere().log("[LOAD] Failed to load " + fileName + " at " + filePath);
                    }
                    return null;
                } else {
                    if (showInfo) {
                        if (langManager != null) {
                            myLogger.atInfo().log(langManager.getMessage(LangKey.LOAD_SUCCESS,true, fileName).getAnsiMessage());
                        } else {
                            myLogger.atInfo().log("[LOAD] Loaded " + fileName);
                        }
                    }
                    return myObj;
                }
            } catch (IOException | JsonSyntaxException e) {
                logExceptionError("loadJsonFile", e);
                if (langManager != null) {
                    myLogger.atSevere().log(langManager.getMessage(LangKey.READ_FAILURE_LOC,true, fileName, filePath.toString()).getAnsiMessage());
                } else {
                    myLogger.atSevere().log("[READ] Failed to read " + fileName + " at " + filePath);
                }
            }
        } else {
            if (langManager != null) {
                myLogger.atWarning().log(langManager.getMessage(LangKey.FILE_NOT_FOUND_LOC,true, fileName, filePath.toString()).getAnsiMessage());
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
                    if (langManager != null) {
                        myLogger.atSevere().log(langManager.getMessage(LangKey.LOAD_FAILURE_LOC,true, fileName, filePath.toString()).getAnsiMessage());
                    } else {
                        myLogger.atSevere().log("[LOAD] Failed to load " + fileName + " at " + filePath);
                    }
                    return null;
                } else {
                    if (showInfo) {
                        if (langManager != null) {
                            myLogger.atInfo().log(langManager.getMessage(LangKey.LOAD_SUCCESS,true, fileName).getAnsiMessage());
                        } else {
                            myLogger.atInfo().log("[LOAD] Loaded " + fileName);
                        }
                    }
                    return jsonObject;
                }
            } catch (IOException | JsonSyntaxException e) {
                logExceptionError("getJsonObject", e);
                if (langManager != null) {
                    myLogger.atSevere().log(langManager.getMessage(LangKey.LOAD_FAILURE_LOC,true, fileName, filePath.toString()).getAnsiMessage());
                } else {
                    myLogger.atSevere().log("[LOAD] Failed to load " + fileName + " at " + filePath);
                }
            }
        } else {
            if (langManager != null) {
                myLogger.atWarning().log(langManager.getMessage(LangKey.FILE_NOT_FOUND_LOC,true, fileName, filePath.toString()).getAnsiMessage());
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
            if (langManager != null) {
                myLogger.atSevere().log(langManager.getMessage(LangKey.SAVE_FAILURE_LOC,true, fileName, savePath.toString()).getAnsiMessage());
            } else {
                myLogger.atSevere().log("[SAVE] Failed to save " + fileName + " at " + savePath);
            }
            return;
        }
        try (BufferedWriter writer = Files.newBufferedWriter(savePath, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            GSON.toJson(dataObject, writer);
            if (showInfo) {
                if (langManager != null) {
                    myLogger.atInfo().log(langManager.getMessage(LangKey.SAVE_SUCCESS_LOC,true, fileName, savePath.toString()).getAnsiMessage());
                } else {
                    myLogger.atInfo().log("[SAVE] Saved " + fileName + " at " + savePath);
                }
            }
        } catch (IOException e) {
            logExceptionError("saveJsonFile", e);
            if (langManager != null) {
                myLogger.atSevere().log(langManager.getMessage(LangKey.SAVE_FAILURE_LOC,true, fileName, savePath.toString()).getAnsiMessage());
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
        if (!Files.exists(exportPath)) {
            try {
                Files.createDirectories(exportPath);
            } catch (IOException ignored) {
            }
        }
        String fileName = TimeUtils.getFileSafeTime() + "-" + at + "-log.txt";
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

            if (langManager != null) {
                myLogger.atWarning().log(langManager.getMessage(LangKey.SAVE_SUCCESS_LOC,true, fileName, logFile.toString()).getAnsiMessage());
            } else {
                System.out.println("Saved error log to: " + logFile.toAbsolutePath());
            }
        } catch (IOException writeEx) {
            if (langManager != null) {
                myLogger.atSevere().log(langManager.getMessage(LangKey.SAVE_FAILURE, true, "error log").getAnsiMessage());
            } else {
                myLogger.atSevere().log("Failed to save error log.");
            }
            writeEx.printStackTrace();
        }
    }
}
