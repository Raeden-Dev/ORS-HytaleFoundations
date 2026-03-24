package com.raeden.hytale.modules.chat;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.raeden.hytale.HytaleFoundations;
import com.raeden.hytale.core.permission.Permissions;
import com.raeden.hytale.core.lang.LangKey;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static com.raeden.hytale.HytaleFoundations.*;
import static com.raeden.hytale.core.config.ConfigManager.*;
import static com.raeden.hytale.utils.FileUtils.loadJsonFile;
import static com.raeden.hytale.utils.FileUtils.saveJsonFile;

public class ColorManager {
    private final HytaleFoundations hytaleFoundations;
    private final String colorFileName = COLORMAP_FILE_NAME;
    private List<String> specialCodes;
    private final Map<String, String> colorMap;

    private static final Pattern CODE_PATTERN = Pattern.compile("^&[0-9a-zA-Z]$");
    private static final Pattern HEX_PATTERN = Pattern.compile("^#([A-Fa-f0-9]{6})$");

    public ColorManager(HytaleFoundations hytaleFoundations) {
        this.hytaleFoundations = hytaleFoundations;
        colorMap = new ConcurrentHashMap<>();
        initializeColorEngine();
    }

    public void initializeColorEngine() {
        specialCodes = new ArrayList<>(List.of("&l", "&o", "&r"));
        colorMap.putAll(getDefaultColors());
        if(!Files.exists(COLORMAP_FILE_PATH)) {
            saveColorFile();
        } else {
            loadColors();
        }
    }
    private  Map<String, String> getDefaultColors() {
        Map<String, String> colors = new ConcurrentHashMap<>();
        for(DefaultColors color : DefaultColors.values()) {
            colors.put(color.getCode(), color.getHex());
        }
        return colors;
    }
    // Saving and Loading
    public void saveColorFile() {
        ColormapHolder colorMapFile = new ColormapHolder();
        colorMapFile.setColorList(colorMap);
        saveJsonFile(colorFileName, COLORMAP_FILE_PATH, colorMapFile, true);
    }
    public void loadColors() {
        Type type = new TypeToken<ColormapHolder>(){}.getType();
        ColormapHolder colorMapFile = loadJsonFile(colorFileName, COLORMAP_FILE_PATH, type, true);
        if(colorMapFile == null || colorMapFile.getColorList() == null) {
            saveColorFile();
            return;
        }
        Map<String, String> obtainedColormap = colorMapFile.getColorList();
        int newColors = 0;
        int overwrittenColors = 0;
        for(Map.Entry<String, String> entry : obtainedColormap.entrySet()) {
            String code = entry.getKey();
            String hex = entry.getValue();
            if(!validateColor(code, hex)) {
                myLogger.atWarning().log(LM.getConsoleMessage(LangKey.INVALID_COLOR_FORMAT, code, hex).getAnsiMessage());
                continue;
            }
            if(!colorMap.containsKey(code)) {
                newColors++;
            } else if (!colorMap.get(code).equalsIgnoreCase(hex)) {
                overwrittenColors++;
            }
            colorMap.put(code, hex);
        }
        if(newColors > 0) myLogger.atInfo().log(LM.getConsoleMessage(LangKey.LOAD_SUCCESS,
                newColors + " new color(s)").getAnsiMessage());
        if(overwrittenColors > 0) myLogger.atInfo().log(LM.getConsoleMessage(LangKey.LOAD_SUCCESS,
            overwrittenColors + "  overwritten color(s)").getAnsiMessage());
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
            boolean isAdmin = hytaleFoundations.getPermissionManager().isPlayerAdmin(playerRef);
            boolean configAllow = hytaleFoundations.getConfigManager().getDefaultChatConfig().isAllowPlayerChatColors();
            boolean hasPermission = hytaleFoundations.getPermissionManager().hasPermission(playerRef, Permissions.CHAT_COLORS.getPermission());
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
        boolean isBold = false;
        boolean isItalic = false;

        boolean ignoreColors = false;
        boolean textAppendedSinceLastColor = false;
        boolean hasActualText = false;

        StringBuilder currentContent = new StringBuilder();
        StringBuilder unappliedCodes = new StringBuilder();
        for(int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if (ignoreColors) {
                if (c == ' ') {
                    ignoreColors = false;
                } else {
                    unappliedCodes.setLength(0);
                    currentContent.append(c);
                    hasActualText = true;
                    textAppendedSinceLastColor = true;
                    continue;
                }
            }
            if(c == '&' && i+1 < length) {
                String codeKey = "&" + text.charAt(i + 1);
                if (codeKey.equals("&-")) {
                    if (hasValidCodeAfter(text, i + 2)) {
                        if(!currentContent.isEmpty()) {
                            appendSegment(finalMessage, currentContent.toString(), activeColors, isBold, isItalic);
                            currentContent.setLength(0);
                        }
                        ignoreColors = true;
                        unappliedCodes.setLength(0);
                        i++;
                        continue;
                    }
                }
                boolean isColor = colorMap.containsKey(codeKey);
                boolean isSpecial = specialCodes.contains(codeKey);
                if(isColor || isSpecial) {
                    if(!currentContent.isEmpty()) {
                        appendSegment(finalMessage, currentContent.toString(), activeColors, isBold, isItalic);
                        currentContent.setLength(0);
                    }
                    unappliedCodes.append(codeKey);
                    if(isSpecial) {
                        if (codeKey.equalsIgnoreCase("&l")) isBold = true;
                        else if (codeKey.equalsIgnoreCase("&o")) isItalic = true;
                        else if (codeKey.equalsIgnoreCase("&r")) {
                            activeColors.clear();
                            isBold = false;
                            isItalic = false;
                        }
                    } else {
                        if (textAppendedSinceLastColor) {
                            activeColors.clear();
                            isBold = false;
                            isItalic = false;
                        }
                        activeColors.add(colorMap.get(codeKey));
                    }
                    textAppendedSinceLastColor = false;
                    i++;
                    continue;
                }
            }

            unappliedCodes.setLength(0);
            currentContent.append(c);
            hasActualText = true;
            textAppendedSinceLastColor = true;
        }

        if (!hasActualText) return Message.raw(text).color(DefaultColors.WHITE.getHex());
        if (!unappliedCodes.isEmpty()) currentContent.append(unappliedCodes);
        if (!currentContent.isEmpty()) appendSegment(finalMessage, currentContent.toString(), activeColors, isBold, isItalic);

        return finalMessage;
    }

    private boolean hasValidCodeAfter(String text, int index) {
        for (int i = index; i < text.length() - 1; i++) {
            if (text.charAt(i) == '&') {
                String code = "&" + text.charAt(i + 1);
                if (colorMap.containsKey(code) || specialCodes.contains(code)) {
                    return true;
                }
            }
        }
        return false;
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

        if (hexColors == null || hexColors.isEmpty()) {
            return Message.raw(text).color(DefaultColors.WHITE.getHex()).bold(bold).italic(italic);
        }
        if (hexColors.size() == 1 || text.isEmpty()) {
            return Message.raw(text).color(hexColors.getFirst()).bold(bold).italic(italic);
        }

        int smoothness = Math.max(2, hytaleFoundations.getConfigManager().getDefaultChatConfig().getGradientChunkSize());
        List<String> chunks = new ArrayList<>();
        for (int i = 0; i < text.length(); i += smoothness) {
            chunks.add(text.substring(i, Math.min(text.length(), i + smoothness)));
        }
        int length = chunks.size();
        for (int i = 0; i < length; i++) {
            double overallProgress = (length > 1) ? (double) i / (length - 1) : 0;
            double segmentWeight = 1.0 / (hexColors.size() - 1);
            int segmentIndex = (int) (overallProgress / segmentWeight);

            if (segmentIndex >= hexColors.size() - 1) {
                segmentIndex = hexColors.size() - 2;
            }

            double localProgress = (overallProgress - (segmentIndex * segmentWeight)) / segmentWeight;
            int[] startRGB = hexToRGB(hexColors.get(segmentIndex));
            int[] endRGB = hexToRGB(hexColors.get(segmentIndex + 1));

            String colorForWord = interpolateColor(startRGB, endRGB, localProgress);

            builder.insert(Message.raw(chunks.get(i))
                    .color(colorForWord)
                    .bold(bold)
                    .italic(italic));
        }

        return builder;
    }
    public String stripTextOfColorCodes(String text) {
        if (text == null || text.isEmpty()) return text;
        StringBuilder builder = new StringBuilder();
        boolean ignoreColors = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (ignoreColors) {
                if (c == ' ') {
                    ignoreColors = false;
                } else {
                    builder.append(c);
                    continue;
                }
            }

            if (c == '&' && i + 1 < text.length()) {
                String codeKey = "&" + text.charAt(i + 1);
                if (codeKey.equals("&-")) {
                    if (hasValidCodeAfter(text, i + 2)) {
                        ignoreColors = true;
                        i++;
                        continue;
                    }
                }
                if (colorMap.containsKey(codeKey) || specialCodes.contains(codeKey)) {
                    i++;
                    continue;
                }
            }
            builder.append(c);
        }
        return builder.toString();
    }
    public boolean isColorCodeAvailable(String code) {
        if(!isValidColorCode(code)) return false;
        return colorMap.containsKey(code) || specialCodes.contains(code);
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
    public boolean validateUsernameDisplayColor(String user, String text) {
        if (text == null || text.isEmpty()) return true;
        for (int i = 0; i < text.length() - 1; i++) {
            if (text.charAt(i) == '&') {
                String code = "&" + text.charAt(i + 1);
                if (code.equals("&-")) continue;
                if (isValidColorCode(code)) {
                    if (!colorMap.containsKey(code) && !specialCodes.contains(code)) {
                        myLogger.atInfo().log(LM.getConsoleMessage(LangKey.INVALID_DISPLAY_COLOR, user).getAnsiMessage());
                        return false;
                    }
                }
            }
        }
        return true;
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
    public Map<String, String> getColorMap() {return colorMap;}
    public List<String> getSpecialCodes() {return specialCodes;}
    public String getColorFile() { return colorFileName;}
    // Color Map Class
    public static final class ColormapHolder {
        @SerializedName("VERSION")
        private final String version = COLORMAP_VERSION;
        @SerializedName("COLOR_LIST")
        private Map<String, String> colorList = new ConcurrentHashMap<>();

        public Map<String, String> getColorList() {return colorList;}
        public void setColorList(Map<String, String> colorList) {this.colorList = colorList;}
        public String getVersion() {return version;}
    }

}