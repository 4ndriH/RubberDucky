package services.place;

import assets.Objects.Pixel;
import assets.Objects.PlaceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class PlaceVerifier {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaceVerifier.class);

    public static void verify() {
        PlaceData.triggerPlaceImageReload();

        if (PlaceData.verify) {
            for (int i = 0; i < PlaceData.drawnPixels; i++) {
                Pixel pixel = PlaceData.pixels.get(i);
                Color placeColor = PlaceData.getPixelColor(pixel.getX(), pixel.getY());
                if (Color.decode(pixel.getPlaceColor()).getRGB() != placeColor.getRGB()) {
                    PlaceData.fixingQ.remove(pixel);
                    PlaceData.fixingQ.add(pixel);
                }
            }
        }
    }
}
