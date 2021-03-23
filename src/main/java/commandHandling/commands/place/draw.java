package commandHandling.commands.place;

import net.dv8tion.jda.api.entities.TextChannel;
import commandHandling.CommandContext;
import services.dbHandler;

import java.util.*;
import java.io.*;

public class draw {
    public boolean draw = true, stopQ = false, drawing = true;
    private final CommandContext ctx;
    public double progress;

    public draw(CommandContext ctx) {
        this.ctx = ctx;
        drawing();
    }

    private void drawing() {
        TextChannel ethPlaceBots = ctx.getGuild().getTextChannelById(819966095070330950L);
        Random random = new Random();
        String file;
        int id;

        if (ctx.getArguments().size() > 1) {
            file = dbHandler.getByID(id = Integer.parseInt(ctx.getArguments().get(1)));
            if (file.length() == 0) {
                ctx.getChannel().sendMessage("Invalid ID").queue();
                return;
            }
        } else {
            ArrayList<Integer> numbers = dbHandler.getIDs();
            if (numbers.size() > 0) {
                file = dbHandler.getByID(id = numbers.get(random.nextInt(numbers.size())));
            } else {
                ctx.getChannel().sendMessage("Queue is empty").queue();
                return;
            }
        }

        try {
            while (file != null && draw && !stopQ) {
                Scanner scanner = new Scanner(new File("tempFiles/" + file));
                ArrayList<String> pixels = new ArrayList<>();
                int start = dbHandler.getProgress(id);
                progress = 0.0;

                while (scanner.hasNextLine()) {
                    pixels.add(scanner.nextLine());
                }

                for (int i = start; i < pixels.size() && draw; i++) {
                    ethPlaceBots.sendMessage(pixels.get(i)).queue();
                    if (i % 64 == 0) {
                        progress = (double)i / pixels.size();
                        dbHandler.updateProgressInQ(i, id);
                    }
                }

                if (draw) {
                    dbHandler.deleteElementInQ(id);
                    File myObj = new File("tempFiles/place/queue/" + file);
                    myObj.delete();
                    ArrayList<Integer> numbers = dbHandler.getIDs();
                    if (numbers.size() > 0) {
                        file = dbHandler.getByID(id = numbers.get(random.nextInt(numbers.size())));
                    } else {
                        break;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        drawing = false;
    }
}
