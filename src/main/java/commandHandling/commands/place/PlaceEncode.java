package commandHandling.commands.place;

import commandHandling.CommandContext;
import services.BotExceptions;
import services.Miscellaneous;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PlaceEncode implements Runnable {
    private final ArrayList<String> pixels = new ArrayList<>();
    private final PlaceData placeData;
    private final CommandContext ctx;
    private BufferedImage img = null;
    private int x, y;

    public PlaceEncode(PlaceData placeData, CommandContext ctx) {
        this.placeData = placeData;
        this.ctx = ctx;
    }

    @Override
    public void run() {
        String pattern, path = "tempFiles/place/encode/";
        PrintStream writer;
        int width, height;
        boolean reverse = false;

        try {
            String fileName = ctx.getMessage().getAttachments().get(0).getFileName();
            path += fileName.substring(0, fileName.length() - 4);
            img = ImageIO.read(new URL(ctx.getMessage().getAttachments().get(0).getUrl()));
            writer = new PrintStream(path + ".txt");
            ctx.getMessage().delete().queue();
        } catch (Exception e) {
            Miscellaneous.CommandLog("Place", ctx, false);
            BotExceptions.missingAttachmentException(ctx);
            return;
        }

        try {
            x = Integer.parseInt(ctx.getArguments().get(1));
            y = Integer.parseInt(ctx.getArguments().get(2));
            width = Integer.parseInt(ctx.getArguments().get(3));
            height = Integer.parseInt(ctx.getArguments().get(4));
        } catch (Exception e) {
            Miscellaneous.CommandLog("Place", ctx, false);
            BotExceptions.invalidArgumentsException(ctx);
            return;
        }

        Miscellaneous.CommandLog("Place", ctx, true);

        if (ctx.getArguments().size() == 6) {
            pattern = ctx.getArguments().get(5);
            if (pattern.endsWith("/r")) {
                pattern = pattern.replace("/r", "");
                reverse = true;
            }
        } else {
            pattern = "lr";
        }

        img = resize(img, width, height);

        switch (pattern) {
            case "leftright": case "lr":
                leftToRight();
                break;
            case "topdown": case "td":
                topDown();
                break;
            case "diagonal":
                diagonal();
                break;
            case "spiral":
                spiral();
                break;
            case "random":
                random();
                break;
            case "circle":
                circle();
                break;
            case "spread":
                spread();
                break;
            default:
                BotExceptions.unknownPatternException(ctx);
                return;
        }

        if (reverse) {
            Collections.reverse(pixels);
        }

        for (String s : pixels) {
            writer.println(s);
        }
        writer.close();

        try {
            ctx.getChannel().sendMessage("Estimated drawing time: \n**" +
                    Miscellaneous.timeFormat(pixels.size()) + "**").addFile(new File(path + ".txt")).queue(
                            msg -> Miscellaneous.deleteMsg(msg, 128)
            );
        } catch (IllegalArgumentException e) {
            placeData.LOGGER.error("PlaceEncode Error", e);
            BotExceptions.FileExceedsUploadLimitException(ctx);
        }

        delete(path);
    }

    private void leftToRight() {
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                writerUtility(new Color(img.getRGB(i, j), true), i, j);
            }
        }
    }

    private void topDown() {
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                writerUtility(new Color(img.getRGB(j, i), true), j, i);
            }
        }
    }

    private void diagonal() {
        boolean isUp = true;
        int n = img.getWidth(), m = img.getHeight(), i = 0, j = 0;

        for (int k = 0; k < n * m;) {
            if (isUp) {
                for (; i >= 0 && j < n; j++, i--) {
                    writerUtility(new Color(img.getRGB(i, j), true), i, j);
                    k++;
                }
                if (i < 0 && j <= n - 1){
                    i = 0;
                }
                if (j == n) {
                    i = i + 2;
                    j--;
                }
            } else {
                for (; j >= 0 && i < m; i++, j--) {
                    writerUtility(new Color(img.getRGB(i, j), true), i, j);
                    k++;
                }
                if (j < 0 && i <= m - 1) {
                    j = 0;
                }
                if (i == m) {
                    j = j + 2;
                    i--;
                }
            }
            isUp = !isUp;
        }
    }

    private void spiral() {
        int n = img.getWidth() - 1, m = img.getHeight() - 1, h = n, v = m;
        boolean[][] usedPixels = new boolean[n + 1][m + 1];

        while (h >= n / 2 || v >= m / 2) {
            for (int y = m - v; y <= v; y++){
                if (!usedPixels[h][y]) {
                    writerUtility(new Color(img.getRGB(h, y), true), h, y);
                    usedPixels[h][y] = true;
                }
            }

            for (int x = --h; x >= n - h - 1; x--) {
                if (!usedPixels[x][v]) {
                    writerUtility(new Color(img.getRGB(x, v), true), x, v);
                    usedPixels[x][v] = true;
                }
            }

            for (int y = --v; y >= m - v - 1; y--) {
                if (!usedPixels[n - h - 1][y]) {
                    writerUtility(new Color(img.getRGB(n - h - 1, y), true), n - h - 1, y);
                    usedPixels[n - h - 1][y] = true;
                }
            }

            for (int x = n - h; x <= h; x++) {
                if (!usedPixels[x][m - v - 1]) {
                    writerUtility(new Color(img.getRGB(x, m - v - 1), true), x, m - v - 1);
                    usedPixels[x][m - v - 1] = true;
                }
            }
        }
    }

    private void random() {
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                writerUtility(new Color(img.getRGB(i, j), true), i, j);
            }
        }
        Collections.shuffle(pixels);
    }

    private void circle() {
        int n = img.getWidth(), m = img.getHeight(), limit = (int)(Math.max(m, n) * 1.25);
        boolean[][] usedPixels = new boolean[n][m];

        for (int i = limit; i >= -10; i--) {
            for (double j = 0; j < 2 * Math.PI; j += 0.0001) {
                int x = n / 2 + (int)(Math.sin(j) * i);
                int y = m / 2 - (int)(Math.cos(j) * i);

                if (x < n && x >= 0 && y < m && y >= 0 && !usedPixels[x][y]) {
                    writerUtility(new Color(img.getRGB(x, y), true), x, y);
                    usedPixels[x][y] = true;
                }
            }
        }
    }

    private void spread() {
        boolean[][] pM = new boolean[img.getWidth()][img.getHeight()];
        ArrayList<int[]> list = new ArrayList<>();
        Random random = new Random();

        int startX = random.nextInt(img.getWidth());
        int startY = random.nextInt(img.getHeight());

        list.add(new int[]{startX, startY});
        pM[startX][startY] = true;
        int idx;

        while(!list.isEmpty() && pixels.size() <= 1_000_000) {
            idx = random.nextInt(list.size());
            int pixelX = list.get(idx)[0];
            int pixelY = list.get(idx)[1];
            list.remove(idx);

            writerUtility(new Color(img.getRGB(pixelX, pixelY), true), x + pixelX, y + pixelY);

            while(spreadPixelCheckComplete(pM, pixelX, pixelY)) {
                switch (random.nextInt(4)) {
                    case 0:
                        if (spreadPixelCheck(pM, pixelX - 1, pixelY)) {
                            pM[pixelX - 1][pixelY] = true;
                            list.add(new int[]{pixelX - 1, pixelY});
                        }
                        break;
                    case 1:
                        if (spreadPixelCheck(pM, pixelX + 1, pixelY)) {
                            pM[pixelX + 1][pixelY] = true;
                            list.add(new int[]{pixelX + 1, pixelY});
                        }
                        break;
                    case 2:
                        if (spreadPixelCheck(pM, pixelX, pixelY - 1)) {
                            pM[pixelX][pixelY - 1] = true;
                            list.add(new int[]{pixelX, pixelY - 1});
                        }
                        break;
                    case 3:
                        if (spreadPixelCheck(pM, pixelX, pixelY + 1)) {
                            pM[pixelX][pixelY + 1] = true;
                            list.add(new int[]{pixelX, pixelY + 1});
                        }
                        break;
                }
            }
        }
    }

    private boolean spreadPixelCheckComplete(boolean[][] pM, int x, int y) {
        return spreadPixelCheck(pM, x - 1, y) || spreadPixelCheck(pM, x + 1, y) ||
                spreadPixelCheck(pM, x, y - 1) || spreadPixelCheck(pM, x, y + 1);
    }

    private boolean spreadPixelCheck(boolean[][] pM, int x, int y) {
        return x >= 0 && x < pM.length && y >= 0 && y < pM[1].length && !pM[x][y];
    }

    private void writerUtility (Color color, int i, int j) {
        if (color.getAlpha() > 230 && x + i >= 0 && x + i < 1000 && y + j >= 0 && y + j < 1000) {
            pixels.add(".place setpixel " + (x + i) + " " + (y + j) + " " + rgbToHex(color));
        }
    }

    private String rgbToHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    private BufferedImage resize(BufferedImage img, int newW, int newH) {
        BufferedImage newImage = new BufferedImage(newW, newH, img.getType());
        int w = img.getWidth(), h = img.getHeight();
        Graphics2D g = newImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return newImage;
    }

    private void delete(String path) {
        File myTxtObj = new File(path + ".txt");
        while(myTxtObj.exists() && !myTxtObj.delete());
    }
}
