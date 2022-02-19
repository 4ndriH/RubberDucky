package commandHandling.commands.publicCommands.place;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.BotExceptions;
import services.CommandManager;
import services.database.DatabaseHandler;
import services.logging.EmbedHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlaceQueue implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(PlaceQueue.class);

    public PlaceQueue(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        ArrayList<Integer> ids = DatabaseHandler.getPlaceProjectIDs();
        Random random = new Random();
        int id = ctx.getArguments().size() == 1 && ctx.getSecurityClearance() == 0 ?
                Integer.parseInt(ctx.getArguments().get(0)) : random.nextInt(10000);

        while (ids.contains(id)) {
            id = random.nextInt(10000);
        }

        List<Message.Attachment> files = new ArrayList<>();
        files.addAll(ctx.getMessage().getAttachments());
        if (ctx.getMessage().getReferencedMessage() != null) {
            files.addAll(ctx.getMessage().getReferencedMessage().getAttachments());
        }

        if (files.isEmpty()) {
            BotExceptions.missingAttachmentException(ctx);
            CommandManager.commandLogger(getName(), ctx, false);
            return;
        }

        CommandManager.commandLogger(getName(), ctx, true);
        DatabaseHandler.addFileToQueue(id, ctx.getAuthor().getId());
        EmbedBuilder embed = EmbedHelper.embedBuilder("Queue");
        embed.setDescription("Your file got ID " + id);

        files.get(0).downloadToFile(new File("tempFiles/place/queue/" + "RDdraw" + id + ".txt"));

        EmbedHelper.sendEmbed(ctx, embed, 32);
    }

    @Override
    public String getName() {
        return "PlaceQueue";
    }

    @Override
    public EmbedBuilder getHelp() {
        return null;
    }

    @Override
    public List<String> getAliases() {
        return List.of("placeQ", "pQ");
    }

    @Override
    public boolean requiresFurtherChecks() {
        return true;
    }
}
