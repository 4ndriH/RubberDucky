package services.place;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PlaceVerify {
    public static void verify() {
        BufferedImage place = PlaceWebSocket.getImage(true);

    }

    private boolean compareColors (Color img, Color place) {
        return img.getRed() == place.getRed() &&
                img.getGreen() == place.getGreen() &&
                img.getBlue() == place.getBlue();
    }
}
