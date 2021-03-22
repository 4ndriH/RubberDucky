package commandHandling.commands.place;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.entities.TextChannel;
import services.StorageHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class draw {
    private final CommandContext ctx;
    public boolean draw = true, stopQ = false, drawing = true;
    public double progress;

    public draw(CommandContext ctx) {
        this.ctx = ctx;
        drawing();
    }

    private void drawing() {
        String file = StorageHandler.readLine("resources/place/", "queue", 0);
        TextChannel ethPlaceBots = ctx.getGuild().getTextChannelById(819966095070330950L);

        try {
            while (file != null && draw && !stopQ) {
                Scanner scanner = new Scanner(new File("tempFiles/" + file));
                ArrayList<String> pixels = new ArrayList<>();
                int start = getProgress();
                progress = 0.0;

                while (scanner.hasNextLine()) {
                    pixels.add(scanner.nextLine());
                }

                for (int i = start; i < pixels.size() && draw; i++) {
                    ethPlaceBots.sendMessage(pixels.get(i)).queue();
                    if (i % 60 == 0) {
                        progress = (double)i / pixels.size();
                        StorageHandler.replaceLine("resources/place/", "progress", "" + i, 0);
                    }
                }
                if (draw) {
                    StorageHandler.deleteLine("resources/place/", "queue", 0);
                    file = StorageHandler.readLine("resources/place/", "queue", 0);
                    StorageHandler.replaceLine("resources/place/", "progress", "0", 0);
                    File myObj = new File("tempFiles/place/queue/" + file);
                    myObj.delete();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        drawing = false;
    }

    private int getProgress() {
        String line = StorageHandler.readLine("resources/place/", "progress", 0);
        return line != null ? Integer.parseInt(line) : 0;
    }
}
