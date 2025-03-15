package services.place.patterns;

import assets.objects.Pixel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class Spread extends PatternBase {
    private static boolean contained;

    public static ArrayList<Pixel> encode(BufferedImage image, int xOffset, int yOffset, boolean contained, ArrayList<int[]> startingPoints) {
        Spread.contained = contained;
        pixels.clear();

        boolean[][] pM = new boolean[image.getWidth()][image.getHeight()];
        ArrayList<int[]> pixelProcessQueue = new ArrayList<>();
        java.util.Random random = new Random();

        for (int[] point : startingPoints) {
            if (new Color(image.getRGB(point[0], point[1]), true).getAlpha() > 10) {
                pixelProcessQueue.add(point);
                pM[point[0]][point[1]] = true;
            }
        }

        if (pixelProcessQueue.isEmpty()) {
            int startX;
            int startY;

            do {
                startX = random.nextInt(image.getWidth());
                startY = random.nextInt(image.getHeight());
            } while (contained && new Color(image.getRGB(startX, startY), true).getAlpha() <= 10);

            pixelProcessQueue.add(new int[]{startX, startY});
            pM[startX][startY] = true;
        }

        int idx;

        while(!pixelProcessQueue.isEmpty() && pixels.size() <= 1_000_000) {
            idx = random.nextInt(pixelProcessQueue.size());
            int pixelX = pixelProcessQueue.get(idx)[0];
            int pixelY = pixelProcessQueue.get(idx)[1];
            int alpha = new Color(image.getRGB(pixelX, pixelY), true).getAlpha();
            pixelProcessQueue.remove(idx);

            addPixel(new Color(image.getRGB(pixelX, pixelY), true), pixelX, pixelY, xOffset, yOffset);

            while(spreadPixelCheckComplete(pM, pixelX, pixelY, alpha)) {
                switch (random.nextInt(4)) {
                    case 0 -> {
                        if (spreadPixelCheck(pM, pixelX - 1, pixelY, alpha)) {
                            pM[pixelX - 1][pixelY] = true;
                            pixelProcessQueue.add(new int[]{pixelX - 1, pixelY});
                        }
                    }
                    case 1 -> {
                        if (spreadPixelCheck(pM, pixelX + 1, pixelY, alpha)) {
                            pM[pixelX + 1][pixelY] = true;
                            pixelProcessQueue.add(new int[]{pixelX + 1, pixelY});
                        }
                    }
                    case 2 -> {
                        if (spreadPixelCheck(pM, pixelX, pixelY - 1, alpha)) {
                            pM[pixelX][pixelY - 1] = true;
                            pixelProcessQueue.add(new int[]{pixelX, pixelY - 1});
                        }
                    }
                    case 3 -> {
                        if (spreadPixelCheck(pM, pixelX, pixelY + 1, alpha)) {
                            pM[pixelX][pixelY + 1] = true;
                            pixelProcessQueue.add(new int[]{pixelX, pixelY + 1});
                        }
                    }
                }
            }
        }

        return pixels;
    }

    private static boolean spreadPixelCheckComplete(boolean[][] pM, int x, int y, int alpha) {
        return spreadPixelCheck(pM, x - 1, y, alpha) || spreadPixelCheck(pM, x + 1, y, alpha) ||
                spreadPixelCheck(pM, x, y - 1, alpha) || spreadPixelCheck(pM, x, y + 1, alpha);
    }

    private static  boolean spreadPixelCheck(boolean[][] pM, int x, int y, int alpha) {
        if (contained) {
            return x >= 0 && x < pM.length && y >= 0 && y < pM[1].length && !pM[x][y] && alpha != 0;
        } else {
            return x >= 0 && x < pM.length && y >= 0 && y < pM[1].length && !pM[x][y];
        }
    }
}
