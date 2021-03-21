package commandHandling.commands.place;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class draw {
    private final CommandContext ctx;

    public draw(CommandContext ctx) {
        this.ctx = ctx;
        drawing();
    }

    private void drawing() {
        Scanner scanner = null;

        try {
            scanner = new Scanner(new File("src/tempFiles/RDdraw.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        TextChannel ethPlaceBots = ctx.getGuild().getTextChannelById(819966095070330950L);

        while (scanner.hasNextLine()) {
            ethPlaceBots.sendMessage(scanner.nextLine()).queue();
        }

        File myObj = new File("src/tempFiles/RDdraw.txt");
        myObj.delete();
        }
}
