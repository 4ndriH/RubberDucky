package services.place;

import resources.Pixel;

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
    public static LinkedList<Pixel>  fixingQ;
    public static ArrayList<Pixel> pixels;
    private static LinkedList<String> requests;
    public static String user;

    public PlaceData(int ID, int drawnPixels, String user) {
        PlaceData.drawnPixels = drawnPixels;
        PlaceData.user = user;
        PlaceData.ID = ID;

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

    private ArrayList<Pixel> readPixelFile() {
        ArrayList<Pixel> pixels = new ArrayList<>();
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("tempFiles/place/queue/RDdraw" + ID + ".txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (scanner.hasNextLine()) {
            int x, y;
            double alpha;
            String color;
            try {
                String[] line = scanner.nextLine().split(" ");
                x = Integer.parseInt(line[0]);
                y = Integer.parseInt(line[1]);
                color = line[2];
                alpha = (line.length == 4) ? Integer.parseInt(line[3]) / 255.0 : 1.0;
            } catch (Exception e) {
                continue;
            }
            pixels.add(new Pixel(x, y, alpha, color));
        }
        scanner.close();

        return pixels;
    }
}
