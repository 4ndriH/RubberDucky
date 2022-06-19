package assets.Objects;

import services.database.DBHandlerPlace;
import services.place.PlaceData;

import java.awt.*;

public class Pixel {
    int x, y;
    double alpha;
    String imageColor, placeColor;

    public Pixel(int x, int y, double alpha, String imageColor) {
        this.x = x;
        this.y = y;
        this.alpha = alpha;
        this.imageColor = imageColor;
    }

    public Pixel(int x, int y, double alpha, String imageColor, String placeColor) {
        this.x = x;
        this.y = y;
        this.alpha = alpha;
        this.imageColor = imageColor;
        this.placeColor = placeColor;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getPlaceColor() {
        return placeColor;
    }

    public String getImageColor() {
        return imageColor;
    }

    public double getAlpha() {
        return alpha;
    }

    @Override
    public String toString() {
        if (alpha == 1.0) {
            return x + " " + y + " " + imageColor;
        } else {
            return x + " " + y + " " + imageColor + " " + (int)(alpha * 255);
        }
    }

    public String getDrawCommand() {
        return ".place setpixel " + x + " " + y + " " + getColor();
    }

    public String getColor() {
        if (alpha == 1.0) {
            return placeColor = imageColor;
        } else {
            return placeColor = imageColor;//mixAndMatch();
        }
    }

    private String mixAndMatch() {
        Color image = Color.decode(imageColor);
        Color place = PlaceData.getPixelColor(x, y);

        int r = (int) (alpha * image.getRed() + (1 - alpha) * place.getRed());
        int g = (int) (alpha * image.getGreen() + (1 - alpha) * place.getGreen());
        int b = (int) (alpha * image.getBlue() + (1 - alpha) * place.getBlue());

        String color = String.format("#%02x%02x%02x", r, g, b);
        DBHandlerPlace.updatePixelPlaceColor(alpha, x, y, imageColor, color);
        return color;
    }
}
