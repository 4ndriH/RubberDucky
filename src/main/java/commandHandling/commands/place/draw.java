package commandHandling.commands.place;

import net.dv8tion.jda.api.entities.TextChannel;
import commandHandling.CommandContext;
import services.database.dbHandlerQ;

import java.util.*;
import java.io.*;

public class draw implements Runnable{
    public boolean stop = false, stopQ = false, drawing = true;
    private final CommandContext ctx;
    public int progress;

    public draw(CommandContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        TextChannel ethPlaceBots = ctx.getGuild().getTextChannelById(819966095070330950L);
        Random random = new Random();
        String file;
        int id;

        if (ctx.getArguments().size() > 1) {
            file = dbHandlerQ.getByID(id = Integer.parseInt(ctx.getArguments().get(1)));
            if (file.length() == 0) {
                ctx.getChannel().sendMessage("Invalid ID").queue();
                return;
            }
        } else {
            ArrayList<Integer> numbers = dbHandlerQ.getIDs();
            if (numbers.size() > 0) {
                file = dbHandlerQ.getByID(id = numbers.get(random.nextInt(numbers.size())));
            } else {
                ctx.getChannel().sendMessage("Queue is empty").queue();
                return;
            }
        }

        try {
            while (file != null && !stop && !stopQ) {
                Scanner scanner = new Scanner(new File("tempFiles/place/queue/" + file));
                ArrayList<String> pixels = new ArrayList<>();
                int start = dbHandlerQ.getProgress(id);
                progress = 0;

                while (scanner.hasNextLine()) {
                    pixels.add(scanner.nextLine());
                }
                scanner.close();

                for (int i = start; i < pixels.size() && !stop; i++) {
                    ethPlaceBots.sendMessage(pixels.get(i)).complete();
                    if (i % 16 == 0) {
                        progress = (int)(i * 100.0 / pixels.size());
                        dbHandlerQ.updateProgressInQ(i, id);
                    }
                }

                if (!stop) {
                    dbHandlerQ.deleteElementInQ(id);
                    File myObj = new File("tempFiles/place/queue/" + file);
                    myObj.delete();
                    ArrayList<Integer> numbers = dbHandlerQ.getIDs();
                    if (numbers.size() > 0) {
                        file = dbHandlerQ.getByID(id = numbers.get(random.nextInt(numbers.size())));
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
