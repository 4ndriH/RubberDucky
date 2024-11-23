package commandhandling.commands.place;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import assets.Config;
import assets.objects.Pixel;
import services.BotExceptions;
import services.database.daos.PlacePixelsDAO;
import services.database.daos.PlaceProjectsDAO;
import services.miscellaneous.GifSequenceWriter;
import services.discordhelpers.EmbedHelper;
import services.place.PlaceWebSocket;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;

import static services.discordhelpers.MessageSendHelper.sendMessageComplete;

public class PlacePreview implements CommandInterface {
    private static final Pattern argumentPattern = Pattern.compile("^(?:10000|[1-9][0-9]{0,3}|0)?\\s?$");
    private final Logger LOGGER = LoggerFactory.getLogger(PlacePreview.class);

    @Override
    public void handle(CommandContext ctx) {
        BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);

        BufferedImage place = PlaceWebSocket.getImage(false);
        ArrayList<Pixel> pixels;
        // 0 = ID, 1 = sent, 2 = replied to
        int sendMessageCase, id = -1;

        if (!ctx.getArguments().isEmpty()) {
            try {
                id = Integer.parseInt(ctx.getArguments().get(0));
                sendMessageCase = 0;
            } catch (Exception e) {
                BotExceptions.invalidArgumentsException(ctx);
                return;
            }

            PlaceProjectsDAO placeProjectsDAO = new PlaceProjectsDAO();

            if (placeProjectsDAO.getProjectIds().contains(id)) {
                PlacePixelsDAO placePixelsDAO = new PlacePixelsDAO();
                pixels = placePixelsDAO.getPixels(id);
            } else {
                BotExceptions.fileDoesNotExistException(ctx);
                return;
            }
        } else {
            pixels = new ArrayList<>();
            Scanner scanner;

            try {
                scanner = new Scanner(ctx.getMessage().getAttachments().get(0).getProxy().download().get());
                sendMessageCase = 1;
            } catch (Exception e) {
                try {
                    scanner = new Scanner(Objects.requireNonNull(ctx.getMessage().getReferencedMessage()).getAttachments().get(0)
                            .getProxy().download().get());
                    sendMessageCase = 2;
                } catch (Exception ee) {
                    BotExceptions.missingAttachmentException(ctx);
                    return;
                }
            }

            while (scanner.hasNextLine()) {
                int x, y;
                double alpha;
                String color;
                try {
                    String[] line = scanner.nextLine().replace(".place setpixel ", "").split(" ");
                    x = Integer.parseInt(line[0]);
                    y = Integer.parseInt(line[1]);
                    color = line[2];
                    alpha = (line.length == 4) ? Integer.parseInt(line[3]) / 255.0 : 1.0;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    continue;
                }
                pixels.add(new Pixel(x, y, alpha, color));
            }
            scanner.close();
        }

        try {
            ImageOutputStream output = new FileImageOutputStream(new File(Config.DIRECTORY_PATH + "tempFiles/place/preview.gif"));
            GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_ARGB, 50, true);
            boolean exception = false;

            int pixelsPerFrame = Math.max(1, (int)(pixels.size() * 0.005));
            writer.writeToSequence(place);

            for (int i = 0; i < pixels.size(); i++) {
                Pixel pixel = pixels.get(i);
                try {
                    img.setRGB(pixel.getX(), pixel.getY(), Color.decode(pixel.getColor(false)).getRGB());
                    if (i % pixelsPerFrame == 0) {
                        writer.writeToSequence(img);
                        img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
                    }
                } catch (Exception e) {
                    if (!exception) {
                        exception = true;
                        BotExceptions.faultyPixelFormatException(ctx, pixels.get(i).toString());
                    }
                }
            }

            for (int i = 0; i < 64; i++) {
                writer.writeToSequence(img);
            }

            writer.close();
            output.close();
        } catch (Exception e) {
            LOGGER.error("PlacePreview Error", e);
        }

        File gif = new File(Config.DIRECTORY_PATH + "tempFiles/place/preview.gif");

        try {
            EmbedBuilder embed = EmbedHelper.embedBuilder("Preview" + (sendMessageCase == 0 ? " - " + id : ""));
            embed.setImage("attachment://preview.gif");
            MessageCreateAction mca;

            switch (sendMessageCase) {
                case 1 -> mca = ctx.getMessage().replyEmbeds(embed.build()).addFiles(FileUpload.fromData(gif));
                case 2 -> mca = ctx.getMessage().getReferencedMessage().replyEmbeds(embed.build()).addFiles(FileUpload.fromData(gif));
                default -> mca = ctx.getChannel().sendMessageEmbeds(embed.build()).addFiles(FileUpload.fromData(gif));
            }
            sendMessageComplete(mca, 1024);
        } catch (IllegalArgumentException e) {
            LOGGER.error("PlacePreview Error", e);
            BotExceptions.FileExceedsUploadLimitException(ctx);
        }

        if (gif.delete()) {
            LOGGER.debug("Deleted file: " + gif.getName());
        } else {
            LOGGER.error("Failed to delete file: " + gif.getName());
        }
    }

    @Override
    public String getName() {
        return "PlacePreview";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Returns an animated preview of the given project\n" +
                             "You can either specify the ID, reply to a text file or send it as an attachment");
        embed.addField("__Usage__", "```" + Config.PREFIX + getName() + " [<ID>]```", false);
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("pp");
    }

    @Override
    public boolean argumentCheck(StringBuilder args) {
        return argumentPattern.matcher(args).matches();
    }

    @Override
    public boolean attachmentCheck(CommandContext ctx) {
        if (!ctx.getMessage().getAttachments().isEmpty()) {
            String type = Objects.requireNonNull(ctx.getMessage().getAttachments().get(0).getContentType()).split("/")[1];

            return type.startsWith("plain");
        }
        return true;
    }
}
