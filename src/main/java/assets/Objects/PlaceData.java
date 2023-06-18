package assets.Objects;

import assets.CONFIG;
import services.database.DBHandlerPlace;
import services.place.PlaceWebSocket;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

public class PlaceData {
    private final static ReentrantLock lock = new ReentrantLock();
    private static LinkedList<String> requests;
    private static boolean runVerificationNow;
    private static BufferedImage place;
    private static long lastRequestTime;

    public static int ID, totalPixels, drawnPixels, fixedPixels;
    public static boolean drawing, stop, stopQ, verify, websocketFailed;
    public static LinkedList<Pixel>  fixingQ;
    public static ArrayList<Pixel> pixels;
    public static String user;

    public PlaceData(int id) {
        drawnPixels = DBHandlerPlace.getProjectProgress(id);
        user = DBHandlerPlace.getProjectAuthor(id);
        ID = id;

        requests = new LinkedList<>();
        fixingQ = new LinkedList<>();
        pixels = DBHandlerPlace.getProjectPixels(id);

        totalPixels = pixels.size();
        fixedPixels = 0;
        lastRequestTime = 0L;

        drawing = true;
        verify = CONFIG.placeVerify;
        stop = stopQ = websocketFailed = runVerificationNow = false;
    }

    public static int getProgress() {
        return drawnPixels * 100 / totalPixels;
    }

    public static Pixel getPixel() {
        return pixels.get(drawnPixels);
    }

    public static Color getPixelColor(int x, int y) {
        if (System.currentTimeMillis() - lastRequestTime > 1800000) {
            if (!websocketFailed) {
                place = PlaceWebSocket.getImage(true);
                lastRequestTime = System.currentTimeMillis();
            } else {
                place = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
                lastRequestTime = Long.MAX_VALUE;
            }
        }

        return new Color(place.getRGB(x, y));
    }

    public static boolean verificationCondition() {
        return verify && fixingQ.isEmpty() && drawnPixels % 2000 == 0 || fixingQ.isEmpty() && drawnPixels == totalPixels || runVerificationNow;
    }

    public static void triggerPlaceImageReload() {
        if (!websocketFailed) {
            place = PlaceWebSocket.getImage(true);
        } else {
            if (!runVerificationNow) {
                verify = !websocketFailed;
            } else {
                runVerificationNow = false;
            }
        }

        lastRequestTime = System.currentTimeMillis();
    }

    public static void addPlaceImageManually(BufferedImage image) {
        runVerificationNow = true;
        websocketFailed = true;

        ColorModel cm = image.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        place = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public static void addPixelRequest(String id) {
        lock.lock();
        try {
            requests.add(id);
        } finally {
            lock.unlock();
        }
    }

    public static boolean openPixelRequests() {
        lock.lock();
        try {
            return !requests.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    public static String getPixelRequest() {
        lock.lock();
        try {
            return requests.poll();
        } finally {
            lock.unlock();
        }
    }
}
