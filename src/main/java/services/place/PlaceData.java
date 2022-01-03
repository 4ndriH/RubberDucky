package services.place;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

public class PlaceData {
    private final static ReentrantLock lock = new ReentrantLock();
    public static int ID, totalPixels, drawnPixels, fixedPixels;
    public static boolean drawing, stop, stopQ, verify;
    public static LinkedList<String> pixels, fixingQ;
    public static ArrayList<String> pixelsDrawn;
    private static LinkedList<String> requests;
    public static String user, file;

    PlaceData(int ID, String user, String file) {
        totalPixels = drawnPixels = fixedPixels = 0;
        pixelsDrawn = new ArrayList<>();
        requests = new LinkedList<>();
        fixingQ = new LinkedList<>();
        pixels = readPixelFile();
        drawing = verify = true;
        stop = stopQ = false;
        PlaceData.ID = ID;
        PlaceData.user = user;
        PlaceData.file = file;
    }

    private LinkedList<String> readPixelFile() {
        return new LinkedList();
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
