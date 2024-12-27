package assets.objects;

import assets.Config;
import services.database.daos.PlacePixelsDAO;
import services.database.daos.PlaceProjectsDAO;
import services.place.PlaceWebSocket;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.LinkedList;
//import java.util.concurrent.locks.ReentrantLock;

public class PlaceData {
//    private final static ReentrantLock lock = new ReentrantLock();
//    private static LinkedList<String> requests;
    private static boolean runVerificationNow;
    private static BufferedImage place;
    private static long lastRequestTime;

    public static int ID, totalPixels, drawnPixels, fixedPixels;
    public static boolean drawing, stop, stopQ, verify, websocketFailed, finalVerification;
    public static LinkedList<Pixel>  fixingQ;
    public static ArrayList<Pixel> pixels;
    public static String user;

//    public PlaceData(int id) {
//        drawnPixels = DBHandlerPlace.getProjectProgress(id);
//        user = DBHandlerPlace.getProjectAuthor(id);
//        ID = id;
//
//        requests = new LinkedList<>();
//        fixingQ = new LinkedList<>();
//        pixels = DBHandlerPlace.getProjectPixels(id);
//
//        totalPixels = pixels.size();
//        fixedPixels = 0;
//        lastRequestTime = 0L;
//
//        drawing = true;
//        verify = Config.placeVerify;
//        stop = stopQ = websocketFailed = runVerificationNow = false;
//    }

    public static void newProject(int id) {
        PlaceProjectsDAO placeProjectsDAO = new PlaceProjectsDAO();
        PlacePixelsDAO placePixelsDAO = new PlacePixelsDAO();

        drawnPixels = placeProjectsDAO.getProjectProgress(id);
        user = placeProjectsDAO.getProjectAuthor(id);
        ID = id;

//        requests = new LinkedList<>();
        fixingQ = new LinkedList<>();
        pixels = placePixelsDAO.getPixels(id);

        totalPixels = pixels.size();
        fixedPixels = 0;
        lastRequestTime = 0L;

        drawing = true;
        verify = Config.PLACE_VERIFY;
        stop = stopQ = websocketFailed = runVerificationNow = finalVerification = false;
    }

    public static int getProgress() {
        return ((drawnPixels - (fixingQ.size() - fixedPixels)) * 100 / totalPixels);
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
        return verify && fixingQ.isEmpty() && !finalVerification && (drawnPixels % 2000 == 0 || drawnPixels == totalPixels || runVerificationNow);
    }

    public static boolean pixelsLeftToDraw() {
        return drawnPixels < totalPixels || (!fixingQ.isEmpty() && verify);
    }

    public static void triggerPlaceImageReload() {
        if (!websocketFailed) {
            place = PlaceWebSocket.getImage(true);
        } else {
            if (!runVerificationNow) {
                verify = false;
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

//    public static void addPixelRequest(String id) {
//        lock.lock();
//        try {
//            requests.add(id);
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    public static boolean openPixelRequests() {
//        lock.lock();
//        try {
//            return !requests.isEmpty();
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    public static String getPixelRequest() {
//        lock.lock();
//        try {
//            return requests.poll();
//        } finally {
//            lock.unlock();
//        }
//    }
}
