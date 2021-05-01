package commandHandling.commands.place;

import java.util.ArrayList;
import java.util.LinkedList;

public class placeData {
    public int id, totalPixels, drawnPixels, progress;
    public boolean drawing, stopQ, stop, verify;
    public ArrayList<String> pixels;
    public LinkedList<String> fixingQ;

    public void reset () {
        id = totalPixels = drawnPixels = progress = 0;
        drawing = stop = stopQ = verify = false;
        pixels = new ArrayList<>();
        fixingQ = new LinkedList<>();
    }

    public void updateProgress() {
        progress = drawnPixels * 100 / totalPixels;
    }
}
