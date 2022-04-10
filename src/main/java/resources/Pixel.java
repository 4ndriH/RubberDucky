package resources;

import services.place.PlaceData;

import java.awt.*;

public class Pixel {
    int x, y;
    double alpha;
    String img, place;

    public Pixel(int x, int y, double alpha, String img) {
        this.x = x;
        this.y = y;
        this.alpha = alpha;
        this.img = img;
    }

    public String getCommand() {
        if (alpha == 1.0) {
            return x + " " + y + " #" + img;
        } else {
            place = mixAndMatch();
            return x + " " + y + " #" + place;
        }
    }

    private String mixAndMatch() {
        Color image = Color.decode(img);
        Color place = PlaceData.getPixelColor(x, y);

        int r = (int) (alpha * image.getRed() + (1 - alpha) * place.getRed());
        int g = (int) (alpha * image.getGreen() + (1 - alpha) * place.getGreen());
        int b = (int) (alpha * image.getBlue() + (1 - alpha) * place.getBlue());

        return String.format("#%02x%02x%02x", r, g, b);
    }
}
