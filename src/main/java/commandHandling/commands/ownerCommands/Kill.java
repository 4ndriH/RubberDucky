package commandHandling.commands.ownerCommands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.DatabaseHandler;
import services.logging.CommandLogger;
import services.logging.EmbedHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Kill implements CommandInterface{
    private static final Logger LOGGER = LoggerFactory.getLogger(Kill.class);
    private static CommandContext ctx;

    public Kill(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        CommandLogger.CommandLog(getName(), ctx, true);
        EmbedBuilder embed = EmbedHelper.embedBuilder("Are you Sure you want to kill this instance?");
        Kill.ctx = ctx;

        embed.setThumbnail(ctx.getSelfUser().getAvatarUrl());
        embed.setDescription(ctx.getSelfUser().getName());

        ctx.getChannel().sendMessageEmbeds(embed.build()).setActionRow(
                Button.danger("$KillAbort", "Abort"),
                Button.success("$KillProceed", "Proceed")
        ).queue(
                msg -> EmbedHelper.deleteMsg(msg, 64)
        );
    }

    public static void executeKill() {
        ArrayList<String> attachments = new ArrayList<>(Arrays.asList("sudoku.jpg", "shutdown.gif"));
        String file = attachments.get(new Random().nextInt(attachments.size()));
        LOGGER.info(ctx.getSelfUser().getName() + " is committing sudoku");
        EmbedBuilder embed = EmbedHelper.embedBuilder("Committing Sudoku").setImage("attachment://" + file);
        DatabaseHandler.pruneTableDeleteMsgs();

        Message msg = ctx.getChannel().sendMessageEmbeds(embed.build())
                .addFile(new File("resources/" + file)).complete();
        EmbedHelper.deleteMsg(msg, 64);
        ctx.getJDA().shutdownNow();
    }

    @Override
    public String getName() {
        return "Kill";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Shuts down the JDA instance and cancels all pending rest actions");
        return embed;
    }

    @Override
    public int getRestrictionLevel() {
        return 0;
    }
}
