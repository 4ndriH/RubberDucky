package commandhandling.commands.place;

import assets.objects.Pixel;
import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import assets.Config;
import services.BotExceptions;
import services.miscellaneous.Format;
import services.place.patterns.*;
import services.place.patterns.Random;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

import static services.discordhelpers.MessageSendHelper.sendMessage;

public class PlaceEncode implements CommandInterface {
    public final Pattern argumentPattern = Pattern.compile("(?:\\d{1,3} ?|1000 ?){4}(?: (?:-r|-c)){0,2}(?:topdown|td|diagonal|spiral|random|circle|spread|lefttoright|lr|)(?: (?:-r|-c|-s(?: (?:\\d{1,3} |1000 )(?:\\d{1,3}|1000) ?)+)){0,3} ?");
    private static final List<String> types = List.of("jpg", "jpeg", "png");
    private final Logger LOGGER = LoggerFactory.getLogger(PlaceEncode.class);

    @Override
    public void handle(CommandContext ctx) {
        String fileName, pattern = "lefttoright";
        ArrayList<Pixel> pixels;
        BufferedImage img ;
        ArrayList<int[]> startingPoints = new ArrayList<>();
        boolean spreadContained = false;

        try {
            fileName = ctx.getMessage().getAttachments().get(0).getFileName();
            fileName = fileName.substring(0, fileName.length() - 4) + ".txt";
            img = ImageIO.read(new URL(ctx.getMessage().getAttachments().get(0).getUrl()));
        } catch (IOException e) {
            LOGGER.error("PlaceEncode Error", e);
            return;
        }

        ctx.getMessage().delete().queue();

        int xOffset = Integer.parseInt(ctx.getArguments().get(0));
        int yOffset = Integer.parseInt(ctx.getArguments().get(1));
        int width = Integer.parseInt(ctx.getArguments().get(2));
        int height = Integer.parseInt(ctx.getArguments().get(3));

        if (ctx.getArguments().size() >= 5) {
            pattern = ctx.getArguments().get(4).toLowerCase();
        }

        if (ctx.getArguments().contains("-c")) {
            spreadContained = true;
        }

        if (ctx.getArguments().contains("-s")) {
            int dashSIdx = ctx.getArguments().indexOf("-s");
            int lastDashIdx = ctx.getArguments().lastIndexOf("-");

            for (int i = dashSIdx + 1; i + 1 < ctx.getArguments().size() || i + 1 < lastDashIdx; i += 2) {
                int x = Integer.parseInt(ctx.getArguments().get(i));
                int y = Integer.parseInt(ctx.getArguments().get(i + 1));
                startingPoints.add(new int[]{x, y});
            }
        }

        LOGGER.debug("xOffset: " + xOffset + " yOffset: " + yOffset + " width: " + width + " height: " + height);
        LOGGER.debug("Pattern: " + pattern);
        LOGGER.debug("SpreadContained: " + spreadContained);
        LOGGER.debug("StartingPoints: " + startingPoints);

        img = resize(img, width, height);

        LOGGER.debug("Image Resized, Width: " + img.getWidth() + " Height: " + img.getHeight());

        switch (pattern) {
            case "topdown", "td" -> pixels = TopDown.encode(img, xOffset, yOffset);
            case "diagonal"      -> pixels = Diagonal.encode(img, xOffset, yOffset);
            case "spiral"        -> pixels = Spiral.encode(img, xOffset, yOffset);
            case "random"        -> pixels = Random.encode(img, xOffset, yOffset);
            case "circle"        -> pixels = Circle.encode(img, xOffset, yOffset);
            case "spread"        -> pixels = Spread.encode(img, xOffset, yOffset, spreadContained, startingPoints);
            default              -> pixels = LeftToRight.encode(img, xOffset, yOffset);
        }

        if (ctx.getArguments().contains("-r")) {
            Collections.reverse(pixels);
        }

        StringBuilder sb = new StringBuilder();

        LOGGER.debug("Pixels: " + pixels.size());

        for (Pixel p : pixels) {
            sb.append(p).append("\n");
        }

        InputStream stream = new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));

        try {
            MessageCreateAction mca = ctx.getChannel().sendMessage("Estimated drawing time: \n**" + Format.Time((int)(pixels.size() * 1.0587)) + "**").addFiles(FileUpload.fromData(stream, fileName));
            sendMessage(ctx, mca, 128);
        } catch (IllegalArgumentException e) {
            LOGGER.error("File Upload Limit", e);
            BotExceptions.FileExceedsUploadLimitException(ctx);
        }
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
        embed.addField("__Usage__", "```" + Config.prefix + getName() + " <Position/Size> [<Pattern>] [<Parameters>]```", false);
        embed.addField("__<Position/Size>__", "Set the top left corner as well as the desired width and height" +
                                                          "```<Position/Size> = <X> <Y> <width> <height>```", false);
        embed.addField("__<Pattern>__", """
                If nothing is provided or nothing can be matched, it defaults to `lefttoright`
                ```
                topdown
                diagonal
                spiral
                random
                circle
                spread
                lefttoright```""", false);
        embed.addField("__<Parameters>__", """
            These are completely optional and mostly only affect the `spread` pattern
            ```-c             forces spread to stay within image bounds
            -r             reverses the list of commands
            -s {<x> <y>}   set starting positions for spread```
            """, false);
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("pe");
    }

    @Override
    public boolean argumentCheck(StringBuilder args) {
        return argumentPattern.matcher(args).matches();
    }

    @Override
    public boolean attachmentCheck(CommandContext ctx) {
        if (ctx.getMessage().getAttachments().isEmpty()) {
            return false;
        }

        String type = Objects.requireNonNull(ctx.getMessage().getAttachments().get(0).getContentType()).split("/")[1];

        return types.contains(type);
    }
}
