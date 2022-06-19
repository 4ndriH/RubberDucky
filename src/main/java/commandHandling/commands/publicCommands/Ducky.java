package commandHandling.commands.publicCommands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.discordHelpers.EmbedHelper;

import java.io.File;
import java.util.List;
import java.util.Random;

public class Ducky implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(Ducky.class);

    public Ducky(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        EmbedBuilder embed = EmbedHelper.embedBuilder("A RubberDucky").setImage("attachment://ducky.png");
        int nr = new Random().nextInt(new File("assets/duckies/").list().length);
        EmbedHelper.sendEmbedWithFile(ctx, embed, 32, "assets/duckies/ducky" + nr + ".png", "ducky.png");
    }

    @Override
    public String getName() {
        return "Ducky";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Sends a rubber ducky image. \n Why? idk, ask <@!223932775474921472> ¯\\_(ツ)_/¯");
        embed.addField("Suggestions", "If you have image suggestions send them to <@!155419933998579713>", false);
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("ucky");
    }
}
