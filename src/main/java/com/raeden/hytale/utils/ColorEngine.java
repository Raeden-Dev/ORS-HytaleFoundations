package com.raeden.hytale.utils;

import com.hypixel.hytale.server.core.Message;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.lang.LangKey;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.raeden.hytale.HytaleFoundations.*;

public class ColorEngine {
    // Map for all colors
    private final Path colorFilePath;
    private final String COLOR_FILE = "colormap.json";
    private LinkedHashMap<String, String> COLOR_MAP = new LinkedHashMap<>();

    public static Message color(String text, String hexColor) {
        return Message.raw(text).color(hexColor).bold(false);
    }
    public static Message color(String text, String hexColor, boolean isBold) {
        return Message.raw(text).color(hexColor).bold(isBold);
    }

    public ColorEngine(HytaleFoundations hytaleFoundations) {
        colorFilePath = hytaleFoundations.getDataDirectory().resolve(COLOR_FILE);
        initializeColorEngine();
    }

    public void initializeColorEngine() {
        if(!Files.exists(colorFilePath)) {
            myLogger.atInfo().log(langManager.getMessage(null, LangKey.CREATE_SUCCESS, COLOR_FILE, colorFilePath.toString()).getAnsiMessage());
            saveColorFile(true);
        } else {
            loadColors();
        }
    }

    private LinkedHashMap<String, String> getDefaultColors() {
        LinkedHashMap<String, String> colorMap = new LinkedHashMap<>();
        for (DefaultColors color : DefaultColors.values()) {
            colorMap.put(color.getCode(), color.getHex());
        }
        return colorMap;
    }
    private void loadDefaultColors() {
        for(DefaultColors color : DefaultColors.values()) {
            COLOR_MAP.put(color.getCode(), color.getHex());
        }
    }

    public void saveColorFile(boolean loadDefault) {
        if(loadDefault) {
            loadDefaultColors();
        }
        COLOR_MAP_FILE colorMapFile = new COLOR_MAP_FILE();
        colorMapFile.setColorList(COLOR_MAP);
        String toJson = GSON.toJson(colorMapFile);
        try {
            Files.writeString(colorFilePath, toJson, StandardCharsets.UTF_8);
        } catch (IOException e) {
            myLogger.atSevere().log(langManager.getMessage(null, LangKey.SAVE_FAILURE_W_LOC, COLOR_FILE, "data directory").getAnsiMessage());
        }
    }

    public void loadColors() {
        if(Files.exists(colorFilePath)) {
            try {
                String readColormap = Files.readString(colorFilePath, StandardCharsets.UTF_8);
                COLOR_MAP_FILE colorMapFile = GSON.fromJson(readColormap, COLOR_MAP_FILE.class);

                if(colorMapFile.getColorList().isEmpty()) {
                    saveColorFile(true);
                } else {
                    int newColors = 0;
                    LinkedHashMap<String, String> defaultColors = getDefaultColors();
                    LinkedHashMap<String, String> obtainedColormap = colorMapFile.getColorList();
                    for(Map.Entry<String, String> color : obtainedColormap.entrySet()) {
                        if(!defaultColors.containsKey(color.getKey()) && !obtainedColormap.containsKey(color.getKey())) {
                            newColors++;
                            COLOR_MAP.put(color.getKey(), color.getValue());
                        }
                    }

                    if(newColors != 0) {
                        myLogger.atInfo().log(langManager.getMessage(null, LangKey.LOAD_FILE, newColors + "colors!").getAnsiMessage());
                    }
                }
            } catch (IOException e) {
                myLogger.atSevere().log(langManager.getMessage(null, LangKey.READ_FAILURE_W_LOC, COLOR_FILE, colorFilePath.toString()).getAnsiMessage());
            }
        }
    }

    public static Message gradient(String text, String startHex, String endHex, boolean bold) {
        Message builder = Message.empty();

        int length = text.length();
        int[] startRGB = hexToRGB(startHex);
        int[] endRGB = hexToRGB(endHex);

        for (int i = 0; i < length; i++) {
            double ratio = (length > 1) ? (double) i / (length - 1) : 0;
            String colorForChar = interpolateColor(startRGB, endRGB, ratio);
            Message charMsg = Message.raw(String.valueOf(text.charAt(i))).color(colorForChar);

            if (bold) charMsg = charMsg.bold(true);
            builder.insert(charMsg);
        }

        return builder;
    }

    private static String interpolateColor(int[] start, int[] end, double ratio) {
        int r = (int) (start[0] + (end[0] - start[0]) * ratio);
        int g = (int) (start[1] + (end[1] - start[1]) * ratio);
        int b = (int) (start[2] + (end[2] - start[2]) * ratio);

        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));

        return String.format("#%02X%02X%02X", r, g, b);
    }

    private static int[] hexToRGB(String hex) {
        if (hex.startsWith("#")) hex = hex.substring(1);
        try {
            return new int[]{
                    Integer.valueOf(hex.substring(0, 2), 16),
                    Integer.valueOf(hex.substring(2, 4), 16),
                    Integer.valueOf(hex.substring(4, 6), 16)
            };
        } catch (Exception e) {
            return new int[]{255, 255, 255};
        }
    }

    public LinkedHashMap<String, String> getColorMap() {return COLOR_MAP;}
    public void setColorMap(LinkedHashMap<String, String> COLOR_MAP) {this.COLOR_MAP = COLOR_MAP;}

    private static final class COLOR_MAP_FILE {
        private LinkedHashMap<String, String> colorList = new LinkedHashMap<>();

        public LinkedHashMap<String, String> getColorList() {return colorList;}
        public void setColorList(LinkedHashMap<String, String> colorList) {this.colorList = colorList;}
    }

}
