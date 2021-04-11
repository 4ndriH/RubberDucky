package commandHandling.commands.place;

import java.awt.Color;
import java.util.LinkedList;

public class placeData {
    public int id, totalPixels, drawnPixels, progress;
    public boolean drawing, stopQ, stop;
    private Color[][] img = new Color[1000][1000];
    public LinkedList<String> fixingQ;

    public void reset () {
        id = totalPixels = drawnPixels = progress = 0;
        drawing = stop = stopQ = false;
        img = new Color[1000][1000];
        fixingQ = new LinkedList<>();
    }

    public void setPixel (String command) {
        int x = Integer.parseInt(command.substring(16, 19));
        int y = Integer.parseInt(command.substring(20, 23));
        Color c = Color.decode(command.substring(24,31));
        img[x][y] = c;
        progress = (int)(++drawnPixels * 100.0 / totalPixels);
    }

    public Color[][] getImg () {
        return img;
    }
}
