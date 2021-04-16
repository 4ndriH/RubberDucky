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

public class preview {
    private final CommandContext ctx;

    public preview(CommandContext ctx) {
        this.ctx = ctx;
        main();
    }

    private void main() {
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