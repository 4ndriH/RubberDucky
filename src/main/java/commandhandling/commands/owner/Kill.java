package commandhandling.commands.owner;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.ConnectionPool;
import services.database.ConnectionPoolCR;
import services.discordhelpers.EmbedHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static services.discordhelpers.MessageDeleteHelper.deleteMsg;

public class Kill implements CommandInterface{
    private static final Logger LOGGER = LoggerFactory.getLogger(Kill.class);
    private static CommandContext ctx;

    @Override
    public void handle(CommandContext ctx) {
        EmbedBuilder embed = EmbedHelper.embedBuilder("Are you Sure you want to kill this instance?");
        Kill.ctx = ctx;

        embed.setThumbnail(ctx.getSelfUser().getAvatarUrl());
        embed.setDescription(ctx.getSelfUser().getName());

        ctx.getChannel().sendMessageEmbeds(embed.build()).setActionRow(
                Button.danger("$KillAbort", "Abort"),
                Button.success("$KillProceed", "Proceed")
        ).queue(
                msg -> deleteMsg(msg, 64)
        );
    }

    public static void executeKill() {
        ArrayList<String> attachments = new ArrayList<>(Arrays.asList("sudoku.jpg", "shutdown.gif"));
        String file = attachments.get(new Random().nextInt(attachments.size()));
        LOGGER.info(ctx.getSelfUser().getName() + " is committing sudoku");
        EmbedBuilder embed = EmbedHelper.embedBuilder("Committing Sudoku").setImage("attachment://" + file);

        Message msg = ctx.getChannel().sendMessageEmbeds(embed.build())
                .addFiles(FileUpload.fromData(new File("resources/images/" + file))).complete();
        deleteMsg(msg, 64);
        ctx.getJDA().shutdownNow();
        ConnectionPool.closeDBConnection();
        ConnectionPoolCR.closeDBConnection();
        System.exit(0);
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
}
