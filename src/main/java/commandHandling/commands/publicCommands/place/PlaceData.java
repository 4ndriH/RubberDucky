package commandHandling.commands.publicCommands.place;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;

public class PlaceData {
    public int id, totalPixels, drawnPixels, fixedPixels, progress;
    public boolean drawing, stop, stopQ = false, verify = false;
    String user, file;
    public ArrayList<String> pixels;
    public LinkedList<String> fixingQ;
    public Logger LOGGER;

    public void reset () {
        id = totalPixels = drawnPixels = fixedPixels = progress = -1;
        user = "";
        file = "";
        drawing = stop = false;
        pixels = new ArrayList<>();
        fixingQ = new LinkedList<>();
    }

    public void updateProgress() {
        progress = drawnPixels * 100 / totalPixels;
    }
}
