package services.place.patterns;

import assets.objects.Pixel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Spiral extends PatternBase {
    public static ArrayList<Pixel> encode(BufferedImage image, int xOffset, int yOffset) {
        pixels.clear();

        int top = 0;
        int bottom = image.getHeight() - 1;
        int left = 0;
        int right = image.getWidth() - 1;

        while (top <= bottom && left <= right) {
            for (int i = left; i <= right; i++) {
                addPixel(new Color(image.getRGB(top, i), true), top, i, xOffset, yOffset);
            }
            top++;

            for (int i = top; i <= bottom; i++) {
                addPixel(new Color(image.getRGB(i, right), true), i, right, xOffset, yOffset);
            }
            right--;

            if (top <= bottom) {
                for (int i = right; i >= left; i--) {
                    addPixel(new Color(image.getRGB(bottom, i), true), bottom, i, xOffset, yOffset);
                }
                bottom--;
            }

            if (left <= right) {
                for (int i = bottom; i >= top; i--) {
                    addPixel(new Color(image.getRGB(i, left), true), i, left, xOffset, yOffset);
                }
                left++;
            }
        }

        return pixels;
    }
}
