package com.raeden.hytale.utils;

import com.hypixel.hytale.server.core.Message;

public class ColorUtils {
    public static Message color(String text, String hexColor) {
        return Message.raw(text).color(hexColor).bold(false);
    }
    public static Message color(String text, String hexColor, boolean isBold) {
        return Message.raw(text).color(hexColor).bold(isBold);
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

}
