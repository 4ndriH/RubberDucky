package commandHandling.commands.place;

import commandHandling.CommandContext;
import services.BotExceptions;
import services.WebSocketClient;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.*;
import java.awt.*;
import java.io.*;
import java.util.concurrent.CountDownLatch;

public class preview {
    private final CommandContext ctx;

    public preview(CommandContext ctx) {
        this.ctx = ctx;
        previewing();
    }

    private void previewing() {
        CountDownLatch latch = new CountDownLatch(1);
        WebSocketClient wsc = new WebSocketClient(latch);
        Scanner scanner = null;
        BufferedImage img;

        try {
            scanner = new Scanner(ctx.getMessage().getReferencedMessage().getAttachments().get(0).retrieveInputStream().get());
        } catch (Exception e) {
            try {
                scanner = new Scanner(ctx.getMessage().getAttachments().get(0).retrieveInputStream().get());
            } catch (Exception ee) {
                BotExceptions.missingAttachmentException(ctx);
            }
            return;
        }

        WebSocket ws = HttpClient
                .newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(URI.create("ws://52.142.4.222:9000/place"), wsc)
                .join();
        ws.sendText(""+(char)1, true);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
        ByteBuffer buffer = WebSocketClient.buffer;
        int x = 0, y = 0;

        buffer.rewind();
        buffer.get();
        while (buffer.hasRemaining() && y < 1000) {
            Color color = new Color((255&buffer.get()), (255&buffer.get()), (255&buffer.get()));
            int rgb = (int)(color.getRed() * 0.299);
            rgb += (color.getGreen() * 0.587) + (color.getBlue() * 0.114);
            img.setRGB(x++, y, new Color(rgb, rgb, rgb).getRGB());
            if (x == 1000) {
                x = 0;
                y++;
            }
        }

        while (scanner.hasNextLine()) {
            Scanner s = new Scanner(scanner.nextLine().substring(16));
            img.setRGB(s.nextInt(), s.nextInt(), Color.decode(s.next()).getRGB());
            s.close();
        }

        ctx.getChannel().sendMessage("Preview").addFile(convert(img), "preview.png").queue();
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