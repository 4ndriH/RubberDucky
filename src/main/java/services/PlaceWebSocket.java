package services;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;

public class PlaceWebSocket {
    public static BufferedImage getImage () {
        CountDownLatch latch = new CountDownLatch(1);
        WebSocketClient wsc = new WebSocketClient(latch);
        BufferedImage img;

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