package services.place.patterns;

import assets.objects.Pixel;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class LeftToRight extends PatternBase {
    public static ArrayList<Pixel> encode(BufferedImage image, int xOffset, int yOffset) {
        pixels.clear();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                addPixel(new java.awt.Color(image.getRGB(x, y), true), x, y, xOffset, yOffset);
            }
        }
        return pixels;
    }
}
