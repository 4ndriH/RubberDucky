package services.place;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class PlaceData {
    private final static ReentrantLock lock = new ReentrantLock();
    private static BufferedImage place;
    private static long time;

    public static int ID, totalPixels, drawnPixels, fixedPixels;
    public static boolean drawing, stop, stopQ, verify;
    public static LinkedList<String>  fixingQ;
    public static ArrayList<String> pixels, pixelVerify;
    private static LinkedList<String> requests;
    public static String user, file;

    PlaceData(int ID, int drawnPixels, String user, String file) {
        PlaceData.drawnPixels = drawnPixels;
        PlaceData.user = user;
        PlaceData.file = file;
        PlaceData.ID = ID;

        pixelVerify = new ArrayList<>();
        requests = new LinkedList<>();
        fixingQ = new LinkedList<>();
        pixels = readPixelFile();

        totalPixels = pixels.size();
        fixedPixels = 0;
        time = 0L;

        drawing = verify = true;
        stop = stopQ = false;
    }

    public static int getProgress() {
        return drawnPixels / totalPixels;
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

    private ArrayList<String> readPixelFile() {
        ArrayList<String> pxls = new ArrayList<>();
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("tempFiles/place/queue/RDdraw" + ID + ".txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (scanner.hasNextLine()) {
            pxls.add(scanner.nextLine());
        }
        scanner.close();

        return pxls;
    }
}
