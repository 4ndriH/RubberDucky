package services.place;

import assets.objects.Pixel;
import assets.objects.PlaceData;

import java.awt.*;
import java.util.Collections;

public class PlaceVerifier {
    public static void verify() {
        PlaceData.triggerPlaceImageReload();

        if (PlaceData.verify) {
            boolean[][] zoomiesImprovement = new boolean[1000][1000];

            for (int i = PlaceData.drawnPixels - 1; i >= 0 ; i--) {
                Pixel pixel = PlaceData.pixels.get(i);
                Color placeColor = PlaceData.getPixelColor(pixel.getX(), pixel.getY());
                if (Color.decode(pixel.getPlaceColor()).getRGB() != placeColor.getRGB()) {
                    if (!zoomiesImprovement[pixel.getX()][pixel.getY()]) {
                        zoomiesImprovement[pixel.getX()][pixel.getY()] = true;
                        PlaceData.fixingQ.add(pixel);
                    }
                }
            }
            Collections.reverse(PlaceData.fixingQ);
        }
    }
}
