package commandHandling.commands.place;

import commandHandling.CommandContext;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.*;

public class encode {
    private final CommandContext ctx;

    public encode(CommandContext ctx) {
        this.ctx = ctx;
        encode();
    }

    private void encode() {
        PrintStream writer = null;
        BufferedImage img = null;
        try {
            img = ImageIO.read(new URL(ctx.getMessage().getAttachments().get(0).getUrl()));
            writer = new PrintStream("src/tempFiles/RDencoder.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        ctx.getMessage().delete().queue();

        int x = Integer.parseInt(ctx.getArguments().get(1));
        int y = Integer.parseInt(ctx.getArguments().get(2));
        int width = Integer.parseInt(ctx.getArguments().get(3));
        int height = Integer.parseInt(ctx.getArguments().get(4));
        String pattern = "";
        if (ctx.getArguments().size() == 6) {
            pattern = ctx.getArguments().get(5);
        }
        int linesCnt = 0;
        img = resize(img, width, height);

        switch (pattern) {
            case "lr":
                linesCnt = leftToRight(linesCnt, x, y, img, writer);
                break;
            case "td":
                linesCnt = topDown(linesCnt, x, y, img, writer);
                break;
            case "diagonal":
                linesCnt = diagonal(linesCnt, x, y, img, writer);
                break;
            case "spiral":
                linesCnt = spiral(linesCnt, x, y, img, writer);
                break;
            case "random":
                linesCnt = random(linesCnt, x, y, img, writer);
                break;
            case "circle":
                linesCnt = circle(linesCnt, x, y, img, writer);
                break;
            default:
                linesCnt = leftToRight(linesCnt, x, y, img, writer);
        }

        ctx.getChannel().sendMessage("Estimated drawing time: \n**" + timeConversion(linesCnt) + "**")
                .addFile(new File("src/tempFiles/RDencoder.txt")).queue();
    }

    private int leftToRight(int linesCnt, int x, int y, BufferedImage img, PrintStream writer) {
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Color color = new Color(img.getRGB(i, j), true);
                if (color.getAlpha() != 0 && x + i < 1000 && y + j < 1000) {
                    linesCnt++;
                    writer.println(".place setpixel " + (x + i) + " " + (y + j) + " " +
                            rgbToHex(color));
                }
            }
        }
        return linesCnt;
    }

    private int topDown(int linesCnt, int x, int y, BufferedImage img, PrintStream writer) {
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                Color color = new Color(img.getRGB(j, i), true);
                if (color.getAlpha() != 0 && x + i < 1000 && y + j < 1000) {
                    linesCnt++;
                    writer.println(".place setpixel " + (x + j) + " " + (y + i) + " " +
                            rgbToHex(color));
                }
            }
        }
        return linesCnt;
    }

    private int diagonal(int linesCnt, int x, int y, BufferedImage img, PrintStream writer) {
        boolean isUp = true;

        int n = img.getWidth(), m = img.getHeight(), i = 0, j = 0;

        for (int k = 0; k < n * m;) {
            if (isUp) {
                for (; i >= 0 && j < n; j++, i--) {
                    Color color = new Color(img.getRGB(j, i), true);
                    if (color.getAlpha() != 0 && x + i < 1000 && y + j < 1000) {
                        linesCnt++;
                        writer.println(".place setpixel " + (x + j) + " " + (y + i) + " " +
                                rgbToHex(color));
                    }
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
                    Color color = new Color(img.getRGB(j, i), true);
                    if (color.getAlpha() != 0 && x + i < 1000 && y + j < 1000) {
                        linesCnt++;
                        writer.println(".place setpixel " + (x + j) + " " + (y + i) + " " +
                                rgbToHex(color));
                    }
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

        return linesCnt;
    }

    private int spiral(int linesCnt, int x, int y, BufferedImage img, PrintStream writer) {
        int n = img.getWidth(), m = img.getHeight();
        int limit;
        if (m > n) {
            limit = m;
        } else {
            limit = n;
        }

        for (int i = limit - 1; i >= limit / 2; i--) {
            //right side
            for (int j = m - i; j <= i; j++) {
                Color color = new Color(img.getRGB(i, j), true);
                if (color.getAlpha() != 0 && x + i < 1000 && y + j < 1000) {
                    linesCnt++;
                    writer.println(".place setpixel " + (x + i) + " " + (y + j) + " " +
                            rgbToHex(color));
                }
            }

            //bottom
            for (int j = i - 1; j >= n - i; j--) {
                Color color = new Color(img.getRGB(i, j), true);
                if (color.getAlpha() != 0 && x + i < 1000 && y + j < 1000) {
                    linesCnt++;
                    writer.println(".place setpixel " + (x + i) + " " + (y + j) + " " +
                            rgbToHex(color));
                }
            }

            //left side
            for (int j = i; j >= m - i; j--) {
                Color color = new Color(img.getRGB(i, j), true);
                if (color.getAlpha() != 0 && x + i < 1000 && y + j < 1000) {
                    linesCnt++;
                    writer.println(".place setpixel " + (x + i) + " " + (y + j) + " " +
                            rgbToHex(color));
                }
            }

            for (int j = n - i; j <= i - 1; j++) {
                Color color = new Color(img.getRGB(i, j), true);
                if (color.getAlpha() != 0 && x + i < 1000 && y + j < 1000) {
                    linesCnt++;
                    writer.println(".place setpixel " + (x + i) + " " + (y + j) + " " +
                            rgbToHex(color));
                }
            }
        }
        return linesCnt;
    }

    private int random(int linesCnt, int x, int y, BufferedImage img, PrintStream writer) {
        ArrayList<String> list = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                Color color = new Color(img.getRGB(i, j), true);
                if (color.getAlpha() != 0 && x + i < 1000 && y + j < 1000) {
                    linesCnt++;
                    list.add(".place setpixel " + (x + i) + " " + (y + j) + " " +
                            rgbToHex(color));
                }
            }
        }

        Collections.shuffle(list);

        for (int i = 0; i < list.size(); i++) {
            writer.println(list.get(i));
        }
        return linesCnt;
    }

    private int circle(int linesCnt, int x, int y, BufferedImage img, PrintStream writer) {
        int n = img.getWidth(), m = img.getHeight();
        boolean[][] usedPixels = new boolean[512][256];

        for (int i = 750; i >= -10; i--) {
            for (double j = 0; j < 2 * Math.PI; j += 0.0001) {
                int xC = 256 + (int)(Math.sin(j) * i);
                int yC = 128 - (int)(Math.cos(j) * i);


                if (xC < 512 && xC >= 0 && yC < 256 && yC >= 0 && !usedPixels[xC][yC]) {
                    Color color = new Color(img.getRGB(xC, yC), true);
                    if (color.getAlpha() != 0 && x + i < 1000 && y + j < 1000) {
                        linesCnt++;
                        writer.println(".place setpixel " + (x + xC) + " " + (y + yC) + " " +
                                rgbToHex(color));
                    }
                    usedPixels[xC][yC] = true;
                }
            }
        }
        return linesCnt;
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
            hours %= 24;
            days = (((linesCnt - seconds) / 60 - minutes) / 60 - hours) / 24 + "";
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
        BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }
}
