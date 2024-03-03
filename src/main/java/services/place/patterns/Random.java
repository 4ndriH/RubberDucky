package services.place.patterns;

import assets.objects.Pixel;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

public class Random extends PatternBase {
    public static ArrayList<Pixel> encode(BufferedImage image, int xOffset, int yOffset) {
        pixels.clear();
        LeftToRight.encode(image, xOffset, yOffset);
        Collections.shuffle(pixels);
        return pixels;
    }
}
