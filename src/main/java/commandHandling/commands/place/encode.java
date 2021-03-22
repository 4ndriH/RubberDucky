package commandHandling.commands.place;

import commandHandling.CommandContext;
import services.BotExceptions;

import java.util.concurrent.TimeUnit;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.net.URL;
import java.util.*;
import java.awt.*;
import java.io.*;

public class encode {
    private final ArrayList<String> list = new ArrayList<>();
    private final CommandContext ctx;
    private BufferedImage img = null;
    private int x, y;

    public encode(CommandContext ctx) {
        this.ctx = ctx;
        encoding();
    }

    private void encoding() {
        String pattern = "", fileName;
        PrintStream writer;
        int width, height;

        try {
            img = ImageIO.read(new URL(ctx.getMessage().getAttachments().get(0).getUrl()));
            fileName = "RDencode - " + ctx.getMessage().getAttachments().get(0).getFileName() + ".txt";
            writer = new PrintStream("tempFiles/place/encode/" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ctx.getMessage().delete().queue();

        try {
            x = Integer.parseInt(ctx.getArguments().get(1));
            y = Integer.parseInt(ctx.getArguments().get(2));
            width = Integer.parseInt(ctx.getArguments().get(3));
            height = Integer.parseInt(ctx.getArguments().get(4));
        } catch (Exception e) {
            BotExceptions.invalidArgumentsException(ctx);
            return;
        }

        if (ctx.getArguments().size() == 6) {
            pattern = ctx.getArguments().get(5);
        } else {
            pattern = "lr";
        }

        img = resize(img, width, height);

        switch (pattern) {
            case "lr":
                leftToRight();
                break;
            case "td":
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
            default:
                BotExceptions.invalidArgumentsException(ctx);
                return;
        }

        for (String s : list) {
            writer.println(s);
        }

        ctx.getChannel().sendMessage("Estimated drawing time: \n**" + timeConversion(list.size()) + "**")
                .addFile(new File("tempFiles/place/encode" + fileName)).queue();

        try {
            TimeUnit.MINUTES.sleep(1);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        File myObj = new File("tempFiles/place/encode/" + fileName);
        myObj.delete();
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
                if (i < 0 && j <= n - 1)
                    i = 0;
                if (j == n) {
                    i = i + 2;
                    j--;
                }
            } else {
                for (; j >= 0 && i < m; i++, j--) {
                    writerUtility(new Color(img.getRGB(i, j), true), i, j);
                    k++;
                }
                if (j < 0 && i <= m - 1)
                    j = 0;
                if (i == m) {
                    j = j + 2;
                    i--;
                }
            }
            isUp = !isUp;
        }
    }

    private void spiral() {
        int n = img.getWidth(), m = img.getHeight();
        int limit = Math.max(m, n);

        for (int i = limit - 1; i >= limit / 2; i--) {
            //right side
            for (int j = m - i; j <= i; j++) {
                writerUtility(new Color(img.getRGB(i, j), true), i, j);
            }

            //bottom
            for (int j = i - 1; j >= n - i; j--) {
                writerUtility(new Color(img.getRGB(j, i), true), j, i);
            }

            //left side
            for (int j = i; j >= m - i; j--) {
                writerUtility(new Color(img.getRGB(i, j), true), (img.getHeight() - i - 1), j);
            }

            for (int j = n - i; j <= i - 1; j++) {
                writerUtility(new Color(img.getRGB(j, i), true), j, (img.getWidth() - i - 1));
            }
        }
    }

    private void random() {
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
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
                int xC = n / 2 + (int)(Math.sin(j) * i);
                int yC = m / 2 - (int)(Math.cos(j) * i);

                if (xC < n && xC >= 0 && yC < m && yC >= 0 && !usedPixels[xC][yC]) {
                    writerUtility(new Color(img.getRGB(xC, yC), true), xC, yC);
                    usedPixels[xC][yC] = true;
                }
            }
        }
    }

    private void writerUtility (Color color, int i, int j) {
        if (color.getAlpha() != 0 && x + i >= 0 && x + i < 1000 && y + j >= 0 && y + j < 1000) {
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
//            hours %= 24;
//            days = (((linesCnt - seconds) / 60 - minutes) / 60 - hours) / 24 + "";
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
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage newImage = new BufferedImage(newW, newH, img.getType());
        Graphics2D g = newImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return newImage;
    }
}
