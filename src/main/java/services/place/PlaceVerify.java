package services.place;

import resources.Pixel;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PlaceVerify {
    public static void verify() {
        BufferedImage place = PlaceWebSocket.getImage(true);

        for (int i = 0; i < PlaceData.drawnPixels; i++) {
            Pixel pixel = PlaceData.pixels.get(i);
            Color placeColor = new Color(place.getRGB(pixel.getX(), pixel.getY()));
            if (!compareColors(Color.decode((pixel.getAlpha() == 1.0 ? pixel.getImageColor() : pixel.getPlaceColor())), placeColor)) {
                PlaceData.fixingQ.add(pixel);
            }
        }
    }

    private static boolean compareColors (Color img, Color place) {
        return img.getRed() == place.getRed() && img.getGreen() == place.getGreen() && img.getBlue() == place.getBlue();
    }
}
