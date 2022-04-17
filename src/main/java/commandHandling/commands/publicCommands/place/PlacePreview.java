package commandHandling.commands.publicCommands.place;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.Pixel;
import services.BotExceptions;
import services.GifSequenceWriter;
import services.database.DatabaseHandler;
import services.logging.EmbedHelper;
import services.place.PlaceWebSocket;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PlacePreview implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(PlacePreview.class);

    public PlacePreview(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
        BufferedImage place = PlaceWebSocket.getImage(false);
        ArrayList<Pixel> pixels;
        // 0 = ID, 1 = sent, 2 = replied to
        int sendMessageCase, id = -1;

        if (ctx.getArguments().size() > 0) {
            try {
                id = Integer.parseInt(ctx.getArguments().get(0));
                sendMessageCase = 0;
            } catch (Exception e) {
                BotExceptions.invalidArgumentsException(ctx);
                return;
            }

            if (DatabaseHandler.getPlaceProjectIDs().contains(id)) {
                    pixels = DatabaseHandler.getPlacePixels(id);
            } else {
                BotExceptions.fileDoesNotExistException(ctx);
                return;
            }
        } else {
            pixels = new ArrayList<>();
            Scanner scanner;

            try {
                scanner = new Scanner(ctx.getMessage().getAttachments().get(0).retrieveInputStream().get());
                sendMessageCase = 1;
            } catch (Exception e) {
                try {
                    scanner = new Scanner(ctx.getMessage().getReferencedMessage().getAttachments().get(0)
                            .retrieveInputStream().get());
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
            ImageOutputStream output = new FileImageOutputStream(new File("tempFiles/place/preview.gif"));
            GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_ARGB, 50, true);
            boolean exception = false;

            int pixelsPerFrame = Math.max(1, (int)(pixels.size() * 0.005));
            writer.writeToSequence(place);

            for (int i = 0; i < pixels.size(); i++) {
                Pixel pixel = pixels.get(i);
                try {
                    img.setRGB(pixel.getX(), pixel.getY(), Color.decode(pixel.getColor()).getRGB());
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

        File gif = new File("tempFiles/place/preview.gif");

        try {
            EmbedBuilder embed = EmbedHelper.embedBuilder("Preview" + (sendMessageCase == 0 ? " - " + id : ""));
            embed.setImage("attachment://preview.gif");
            switch (sendMessageCase) {
                case 0:
                    ctx.getChannel().sendMessageEmbeds(embed.build()).addFile(gif).queue(
                            msg -> EmbedHelper.deleteMsg(msg, 1024)
                    );
                    break;
                case 1:
                    ctx.getMessage().replyEmbeds(embed.build()).addFile(gif).queue(
                            msg -> EmbedHelper.deleteMsg(msg, 1024)
                    );
                    break;
                case 2:
                    ctx.getMessage().getReferencedMessage().replyEmbeds(embed.build()).addFile(gif).queue(
                            msg -> EmbedHelper.deleteMsg(msg, 1024)
                    );
            }
        } catch (IllegalArgumentException e) {
            LOGGER.error("PlacePreview Error", e);
            BotExceptions.FileExceedsUploadLimitException(ctx);
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        gif.delete();
    }

    @Override
    public String getName() {
        return "PlacePreview";
    }

    @Override
    public EmbedBuilder getHelp() {
        return null;
    }

    @Override
    public List<String> getAliases() {
        return List.of("pp");
    }
}
