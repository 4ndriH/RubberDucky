package commandHandling.commands.publicCommands.place;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.CONFIG;
import services.BotExceptions;
import services.CommandManager;
import services.Miscellaneous.TimeFormat;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static services.MessageDeleteHelper.deleteMsg;

public class PlaceEncode implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(PlaceEncode.class);
    private ArrayList<String> pixels;
    private ArrayList<int[]> startingPoints;
    private BufferedImage img = null;
    private int x, y;
    private boolean spreadContained = false;

    public PlaceEncode(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        String fileName, pattern;
        startingPoints = new ArrayList<>();
        pixels = new ArrayList<>();
        int width, height;

        try {
            fileName = ctx.getMessage().getAttachments().get(0).getFileName();
            fileName = fileName.substring(0, fileName.length() - 4) + ".txt";
            img = ImageIO.read(new URL(ctx.getMessage().getAttachments().get(0).getUrl()));
            ctx.getMessage().delete().queue();
        } catch (Exception e) {
            CommandManager.commandLogger("Place", ctx, false);
            System.out.println(e);
            BotExceptions.missingAttachmentException(ctx);
            return;
        }

        try {
            x = Integer.parseInt(ctx.getArguments().get(0));
            y = Integer.parseInt(ctx.getArguments().get(1));
            width = Integer.parseInt(ctx.getArguments().get(2));
            height = Integer.parseInt(ctx.getArguments().get(3));
        } catch (Exception e) {
            CommandManager.commandLogger("Place", ctx, false);
            BotExceptions.invalidArgumentsException(ctx);
            return;
        }

        try {
            pattern = ctx.getArguments().get(4).toLowerCase();
        } catch (Exception e) {
            pattern = "";
        }

        CommandManager.commandLogger("Place", ctx, true);

        if (ctx.getArguments().contains("-c")) {
            spreadContained = true;
        }

        if (ctx.getArguments().contains("-s")) {
            int dashSIdx = ctx.getArguments().indexOf("-s");
            int lastDashIdx = ctx.getArguments().lastIndexOf("-");

            for (int i = dashSIdx + 1; i + 1 < ctx.getArguments().size() || i + 1 < lastDashIdx; i += 2) {
                int x = 0, y = 0;
                try {
                    x = Integer.parseInt(ctx.getArguments().get(i));
                    y = Integer.parseInt(ctx.getArguments().get(i + 1));
                } catch (Exception e) {
                    BotExceptions.invalidArgumentsException(ctx);
                    return;
                }
                startingPoints.add(new int[]{x, y});
            }
        }

        img = resize(img, width, height);

        switch (pattern) {
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
                leftToRight();
        }

        if (ctx.getArguments().contains("-r")) {
            Collections.reverse(pixels);
        }

        StringBuilder sb = new StringBuilder();

        for (String s : pixels) {
            sb.append(s).append("\n");
        }

        InputStream stream = new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));

        try {
            ctx.getChannel().sendMessage("Estimated drawing time: \n**" +
                    TimeFormat.timeFormat(pixels.size()) + "**").addFile(stream, fileName).queue(
                    msg -> deleteMsg(msg, 128)
            );
        } catch (IllegalArgumentException e) {
            LOGGER.error("PlaceEncode Error", e);
            BotExceptions.FileExceedsUploadLimitException(ctx);
        }
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
        leftToRight();
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
        ArrayList<int[]> pixelProcessQueue = new ArrayList<>();
        Random random = new Random();

        for (int[] point : startingPoints) {
            if (new Color(img.getRGB(point[0], point[1]), true).getAlpha() > 10) {
                pixelProcessQueue.add(point);
                pM[point[0]][point[1]] = true;
            }
        }

        if (pixelProcessQueue.size() == 0) {
            int startX;
            int startY;

            do {
                startX = random.nextInt(img.getWidth());
                startY = random.nextInt(img.getHeight());
            } while (spreadContained && new Color(img.getRGB(startX, startY), true).getAlpha() <= 10);

            pixelProcessQueue.add(new int[]{startX + x, startY + y});
            pM[startX + x][startY + y] = true;
        }

        int idx;

        while(!pixelProcessQueue.isEmpty() && pixels.size() <= 1_000_000) {
            idx = random.nextInt(pixelProcessQueue.size());
            int pixelX = pixelProcessQueue.get(idx)[0];
            int pixelY = pixelProcessQueue.get(idx)[1];
            pixelProcessQueue.remove(idx);

            writerUtility(new Color(img.getRGB(pixelX, pixelY), true), x + pixelX, y + pixelY);

            while(spreadPixelCheckComplete(pM, pixelX, pixelY)) {
                switch (random.nextInt(4)) {
                    case 0:
                        if (spreadPixelCheck(pM, pixelX - 1, pixelY)) {
                            pM[pixelX - 1][pixelY] = true;
                            pixelProcessQueue.add(new int[]{pixelX - 1, pixelY});
                        }
                        break;
                    case 1:
                        if (spreadPixelCheck(pM, pixelX + 1, pixelY)) {
                            pM[pixelX + 1][pixelY] = true;
                            pixelProcessQueue.add(new int[]{pixelX + 1, pixelY});
                        }
                        break;
                    case 2:
                        if (spreadPixelCheck(pM, pixelX, pixelY - 1)) {
                            pM[pixelX][pixelY - 1] = true;
                            pixelProcessQueue.add(new int[]{pixelX, pixelY - 1});
                        }
                        break;
                    case 3:
                        if (spreadPixelCheck(pM, pixelX, pixelY + 1)) {
                            pM[pixelX][pixelY + 1] = true;
                            pixelProcessQueue.add(new int[]{pixelX, pixelY + 1});
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
        if (spreadContained) {
            return x >= 0 && x < pM.length && y >= 0 && y < pM[1].length && !pM[x][y] && new Color(img.getRGB(x, y), true).getAlpha() != 0;
        } else {
            return x >= 0 && x < pM.length && y >= 0 && y < pM[1].length && !pM[x][y];
        }
    }

    private void writerUtility (Color color, int i, int j) {
        if (color.getAlpha() > 230 && x + i >= 0 && x + i < 1000 && y + j >= 0 && y + j < 1000) {
            if (color.getAlpha() != 255 && color.getAlpha() != 0) {
                pixels.add((x + i) + " " + (y + j) + " " + rgbToHex(color) + " " + color.getAlpha());
            } else {
                pixels.add((x + i) + " " + (y + j) + " " + rgbToHex(color));
            }
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

    @Override
    public String getName() {
        return "PlaceEncode";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Returns a file containing the commands to draw the provided image");
        embed.addField("__Usage__", "```" + CONFIG.Prefix.get() + getName() + " <Position/Size> [<Pattern>] [<Parameters>]```", false);
        embed.addField("__Position/Size__", "Set the top left corner as well as the desired width and height" +
                "```<Position/Size> = <X> <Y> <width> <height>```", false);
        embed.addField("__<Pattern>__", "If nothing is provided or nothing can be matched, it defaults to `lefttoright`\n" +
                "```\ntopdown\ndiagonal\nspiral\nrandom\rcircle\nspread\nlefttoright```", false);
        embed.addField("__<Parameters>__", "These are completely optional and mostly only affect the `spread` pattern" +
                "```\n-c\t\t\t  forces spread to stay within image bounds" +
                "\n-r\t\t\t  reverses the list of commands" +
                "\n-s {<x> <y>}\tset starting positions for spread```", false);
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("pe");
    }
}
