package services.place.patterns;

import assets.objects.Pixel;

import java.awt.*;
import java.util.ArrayList;

public class PatternBase {
    public static ArrayList<Pixel> pixels = new ArrayList<>();

    public static void addPixel(Color color, int x, int y, int xOffset, int yOffset) {
        if (color.getAlpha() > 0 && x + xOffset >= 0 && x + xOffset < 1000 && y + yOffset >= 0 && y + yOffset < 1000) {
            pixels.add(new Pixel(x + xOffset, y + yOffset, color.getAlpha() / 255.0, rgbToHex(color)));
        }
    }

    private static String rgbToHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }
}
