package commandHandling.commands.place;

import services.PlaceWebSocket;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class PlaceVerify {
    private final PlaceData placeData;

    public PlaceVerify(PlaceData placeData) {
        this.placeData = placeData;
        main();
    }

    private void main() {
        BufferedImage place = PlaceWebSocket.getImage(true);
        LinkedList<String> fixingQ = new LinkedList<>();

        for (int i = 0; i < placeData.drawnPixels; i++) {
            String command = placeData.pixels.get(i);
            String[] split = command.split(" ");
            int x = Integer.parseInt(split[2]);
            int y = Integer.parseInt(split[3]);
            Color colour = Color.decode(split[4]);
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
