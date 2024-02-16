package services.place;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class ChunkFetcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChunkFetcher.class);

    public static ArrayList<String> fetchChunks(int chunk) {
        byte[] send = new byte[]{(byte)8, (byte)0, (byte)0};
        send[2] = (byte) (chunk / 256);
        send[1] = (byte) (chunk % 256);

        ArrayList<String> pixels = new ArrayList<>();
        ByteBuffer buffer;

        do {
            CountDownLatch latch = new CountDownLatch(1);
            WebSocketClient wsc = new WebSocketClient(latch);
            WebSocket ws = HttpClient.newHttpClient().newWebSocketBuilder()
                    .buildAsync(URI.create("wss://ws.battlerush.dev/"), wsc).join();
            ws.sendBinary(ByteBuffer.wrap(send), true);
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ws.abort();
            buffer = wsc.buffer;
        } while (buffer.remaining() < 1_200_003);

        int r, g, b, x, y;
        int retryCnt = 0;

        do {
            if (++retryCnt > 5) {
                LOGGER.warn("Could not fetch chunk " + chunk, new IllegalArgumentException());
                return null;
            }

            pixels.clear();
            buffer.rewind();
            buffer.get(); buffer.get(); buffer.get();

            while (buffer.hasRemaining() && pixels.size() < 100000) {
                buffer.get(); buffer.get(); buffer.get(); buffer.get(); // skip 4 bytes because of index
                x = toCoord(255 & buffer.get(), 255 & buffer.get());
                y = toCoord(255 & buffer.get(), 255 & buffer.get());
                r = 255 & buffer.get();
                g = 255 & buffer.get();
                b = 255 & buffer.get();
                buffer.get();

                if (x >= 1000 || x < 0 || y >= 1000 || y < 0) {
                    throw new IllegalArgumentException("x: " + x + " - " + "y: " + y);
                }

                pixels.add(x + " " + y + " " + rgbToHex(new Color(r, g, b)));
            }
        } while (pixels.size() != 100000);

        return pixels;
    }

    private static int toCoord(int a, int b) {
        return toInt(toBinary(b) + toBinary(a));
    }

    private static String toBinary(int n) {
        String s = "";
        while (n > 0) {
            s = ((n % 2) == 0 ? "0" : "1") + s;
            n = n / 2;
        }

        while (s.length() < 8) {
            s = "0" + s;
        }

        return s;
    }

    private static int toInt(String s) {
        if (s.isEmpty()) {
            return 0;
        } else {
            return Integer.parseInt(s, 2);
        }
    }

    private static String rgbToHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }
}
