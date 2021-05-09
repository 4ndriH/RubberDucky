package commandHandling.commands.place;

import services.PlaceWebSocket;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Random;

public class verify {
    private final placeData placeData;

    public verify(placeData placeData) {
        this.placeData = placeData;
        verifier();
    }

    private void verifier() {
        BufferedImage place = PlaceWebSocket.getImage(true);
        LinkedList<String> fixingQ = new LinkedList<>();

        if (!blackWebSocketImage(place)) {
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
    }

    private boolean blackWebSocketImage (BufferedImage img) {
        Random random = new Random();
        int blackCount = 0, iterations = 1000;

        for (int i = 0; i < iterations; i++) {
            Color c = new Color(img.getRGB(random.nextInt(1000), random.nextInt(1000)));
            if (c.getRed() <= 0 && c.getGreen() <= 0 && c.getBlue() <= 0) {
                blackCount++;
            }
        }

        return blackCount == iterations;
    }

    private boolean compareColors (Color img, Color place) {
        return img.getRed() == place.getRed() &&
               img.getGreen() == place.getGreen() &&
               img.getBlue() == place.getBlue();
    }
}
