package services.place;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;

public class PlaceWebSocket {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaceWebSocket.class);

    public static BufferedImage getImage (boolean colored) {
        ByteBuffer buffer;
        return new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);

//        do {
//            CountDownLatch latch = new CountDownLatch(1);
//            WebSocketClient wsc = new WebSocketClient(latch);
//            WebSocket ws;
//
//            try {
//                ws = HttpClient
//                    .newHttpClient()
//                    .newWebSocketBuilder()
//                    .buildAsync(URI.create("wss://place.battlerush.dev:9000/place"), wsc)
//                    .join();
//            } catch (Exception e) {
//                LOGGER.error("Websocket Problem", e);
//                return new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
//            }
//
//            WebSocketClient.buffer = ByteBuffer.allocate(0);
//            ws.sendText(""+(char)1, true);
//
//            try {
//                latch.await();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            buffer = WebSocketClient.buffer;
//            ws.abort();
//        } while (buffer.remaining() <= 3000000);
//
//        BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
//        int x = 0, y = 0;
//
//        buffer.position(0);
//        buffer.get();
//
//        // write the received data to a buffered image, either colored or grey scaled
//        if (buffer.remaining() >= 3000000) {
//            while (buffer.hasRemaining() && y < 1000) {
//                int r = 255&buffer.get(), g = 255&buffer.get(), b = 255&buffer.get();
//                Color color;
//
//                if (colored) {
//                    color = new Color(r, g, b);
//                } else {
//                    int rgb = (int)(r * 0.299) + (int)(g * 0.587) + (int)(b * 0.114);
//                    color = new Color(rgb, rgb, rgb);
//                }
//
//                img.setRGB(x++, y, color.getRGB());
//                if (x == 1000) {
//                    x = 0;
//                    y++;
//                }
//            }
//        }
//
//        // Sleep so the buffered image is not just black before it gets returned
//        try {
//            Thread.sleep(128);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        return img;
    }

    private static class WebSocketClient implements WebSocket.Listener {
        private final CountDownLatch latch;
        public static ByteBuffer buffer = ByteBuffer.allocate(0);

        public WebSocketClient(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
            // Live Pixels arriving
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

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            latch.countDown();
            Listener.super.onError(webSocket, error);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            latch.countDown();
            return Listener.super.onClose(webSocket, statusCode, reason);
        }
    }
}