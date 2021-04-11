package commandHandling.commands.place;

import services.PlaceWebSocket;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class verify {
    private final placeData placeData;

    public verify(placeData placeData) {
        this.placeData = placeData;
        verifier();
    }

    private void verifier() {
        Color[][] img = placeData.getImg();
        BufferedImage place = PlaceWebSocket.getImage();
        LinkedList<String> fixingQ = new LinkedList<>();

        for (int y = 0; y < img.length; y++) {
            for (int x = 0; x < img.length; x++) {
                if (img[x][y] != null && !compareColors(img[x][y], new Color(place.getRGB(x, y)))) {
                    fixingQ.add(".place setpixel " + x + " " + y + " " + rgbToHex(img[x][y]));
                }
            }
        }

        placeData.fixingQ = fixingQ;
    }

    private boolean compareColors (Color img, Color place) {
        return img.getRed() == place.getRed() &&
               img.getGreen() == place.getGreen() &&
               img.getBlue() == place.getBlue();
    }

    private String rgbToHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }
}
