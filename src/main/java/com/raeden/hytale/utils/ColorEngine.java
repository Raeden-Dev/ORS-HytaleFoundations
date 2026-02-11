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
    private final String COLOR_FILE = "colormap.json";
    private List<String> SPECIAL_CODES;
    private LinkedHashMap<String, String> COLOR_MAP = new LinkedHashMap<>();

    private static final Pattern CODE_PATTERN = Pattern.compile("^&[0-9a-zA-Z]$");
    private static final Pattern HEX_PATTERN = Pattern.compile("^#([A-Fa-f0-9]{6})$");

    public ColorEngine(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        colorFilePath = hytaleFoundations.getDataDirectory().resolve(COLOR_FILE);
        initializeColorEngine();
    }

    public void initializeColorEngine() {
        if(!Files.exists(colorFilePath)) {
            myLogger.atInfo().log(langManager.getMessage(LangKey.CREATE_SUCCESS, COLOR_FILE, colorFilePath.toString()).getAnsiMessage());
            saveColorFile(true);
        } else {
            loadColors();
        }

        addSpecialCodes();
    }

    // Default Colors & Codes
    private void addSpecialCodes() {
        // Bold and Italic
        SPECIAL_CODES = new ArrayList<>(List.of("&l", "&o", "&r"));
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

    // Saving and Loading
    public void saveColorFile(boolean loadDefault) {
        if(loadDefault) {
            loadDefaultColors();
        }
        COLOR_MAP_FILE colorMapFile = new COLOR_MAP_FILE();
        colorMapFile.setColorList(COLOR_MAP);
        saveJsonFile(COLOR_FILE, colorFilePath, colorMapFile, true);
    }
    public void loadColors() {
        if(Files.exists(colorFilePath)) {
            Type type = new TypeToken<LinkedHashMap<String, String>>(){}.getType();
            COLOR_MAP_FILE colorMapFile = loadJsonFile(COLOR_FILE, colorFilePath, type, true);

            if(colorMapFile == null) {
                saveColorFile(true);
                return;
            }

            if(colorMapFile.getColorList().isEmpty()) {
                saveColorFile(true);
            } else {
                int newColors = 0;
                LinkedHashMap<String, String> defaultColors = getDefaultColors();
                LinkedHashMap<String, String> obtainedColormap = colorMapFile.getColorList();

                // Validate if default colors exist or not
                for(Map.Entry<String, String> defaultColor : defaultColors.entrySet()) {
                    if(!COLOR_MAP.containsKey(defaultColor.getKey())) {
                        COLOR_MAP.put(defaultColor.getKey(), defaultColor.getValue());
                    }
                }
                // Get the obtained colors from colormap.json
                for(Map.Entry<String, String> color : obtainedColormap.entrySet()) {
                    if(!validateColor(color.getKey(), color.getValue())) {
                        myLogger.atWarning().log(langManager.getMessage(LangKey.INVALID_COLOR_FORMAT, color.getKey(), color.getValue()).getAnsiMessage());
                        continue;
                    }
                    if(!COLOR_MAP.containsKey(color.getKey())) {
                        newColors++;
                        COLOR_MAP.put(color.getKey(), color.getValue());
                    }
                }

                if(newColors != 0) {
                    myLogger.atInfo().log(langManager.getMessage(LangKey.LOAD_FILE, newColors + "colors!").getAnsiMessage());
                }
            }
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
        return formatMessage(message);
    }
    private Message formatMessage(String text) {
        if (text == null || text.isEmpty()) return Message.empty();
        Message finalMessage = Message.empty();
        String[] splitMessage = text.split("(?=&)");

        List<String> activeColors = new ArrayList<>();
        activeColors.add(DefaultColors.WHITE.getHex());
        boolean isBold = false;
        boolean isItalic = false;

        for(String segment : splitMessage) {
            if(!segment.startsWith("&") || segment.length() < 2) {
                String color = activeColors.isEmpty() ? DefaultColors.WHITE.getHex() : activeColors.getFirst();
                finalMessage.insert(Message.raw(segment).color(color).bold(isBold).italic(isItalic));
                continue;
            }
            String code = segment.substring(0, 2);
            String content = segment.substring(2);
            boolean isSpecial = SPECIAL_CODES.contains(code);
            boolean isColor = COLOR_MAP.containsKey(code);

            if (!isSpecial && !isColor) {
                String color = activeColors.isEmpty() ? DefaultColors.WHITE.getHex() : activeColors.getLast();
                finalMessage.insert(Message.raw(segment).color(color).bold(isBold).italic(isItalic));
                continue;
            }

            if (isSpecial) {
                if (code.equalsIgnoreCase("&l")) isBold = true;
                else if (code.equalsIgnoreCase("&o")) isItalic = true;
                else if (code.equalsIgnoreCase("&r")) {
                    activeColors.clear();
                    activeColors.add(DefaultColors.WHITE.getHex());
                    isBold = false;
                    isItalic = false;
                }
            } else {
                if (activeColors.size() == 1 && activeColors.getFirst().equals(DefaultColors.WHITE.getHex())) {
                    activeColors.clear();
                }
                activeColors.add(COLOR_MAP.get(code));
            }

            if (!content.isEmpty()) {
                if (activeColors.isEmpty()) activeColors.add(DefaultColors.WHITE.getHex());
                if (activeColors.size() > 1) {
                    finalMessage.insert(gradient(content, new ArrayList<>(activeColors), isBold, isItalic));
                    String lastColor = activeColors.getLast();
                    activeColors.clear();
                    activeColors.add(lastColor);
                } else {
                    finalMessage.insert(Message.raw(content).color(activeColors.getFirst()).bold(isBold).italic(isItalic));
                }
            }
        }

        return finalMessage;
    }

    // Helpers
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

    private boolean isColorCodeAvailable(String code) {
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

    public List<String> getSSpecialCodes() {return SPECIAL_CODES;}

    // Color Map Class
    private static final class COLOR_MAP_FILE {
        private LinkedHashMap<String, String> colorList = new LinkedHashMap<>();

        public LinkedHashMap<String, String> getColorList() {return colorList;}
        public void setColorList(LinkedHashMap<String, String> colorList) {this.colorList = colorList;}
    }

}
