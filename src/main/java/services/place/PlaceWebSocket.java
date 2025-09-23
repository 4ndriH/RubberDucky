package services.place;

import assets.objects.PlaceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;

public class PlaceWebSocket {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaceWebSocket.class);

    public static BufferedImage getImage (boolean colored) {
        BufferedImage img;
        ByteBuffer buffer;

        do {
            img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);

            do {
                CountDownLatch latch = new CountDownLatch(1);
                WebSocketClient wsc = new WebSocketClient(latch);
                WebSocket ws;

                try {
                    ws = HttpClient
                            .newHttpClient()
                            .newWebSocketBuilder()
                            .connectTimeout(java.time.Duration.ofSeconds(5))
                            .buildAsync(URI.create("wss://ws.battlerush.dev/"), wsc)
                            .join();
                } catch (CompletionException ce) {
                    LOGGER.error("Websocket Dead", ce);
                    PlaceData.websocketFailed = true;
                    return blameKarloPng();
                } catch(Exception e) {
                    LOGGER.error("Websocket Problem", e);
                    return img;
                }

                WebSocketClient.buffer = ByteBuffer.allocate(0);
                ws.sendText(""+(char)1, true);

                try {
                    latch.await();
                } catch (InterruptedException e) {
                    LOGGER.error("Latch Interrupted", e);
                }

                buffer = WebSocketClient.buffer;
                ws.abort();
            } while (buffer.remaining() <= 3000000);

            int x = 0, y = 0;

            buffer.position(0);
            buffer.get();

            // write the received data to a buffered image, either colored or grey scaled
            if (buffer.remaining() >= 3000000) {
                while (buffer.hasRemaining() && y < 1000) {
                    int r = 255&buffer.get(), g = 255&buffer.get(), b = 255&buffer.get();
                    Color color;

                    if (colored) {
                        color = new Color(r, g, b);
                    } else {
                        int rgb = (int)(r * 0.299) + (int)(g * 0.587) + (int)(b * 0.114);
                        color = new Color(rgb, rgb, rgb);
                    }

                    img.setRGB(x++, y, color.getRGB());
                    if (x == 1000) {
                        x = 0;
                        y++;
                    }
                }
            }

            // if the image is not complete, try again
            for (int i = 0; i < 1000; i++) {
                if (new Color(img.getRGB(i, i), true).getAlpha() == 0) {
                    img = null;
                    break;
                }
            }
        } while (img == null);

        return img;
    }

    private static BufferedImage blameKarloPng() {
        final BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);

        java.time.LocalDate start = java.time.LocalDate.of(2024, 3, 9);
        long days = java.time.temporal.ChronoUnit.DAYS.between(start, java.time.LocalDate.now());

        String line1 = "Karlos Websocket is Broken";
        String line2 = "It has been " + days + " days since it worked";

        Graphics2D g = img.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // semi-transparent background box
            int boxWidth = 900;
            int boxHeight = 180;
            int boxX = (img.getWidth() - boxWidth) / 2;
            int boxY = (img.getHeight() - boxHeight) / 2;
            g.setColor(new Color(240, 0, 0, 170));
            g.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

            // fonts and metrics
            Font f1 = new Font("SansSerif", Font.BOLD, 48);
            Font f2 = new Font("SansSerif", Font.PLAIN, 36);
            g.setFont(f1);
            FontMetrics fm1 = g.getFontMetrics(f1);
            FontMetrics fm2 = g.getFontMetrics(f2);

            // compute vertical positions to center both lines
            int paddingBetween = 10;
            int totalTextHeight = fm1.getHeight() + fm2.getHeight() + paddingBetween;
            int startY = img.getHeight() / 2 - totalTextHeight / 2 + fm1.getAscent();
            int y1 = startY;
            int y2 = startY + fm1.getHeight() + paddingBetween;

            // draw first line
            int x1 = img.getWidth() / 2 - fm1.stringWidth(line1) / 2;
            g.setColor(Color.WHITE);
            g.setFont(f1);
            g.drawString(line1, x1, y1);

            // draw second line
            int x2 = img.getWidth() / 2 - fm2.stringWidth(line2) / 2;
            g.setFont(f2);
            g.drawString(line2, x2, y2);
        } catch(Exception e) {
          LOGGER.error("Error", e);
        } finally {
            g.dispose();
        }

        return img;
    }
}