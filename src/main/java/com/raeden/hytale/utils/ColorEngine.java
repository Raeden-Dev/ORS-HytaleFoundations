package com.raeden.hytale.utils;

import com.google.gson.reflect.TypeToken;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.utils.Permissions;
import com.raeden.hytale.lang.LangKey;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.raeden.hytale.HytaleFoundations.*;
import static com.raeden.hytale.core.utils.Permissions.hasPermission;
import static com.raeden.hytale.core.utils.Permissions.isPlayerAdmin;
import static com.raeden.hytale.utils.FileManager.loadJsonFile;
import static com.raeden.hytale.utils.FileManager.saveJsonFile;

public class ColorEngine {
    // Map for all colors
    private final HytaleFoundations hytaleFoundations;
    private final Path colorFilePath;
    private final String COLOR_FILE_NAME = "colormap.json";
    private List<String> SPECIAL_CODES;
    private LinkedHashMap<String, String> COLOR_MAP = new LinkedHashMap<>();

    private static final Pattern CODE_PATTERN = Pattern.compile("^&[0-9a-zA-Z]$");
    private static final Pattern HEX_PATTERN = Pattern.compile("^#([A-Fa-f0-9]{6})$");

    public ColorEngine(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        colorFilePath = hytaleFoundations.getDataDirectory().resolve(COLOR_FILE_NAME);
        initializeColorEngine();
    }

    public void initializeColorEngine() {
        SPECIAL_CODES = new ArrayList<>(List.of("&l", "&o", "&r"));
        loadDefaultColors();
        if(!Files.exists(colorFilePath)) {
            myLogger.atInfo().log(langManager.getMessage(LangKey.CREATE_SUCCESS, true, COLOR_FILE_NAME, colorFilePath.toString()).getAnsiMessage());
            saveColorFile(); // Just save the defaults we just loaded
        } else {
            loadColors();
        }
    }
    private void loadDefaultColors() {
        for(DefaultColors color : DefaultColors.values()) {
            COLOR_MAP.put(color.getCode(), color.getHex());
        }
    }
    // Saving and Loading
    public void saveColorFile() {
        ColormapHolder colorMapFile = new ColormapHolder();
        colorMapFile.setColorList(COLOR_MAP);
        saveJsonFile(COLOR_FILE_NAME, colorFilePath, colorMapFile, true);
        myLogger.atInfo().log(langManager.getMessage(LangKey.LOAD_SUCCESS, COLOR_MAP.size() + " colors!").getAnsiMessage());
    }
    public void loadColors() {
        Type type = new TypeToken<ColormapHolder>(){}.getType();
        ColormapHolder colorMapFile = loadJsonFile(COLOR_FILE_NAME, colorFilePath, type, true);
        if(colorMapFile == null || colorMapFile.getColorList() == null) {
            saveColorFile();
            return;
        }
        LinkedHashMap<String, String> obtainedColormap = colorMapFile.getColorList();
        int newColors = 0;
        int overwrittenColors = 0;
        for(Map.Entry<String, String> entry : obtainedColormap.entrySet()) {
            String code = entry.getKey();
            String hex = entry.getValue();
            if(!validateColor(code, hex)) {
                myLogger.atWarning().log(langManager.getMessage(LangKey.INVALID_COLOR_FORMAT, true, code, hex).getAnsiMessage());
                continue;
            }
            if(!COLOR_MAP.containsKey(code)) {
                newColors++;
            } else if (!COLOR_MAP.get(code).equalsIgnoreCase(hex)) {
                overwrittenColors++;
            }
            COLOR_MAP.put(code, hex);
        }
        if(newColors > 0 || overwrittenColors > 0) {
            String msg = String.format("%d custom colors and %d overrides loaded.", newColors, overwrittenColors);
            myLogger.atInfo().log(langManager.getMessage(LangKey.LOAD_SUCCESS, true, msg).getAnsiMessage());
        }
    }

    // Parsing a Message
    public Message parseText(String message) {
        return parseText(null, message, false);
    }
    public Message parseText(PlayerRef playerRef, String message) {
        return parseText(playerRef, message, false);
    }
    public Message parseText(String message, boolean isConsole) {
        return parseText(null, message, isConsole);
    }
    public Message parseText(PlayerRef playerRef, String message, boolean isConsole) {
        // No colors for chat [Not Admin, No Permission, Turned off]
        if(playerRef != null) {
            boolean isAdmin = isPlayerAdmin(playerRef);
            boolean configAllow = hytaleFoundations.getConfigManager().getDefaultChatConfig().isAllowPlayerChatColors();
            boolean hasPermission = hasPermission(playerRef, Permissions.HFPermissions.CHAT_COLORS.getPermission());
            if (!isAdmin && (!configAllow || !hasPermission)) {
                return Message.raw(stripTextOfColorCodes(message)).color(DefaultColors.WHITE.getHex());
            }
        }
        if(isConsole) {
            return Message.raw(stripTextOfColorCodes(message)).color(DefaultColors.WHITE.getHex());
        }
        return colorMessage(message);
    }
    private Message colorMessage(String text) {
        if (text == null || text.isEmpty()) return Message.empty();
        Message finalMessage = Message.empty();
        int length = text.length();

        List<String> activeColors = new ArrayList<>(4);
        activeColors.add(DefaultColors.WHITE.getHex());
        boolean isBold = false;
        boolean isItalic = false;

        StringBuilder currentContent = new StringBuilder();
        for(int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if(c == '&' && i+1 < length) {
                String codeKey = "&" + text.charAt(i + 1);
                boolean isColor = COLOR_MAP.containsKey(codeKey);
                boolean isSpecial = SPECIAL_CODES.contains(codeKey);

                if(isColor || isSpecial) {
                    if(!currentContent.isEmpty()) {
                        appendSegment(finalMessage, currentContent.toString(), activeColors, isBold, isItalic);
                        currentContent.setLength(0);
                    }
                    if(isSpecial) {
                        if (codeKey.equalsIgnoreCase("&l")) isBold = true;
                        else if (codeKey.equalsIgnoreCase("&o")) isItalic = true;
                        else if (codeKey.equalsIgnoreCase("&r")) {
                            activeColors.clear();
                            activeColors.add(DefaultColors.WHITE.getHex());
                            isBold = false;
                            isItalic = false;
                        }
                    } else {
                        if (activeColors.size() == 1 && activeColors.getFirst().equals(DefaultColors.WHITE.getHex())) {
                            activeColors.clear();
                        }
                        activeColors.add(COLOR_MAP.get(codeKey));
                    }
                    i++;
                    continue;
                }
            }
            currentContent.append(c);
        }
        if (!currentContent.isEmpty()) {
            appendSegment(finalMessage, currentContent.toString(), activeColors, isBold, isItalic);
        }
        return finalMessage;
    }

    // Helpers
    private void appendSegment(Message builder, String content, List<String> colors, boolean bold, boolean italic) {
        if(colors.isEmpty()) colors.add(DefaultColors.WHITE.getHex());
        if(colors.size() > 1) {
            builder.insert(gradient(content, colors, bold, italic));
            String lastColor = colors.getLast();
            colors.clear();
            colors.add(lastColor);
        } else {
            builder.insert(Message.raw(content).color(colors.getFirst()).bold(bold).italic(italic));
        }
    }
    public Message gradient(String text, List<String> hexColors) {
        return gradient(text, hexColors, false, false);
    }
    public Message gradient(String text, List<String> hexColors, boolean bold) {
        return gradient(text, hexColors, bold, false);
    }
    public Message gradient(String text, List<String> hexColors, boolean bold, boolean italic) {
        Message builder = Message.empty();
        int length = text.length();
        if(hexColors == null || hexColors.isEmpty()) {
            return Message.raw(text).color(DefaultColors.WHITE.getHex()).bold(bold).italic(italic);
        }
        if (hexColors.size() == 1 || length == 0) {
            return Message.raw(text).color(hexColors.getFirst()).bold(bold);
        }

        for (int i = 0; i < length; i++) {
            double overallProgress = (length > 1) ? (double) i / (length - 1) : 0;
            double segmentWeight = 1.0 / (hexColors.size() - 1);
            int segmentIndex = (int) (overallProgress / segmentWeight);
            if (segmentIndex >= hexColors.size() - 1) segmentIndex = hexColors.size() - 2;
            double localProgress = (overallProgress - (segmentIndex * segmentWeight)) / segmentWeight;
            int[] startRGB = hexToRGB(hexColors.get(segmentIndex));
            int[] endRGB = hexToRGB(hexColors.get(segmentIndex + 1));

            String colorForChar = interpolateColor(startRGB, endRGB, localProgress);

            builder.insert(Message.raw(String.valueOf(text.charAt(i)))
                    .color(colorForChar)
                    .bold(bold)
                    .italic(italic));
        }

        return builder;
    }
    public String stripTextOfColorCodes(String text) {
        StringBuilder builder = new StringBuilder();
        String[] splitMessage = text.split("(?=&)");
        for(String msg : splitMessage) {
            if(msg.length() >= 2 && isColorCodeAvailable(msg.substring(0, 2))) {
                builder.append(msg.substring(2));
            } else {
                builder.append(msg);
            }
        }
        return builder.toString();
    }

    public boolean isColorCodeAvailable(String code) {
        if(!isValidColorCode(code)) return false;
        return COLOR_MAP.containsKey(code) || SPECIAL_CODES.contains(code);
    }
    private boolean isValidColorCode(String code) {
        return CODE_PATTERN.matcher(code).matches();
    }
    private boolean isValidHexCode(String hex) {
        return HEX_PATTERN.matcher(hex).matches();
    }

    public boolean validateColor(String code, String hex) {
        return isValidColorCode(code) && isValidHexCode(hex);
    }

    private String interpolateColor(int[] start, int[] end, double ratio) {
        int r = (int) (start[0] + (end[0] - start[0]) * ratio);
        int g = (int) (start[1] + (end[1] - start[1]) * ratio);
        int b = (int) (start[2] + (end[2] - start[2]) * ratio);

        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));

        return String.format("#%02X%02X%02X", r, g, b);
    }

    private int[] hexToRGB(String hex) {
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

    // Getters and Setters
    public LinkedHashMap<String, String> getColorMap() {return COLOR_MAP;}
    public void setColorMap(LinkedHashMap<String, String> COLOR_MAP) {this.COLOR_MAP = COLOR_MAP;}
    public List<String> getSpecialCodes() {return SPECIAL_CODES;}
    public String getColorFile() { return COLOR_FILE_NAME;}
    public Path getColorFilePath() { return colorFilePath;}
    // Color Map Class
    public static final class ColormapHolder {
        private final String VERSION = "v1.0";
        private LinkedHashMap<String, String> COLOR_LIST = new LinkedHashMap<>();

        public LinkedHashMap<String, String> getColorList() {return COLOR_LIST;}
        public void setColorList(LinkedHashMap<String, String> colorList) {this.COLOR_LIST = colorList;}
        public String getVersion() {return VERSION;}
    }

}
