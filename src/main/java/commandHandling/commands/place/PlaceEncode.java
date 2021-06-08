package commandHandling.commands.place;

import commandHandling.CommandContext;
import services.BotExceptions;
import services.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class PlaceEncode implements Runnable {
    private final ArrayList<String> list = new ArrayList<>();
    private final CommandContext ctx;
    private BufferedImage img = null;
    private int x, y;

    public PlaceEncode(CommandContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        String pattern, path = "tempFiles/place/encode/";
        PrintStream writer;
        int width, height;

        try {
            String fileName = ctx.getMessage().getAttachments().get(0).getFileName();
            path += fileName.substring(0, fileName.length() - 4);
            img = ImageIO.read(new URL(ctx.getMessage().getAttachments().get(0).getUrl()));
            writer = new PrintStream(path + ".txt");
        } catch (Exception e) {
            Logger.commandAndException(ctx, "place", e, false);
            BotExceptions.missingAttachmentException(ctx);
            return;
        }

        try {
            x = Integer.parseInt(ctx.getArguments().get(1));
            y = Integer.parseInt(ctx.getArguments().get(2));
            width = Integer.parseInt(ctx.getArguments().get(3));
            height = Integer.parseInt(ctx.getArguments().get(4));
        } catch (Exception e) {
            Logger.commandAndException(ctx, "place", e, false);
            BotExceptions.invalidArgumentsException(ctx);
            return;
        }

        Logger.command(ctx, "place", true);

        if (ctx.getArguments().size() == 6) {
            pattern = ctx.getArguments().get(5);
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
        }

        for (String s : list) {
            writer.println(s);
        }
        writer.close();

        try {
            ctx.getChannel().sendMessage("Estimated drawing time: \n**" + timeConversion(list.size()) + "**")
                    .addFile(new File(path + ".txt")).queue(
                            msg -> msg.delete().queueAfter(512, TimeUnit.SECONDS)
            );
        } catch (IllegalArgumentException e) {
            Logger.exception(ctx, e);
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
        Collections.shuffle(list);
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

    private void writerUtility (Color color, int i, int j) {
        if (color.getAlpha() > 230 && x + i >= 0 && x + i < 1000 && y + j >= 0 && y + j < 1000) {
            list.add(".place setpixel " + (x + i) + " " + (y + j) + " " + rgbToHex(color));
        }
    }

    private String rgbToHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    private String timeConversion(int linesCnt) {
        int seconds = linesCnt % 60;
        int minutes = (linesCnt - seconds) / 60 % 60;
        int hours = ((linesCnt - seconds) / 60 - minutes) / 60;

        String days = "";
        if (hours > 23) {
            days = (hours - (hours %= 24)) / 24 + "";
            if (Integer.parseInt(days) == 1) {
                days += " day, ";
            } else {
                days += " days, ";
            }
        }

        return String.format(days + "%02d:%02d:%02d", hours, minutes, seconds);
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