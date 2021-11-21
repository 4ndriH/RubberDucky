package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.DatabaseHandler;
import services.Miscellaneous;

import java.awt.*;
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
        Miscellaneous.CommandLog(getName(), ctx, true);
        EmbedBuilder embed = new EmbedBuilder();
        Kill.ctx = ctx;

        embed.setThumbnail(ctx.getSelfUser().getAvatarUrl());
        embed.setTitle("Are you Sure you want to kill this instance?");
        embed.setDescription(ctx.getSelfUser().getName());
        embed.setColor(new Color(0xb074ad));

        ctx.getChannel().sendMessageEmbeds(embed.build()).setActionRow(
                Button.danger("$KillAbort", "Abort"),
                Button.success("$KillProceed", "Proceed")
        ).queue(
                msg -> Miscellaneous.deleteMsg(msg, 64)
        );
    }

    public static void executeKill() {
        ArrayList<String> attachments = new ArrayList<>(Arrays.asList("sudoku.jpg", "shutdown.gif"));
        String file = attachments.get(new Random().nextInt(attachments.size()));
        LOGGER.info(ctx.getSelfUser().getName() + " is committing sudoku");
        EmbedBuilder embed = new EmbedBuilder();
        DatabaseHandler.pruneTableDeleteMsgs();

        embed.setTitle("Committing Sudoku");
        embed.setColor(new Color(0xb074ad));
        embed.setImage("attachment://" + file);

        Message msg = ctx.getChannel().sendMessageEmbeds(embed.build())
                .addFile(new File("resources/" + file)).complete();
        Miscellaneous.deleteMsg(msg, 64);
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
    public boolean isOwnerOnly() {
        return true;
    }
}
