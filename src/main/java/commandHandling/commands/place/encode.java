package commandHandling.commands.place;

import commandHandling.CommandContext;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;

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
        int width = Integer.parseInt(ctx.getArguments().get(4));
        int height = Integer.parseInt(ctx.getArguments().get(3));
        int linesCnt = 0;

        img = resize(img, width, height);

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                if (x + i < 1000 && y + j < 1000) {
                    linesCnt++;
                    writer.println("dev.place setpixel " + (x + i) + " " + (y + j) + " " +
                            rgbToHex(new Color(img.getRGB(i, j))));
                }
            }
        }

        ctx.getChannel().sendMessage("Estimated drawing time: \n**" + timeConversion(linesCnt) + "**")
                .addFile(new File("src/tempFiles/RDencoder.txt")).queue();
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
