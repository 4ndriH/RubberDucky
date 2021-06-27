package commandHandling.commands.place;

import java.util.ArrayList;
import java.util.LinkedList;

public class PlaceData {
    public int id, totalPixels, drawnPixels, fixedPixels, progress;
    public boolean drawing, stop, stopQ = false, verify = true;
    public ArrayList<String> pixels;
    public LinkedList<String> fixingQ;

    public void reset () {
        id = totalPixels = drawnPixels = fixedPixels = progress = 0;
        drawing = stop = false;
        pixels = new ArrayList<>();
        fixingQ = new LinkedList<>();
    }

    public void updateProgress() {
        progress = drawnPixels * 100 / totalPixels;
    }
}
