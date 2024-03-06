package services.place;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;

public class WebSocketClient implements WebSocket.Listener {
    private final CountDownLatch latch;
    public static ByteBuffer buffer = ByteBuffer.allocate(0);
    public int liveCnt = 0;

    public WebSocketClient(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
        if (liveCnt > 10) {
            latch.countDown();
            WebSocket.Listener.super.onBinary(webSocket, data, last);
        }

        // weird packets arriving
        if (data.remaining() == 9 && last && buffer.remaining() < 1000) {
            liveCnt++;
            return WebSocket.Listener.super.onBinary(webSocket, data, last);
        }

        // concatenate the ByteBuffers
        buffer = ByteBuffer.allocate(buffer.remaining() + data.remaining()).put(buffer).put(data).flip();

        if (last) {
            latch.countDown();
        }

        return WebSocket.Listener.super.onBinary(webSocket, data, last);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        latch.countDown();
        WebSocket.Listener.super.onError(webSocket, error);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        latch.countDown();
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }
}
