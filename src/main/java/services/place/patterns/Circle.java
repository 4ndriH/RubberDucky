package services.place.patterns;

import assets.objects.Pixel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Circle extends PatternBase {
    public static ArrayList<Pixel> encode(BufferedImage image, int xOffset, int yOffset) {
        pixels.clear();

        int n = image.getWidth();
        int m = image.getHeight();
        int limit = (int)(Math.max(m, n) * 1.25);
        boolean[][] usedPixels = new boolean[n][m];

        for (int i = limit; i >= -10; i--) {
            for (double j = 0; j < 2 * Math.PI; j += 0.0001) {
                int x = n / 2 + (int)(Math.sin(j) * i);
                int y = m / 2 - (int)(Math.cos(j) * i);

                if (x < n && x >= 0 && y < m && y >= 0 && !usedPixels[x][y]) {
                    addPixel(new Color(image.getRGB(x, y), true), x, y, xOffset, yOffset);
                    usedPixels[x][y] = true;
                }
            }
        }

        return pixels;
    }
}