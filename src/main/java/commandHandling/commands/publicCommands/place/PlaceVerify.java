package commandHandling.commands.publicCommands.place;

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

        for (int i = 0; i < placeData.drawnPixels && i < placeData.pixels.size(); i++) {
            String command = placeData.pixels.get(i);
            String[] split = command.split(" ");
            int x = Integer.parseInt(split[2]);
            int y = Integer.parseInt(split[3]);
            Color colour = Color.decode(split[4]);
            Color placeC = new Color(place.getRGB(x, y));
            if (placeC.getAlpha() != 0 && !compareColors(colour, placeC)) {
                fixingQ.add(command);
            }
        }

        placeData.fixingQ = new LinkedList<>();
    }

    private boolean compareColors (Color img, Color place) {
        return img.getRed() == place.getRed() &&
               img.getGreen() == place.getGreen() &&
               img.getBlue() == place.getBlue();
    }
}
