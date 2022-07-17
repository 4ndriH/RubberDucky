package services.place;

import assets.Objects.Pixel;
import net.dv8tion.jda.api.entities.ThreadChannel;
import services.database.DBHandlerPlace;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class PlaceData {
    private final static ReentrantLock lock = new ReentrantLock();
    private static BufferedImage place;
    private static long time;

    public static int ID, totalPixels, drawnPixels, fixedPixels;
    public static boolean drawing, stop, stopQ, verify;
    public static LinkedList<Pixel>  fixingQ;
    public static ArrayList<Pixel> pixels;
    public static ArrayList<ThreadChannel> threads;
    private static LinkedList<String> requests;
    public static String user;

    public PlaceData(int ID) {
        PlaceData.drawnPixels = DBHandlerPlace.getProjectProgress(ID);
        PlaceData.user = DBHandlerPlace.getProjectAuthor(ID);
        PlaceData.ID = ID;

        requests = new LinkedList<>();
        fixingQ = new LinkedList<>();
        threads = new ArrayList<>();
        pixels = readPixelFile();

        totalPixels = pixels.size();
        fixedPixels = 0;
        time = 0L;

        drawing = verify = true;
        stop = stopQ = false;
    }

    public static int getProgress() {
        return drawnPixels * 100 / totalPixels;
    }

    public static Pixel getPixel() {
        return pixels.get(drawnPixels);
    }

    public static Color getPixelColor(int x, int y) {
        if (System.currentTimeMillis() - time > 1800000) {
            place = PlaceWebSocket.getImage(true);
        }
        return new Color(place.getRGB(x, y));
    }

    public static void forceReloadImage() {
        time = 0L;
    }

    public static void addThreads(List<ThreadChannel> ts) {
        for (ThreadChannel t : ts) {
            if (t.getName().contains("phdt")) {
                threads.add(t);
            }
        }
    }

    public static void addPixelRequest(String id) {
        lock.lock();
        try {
            requests.add(id);
        } finally {
            lock.unlock();
        }
    }

    public static String getPixelRequests() {
        lock.lock();
        try {
            return requests.poll();
        } finally {
            lock.unlock();
        }
    }

    private ArrayList<Pixel> readPixelFile() {
        return DBHandlerPlace.getProjectPixels(ID);
    }
}
