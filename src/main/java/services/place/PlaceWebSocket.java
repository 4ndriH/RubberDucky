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
                    return img;
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
}