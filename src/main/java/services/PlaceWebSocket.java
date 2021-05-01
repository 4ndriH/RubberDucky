package services;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;

public class PlaceWebSocket {
    public static BufferedImage getImage () {
        ByteBuffer buffer;
        int maxAttempts = 4;

        do {
            CountDownLatch latch = new CountDownLatch(1);
            WebSocketClient wsc = new WebSocketClient(latch);

            WebSocket ws = HttpClient
                    .newHttpClient()
                    .newWebSocketBuilder()
                    .buildAsync(URI.create("wss://place.battlerush.dev/place:9000"), wsc)
                    .join();

            WebSocketClient.buffer = ByteBuffer.allocate(0);
            ws.sendText(""+(char)1, true);

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            buffer = WebSocketClient.buffer;
        } while (buffer.remaining() <= 3000000 && --maxAttempts > 0);

        BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
        int x = 0, y = 0;

        buffer.position(0);
        buffer.get();

        if (buffer.remaining() >= 3000000) {
            while (buffer.hasRemaining() && y < 1000) {
                Color color = new Color(255&buffer.get(), 255&buffer.get(), 255&buffer.get());
                img.setRGB(x++, y, color.getRGB());
                if (x == 1000) {
                    x = 0;
                    y++;
                }
            }
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return img;
    }

    private static class WebSocketClient implements WebSocket.Listener {
        private final CountDownLatch latch;
        public static ByteBuffer buffer = ByteBuffer.allocate(0);

        public WebSocketClient(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
            // weird packets arriving
            if(data.remaining() == 9 && last && buffer.remaining() < 1000) {
                return WebSocket.Listener.super.onBinary(webSocket, data, last);
            }

            // concatenate the ByteBuffers
            buffer = ByteBuffer.allocate(buffer.remaining() + data.remaining()).put(buffer).put(data).flip();

            if(last) {
                latch.countDown();
            }

            return WebSocket.Listener.super.onBinary(webSocket, data, last);
        }
    }
}