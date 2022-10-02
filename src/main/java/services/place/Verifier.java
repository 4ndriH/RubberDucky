package services.place;

import assets.Objects.Pixel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Verifier {
    private static final Logger LOGGER = LoggerFactory.getLogger(Verifier.class);

    public static void verify() {
        BufferedImage place = PlaceWebSocket.getImage(true);

        LOGGER.warn("starting verification");

        for (int i = 0; i < PlaceData.drawnPixels; i++) {
            LOGGER.info("Starting verification of Pixel " + i);
            Pixel pixel = PlaceData.pixels.get(i);
            Color placeColor = new Color(place.getRGB(pixel.getX(), pixel.getY()));
            LOGGER.info("Pre check of pixel " + i);
            if (!compareColors(Color.decode(pixel.getPlaceColor()), placeColor)) {
                PlaceData.fixingQ.add(pixel);
                LOGGER.info("Pixel " + i + " needs to be fixed");
            }
            LOGGER.info("Post check of pixel " + i);
        }

        LOGGER.warn("force reloading image");
        LOGGER.warn(PlaceData.fixingQ.size() + " pixels to fix");
        PlaceData.forceReloadImage();
    }

    private static boolean compareColors (Color img, Color place) {
        LOGGER.info("do I get here?");
        return img.getRed() == place.getRed() && img.getGreen() == place.getGreen() && img.getBlue() == place.getBlue();
    }
}
