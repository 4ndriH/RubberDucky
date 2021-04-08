package commandHandling.commands.place;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import commandHandling.CommandContext;
import services.database.dbHandlerQ;

import java.awt.*;
import java.util.*;
import java.io.*;

public class draw implements Runnable{
    public boolean stop = false, stopQ = false, drawing = true;
    private final CommandContext ctx;
    public int progress, total, id;

    public draw(CommandContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        TextChannel ethPlaceBots = ctx.getGuild().getTextChannelById(819966095070330950L);
        Random random = new Random();
        String file;

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

                while (scanner.hasNextLine()) {
                    pixels.add(scanner.nextLine());
                }
                scanner.close();

                total = pixels.size();
                progress = 0;

                for (int i = start; i < pixels.size() && !stop; i++) {
                    ethPlaceBots.sendMessage(pixels.get(i)).complete();
                    if (i % 16 == 0) {
                        progress = (int)(i * 100.0 / pixels.size());
                        dbHandlerQ.updateProgressInQ(i, id);
                    }
                }

                if (!stop) {
                    sendCompletionMessage();
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

    private void sendCompletionMessage () {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Your drawing has been finished");
        embed.setColor(new Color(0xb074ad));
        embed.setDescription("Thank you for using RubberDucky to draw");
        ctx.getMessage().getAuthor().openPrivateChannel().complete().sendMessage(embed.build()).queue();
    }
}
