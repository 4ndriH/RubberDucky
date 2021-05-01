package commandHandling.commands.place;

import commandHandling.CommandContext;
import services.BotExceptions;
import services.PlaceWebSocket;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class preview implements Runnable{
    private final CommandContext ctx;

    public preview(CommandContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        BufferedImage img = PlaceWebSocket.getImage();
        Scanner scanner;

        try {
            scanner = new Scanner(ctx.getMessage().getAttachments().get(0).retrieveInputStream().get());
        } catch (Exception e) {
            try {
                scanner = new Scanner(ctx.getMessage().getReferencedMessage().getAttachments().get(0)
                        .retrieveInputStream().get());
            } catch (Exception ee) {
                BotExceptions.missingAttachmentException(ctx);
                return;
            }
        }

        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                Color color = new Color(img.getRGB(i, j));
                int rgb = (int)(color.getRed() * 0.299);
                rgb += (color.getGreen() * 0.587) + (color.getBlue() * 0.114);
                img.setRGB(i, j, new Color(rgb, rgb, rgb).getRGB());
            }
        }

        while (scanner.hasNextLine()) {
            Scanner s = new Scanner(scanner.nextLine().substring(16));
            img.setRGB(s.nextInt(), s.nextInt(), Color.decode(s.next()).getRGB());
            s.close();
        }

        ctx.getChannel().sendMessage("Preview").addFile(convert(img), "preview.png").queue(
                msg -> msg.delete().queueAfter(64, TimeUnit.SECONDS)
        );
    }

    private InputStream convert (BufferedImage img) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(os.toByteArray());
    }
}