package services.place.patterns;

import assets.objects.Pixel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

public class Diagonal extends PatternBase {
    public static ArrayList<Pixel> encode(BufferedImage image, int xOffset, int yOffset) {
        ArrayList<Pixel> diagPattern = new ArrayList<>();
        boolean flip = true;
        int rows = image.getHeight();
        int cols = image.getWidth();

        for (int k = 0; k < rows + cols - 1; k++) {
            int y = k < cols ? 0 : k - cols + 1;
            int x = k < cols ? k : cols - 1;

            pixels.clear();

            while (y < rows && x >= 0) {
                addPixel(new Color(image.getRGB(x, y), true), x, y, xOffset, yOffset);
                y++;
                x--;
            }

            if (flip) {
                Collections.reverse(pixels);
            }

            diagPattern.addAll(pixels);

            flip = !flip;
        }

        return diagPattern;
    }
}
