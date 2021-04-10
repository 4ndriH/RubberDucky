package commandHandling.commands.place;

public class placeData {
    private int id, totalPixels, drawnPixels, progress;
    private boolean drawing, stopQ, stop;

    public boolean isDrawing () {
        return drawing;
    }

    public boolean stopQ () {
        return stopQ;
    }

    public boolean stop () {
        return stop;
    }

    public void setDrawing (boolean drawing) {
        this.drawing = drawing;
    }

    public void setStopQ (boolean stopQ) {
        this.stopQ = stopQ;
    }

    public void setStop (boolean stop) {
        this.stop = stop;
    }

    public int getID () {
        return id;
    }

    public int getTotalPixels () {
        return totalPixels;
    }

    public int getDrawnPixels () {
        return drawnPixels;
    }

    public int getProgress () {
        return progress;
    }

    public int setID (int id) {
        return this.id = id;
    }

    public void setTotalPixels (int totalPixels) {
        this.totalPixels = totalPixels;
    }

    public void setDrawnPixels (int drawnPixels) {
        this.drawnPixels = drawnPixels;
        progress = (int)(drawnPixels * 100.0 / totalPixels);
    }
}
