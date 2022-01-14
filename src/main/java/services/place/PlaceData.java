package services.place;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

public class PlaceData {
    private final static ReentrantLock lock = new ReentrantLock();
    private static BufferedImage place;
    private static long time;

    public static int ID, totalPixels, drawnPixels, fixedPixels;
    public static boolean drawing, stop, stopQ, verify;
    public static LinkedList<String>  fixingQ;
    public static ArrayList<String> pixels;
    private static LinkedList<String> requests;
    public static String user, file;

    PlaceData(int ID, String user, String file) {
        totalPixels = drawnPixels = fixedPixels = 0;
        requests = new LinkedList<>();
        fixingQ = new LinkedList<>();
        pixels = readPixelFile();
        drawing = verify = true;
        stop = stopQ = false;
        PlaceData.ID = ID;
        PlaceData.user = user;
        PlaceData.file = file;
        time = 0L;
    }

    private ArrayList<String> readPixelFile() {
        return new ArrayList();
    }

    public static Color getPixelColor(int x, int y) {
        if (System.currentTimeMillis() - time > 1800000) {
            place = PlaceWebSocket.getImage(true);
        }
        return new Color(place.getRGB(x, y));
    }

    public static void addRequest(String id) {
        lock.lock();
        try {
            requests.add(id);
        } finally {
            lock.unlock();
        }
    }

    public static String getRequests() {
        lock.lock();
        try {
            return requests.poll();
        } finally {
            lock.unlock();
        }
    }
}
