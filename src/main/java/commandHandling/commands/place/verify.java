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
        BufferedImage place = PlaceWebSocket.getImage();
        LinkedList<String> fixingQ = new LinkedList<>();

        for (int i = 0; i <= placeData.drawnPixels; i++) {
            String command = placeData.pixels.get(i);
            int x = Integer.parseInt(command.substring(16, 19));
            int y = Integer.parseInt(command.substring(20, 23));
            Color colour = Color.decode(command.substring(24,31));
            if (!compareColors(colour, new Color(place.getRGB(x, y)))) {
                fixingQ.add(command);
            }
        }

        placeData.fixingQ = fixingQ;
    }

    private boolean compareColors (Color img, Color place) {
        return img.getRed() == place.getRed() &&
               img.getGreen() == place.getGreen() &&
               img.getBlue() == place.getBlue();
    }
}
