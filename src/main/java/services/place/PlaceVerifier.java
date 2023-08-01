package services.place;

import assets.Objects.Pixel;
import assets.Objects.PlaceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Collections;

public class PlaceVerifier {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaceVerifier.class);

    public static void verify() {
        PlaceData.triggerPlaceImageReload();

        if (PlaceData.verify) {
            boolean[][] zoomiesImprovement = new boolean[1000][1000];

            for (int i = PlaceData.drawnPixels - 1; i >= 0 ; i--) {
                LOGGER.info("are we looping here?");
                LOGGER.info("i " + i);
                LOGGER.info("sze " + PlaceData.pixels.size());
                Pixel pixel = PlaceData.pixels.get(i);
                Color placeColor = PlaceData.getPixelColor(pixel.getX(), pixel.getY());
                LOGGER.info("pre if if");
                if (Color.decode(pixel.getPlaceColor()).getRGB() != placeColor.getRGB()) {
                    LOGGER.info("pre if");
                    if (!zoomiesImprovement[pixel.getX()][pixel.getY()]) {
                        zoomiesImprovement[pixel.getX()][pixel.getY()] = true;
                        PlaceData.fixingQ.add(pixel);
                    }
                }
                LOGGER.info("post if");
            }
            Collections.reverse(PlaceData.fixingQ);
        }
        LOGGER.info("are we done here?");
    }
}
