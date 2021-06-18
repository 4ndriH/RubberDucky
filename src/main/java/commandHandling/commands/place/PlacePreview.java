package commandHandling.commands.place;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import services.*;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class PlacePreview implements Runnable{
    private final CommandContext ctx;

    public PlacePreview(CommandContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        BufferedImage place = PlaceWebSocket.getImage(false);
        BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
        Scanner scanner;

        try {
            scanner = new Scanner(ctx.getMessage().getAttachments().get(0).retrieveInputStream().get());
        } catch (Exception e) {
            try {
                scanner = new Scanner(ctx.getMessage().getReferencedMessage().getAttachments().get(0)
                        .retrieveInputStream().get());
            } catch (Exception ee) {
                services.Logger.command(ctx, "place", false);
                BotExceptions.missingAttachmentException(ctx);
                return;
            }
        }

        services.Logger.command(ctx, "place", true);

        try {
            ImageOutputStream output = new FileImageOutputStream(new File("tempFiles/place/preview.gif"));
            GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_ARGB, 50, true);
            ArrayList<String> pixels = new ArrayList<>();

            while (scanner.hasNextLine()) {
                pixels.add(scanner.nextLine());
            }
            scanner.close();

            int pixelsPerFrame = (int)(pixels.size() * 0.012);
            writer.writeToSequence(place);

            for (int i = 0; i < pixels.size(); i++) {
                String[] pixel = pixels.get(i).split(" ");
                img.setRGB(Integer.parseInt(pixel[2]), Integer.parseInt(pixel[3]), Color.decode(pixel[4]).getRGB());
                if (i % pixelsPerFrame == 0) {
                    writer.writeToSequence(img);
                    img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
                }
            }

            for (int i = 0; i < 8; i++) {
                writer.writeToSequence(img);
            }

            writer.close();
            output.close();
        } catch (IOException e) {
            Logger.exception(ctx, e);
        }

        File gif = new File("tempFiles/place/preview.gif");

        try {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Preview");
            embed.setColor(new Color(0xb074ad));
            embed.setImage("attachment://preview.gif");
            ctx.getChannel().sendMessageEmbeds(embed.build()).addFile(gif).queue(
                    msg -> msg.delete().queueAfter(1024, TimeUnit.SECONDS)
            );
        } catch (IllegalArgumentException e) {
            Logger.exception(ctx, e);
            BotExceptions.FileExceedsUploadLimitException(ctx);
        }

        gif.delete();
    }
}