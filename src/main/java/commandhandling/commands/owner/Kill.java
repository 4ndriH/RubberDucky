package commandhandling.commands.owner;

import assets.Config;
import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import org.hibernate.Hibernate;
import services.database.ConnectionPoolCR;
import services.database.HibernateUtil;
import services.discordhelpers.EmbedHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static services.discordhelpers.MessageSendHelper.sendMessage;
import static services.discordhelpers.MessageSendHelper.sendMessageComplete;

public class Kill implements CommandInterface{
    private static CommandContext ctx;

    @Override
    public void handle(CommandContext ctx) {
        EmbedBuilder embed = EmbedHelper.embedBuilder("Are you Sure you want to kill this instance?");
        Kill.ctx = ctx;

        embed.setThumbnail(ctx.getSelfUser().getAvatarUrl());
        embed.setDescription(ctx.getSelfUser().getName());

        MessageCreateAction mca = ctx.getChannel().sendMessageEmbeds(embed.build()).setActionRow(
                Button.danger("$KillAbort", "Abort"),
                Button.success("$KillProceed", "Proceed")
        );

        sendMessage(ctx, mca, 64);
    }

    public static void executeKill() {
        ArrayList<String> attachments = new ArrayList<>(Arrays.asList("sudoku.jpg", "shutdown.gif"));
        String file = attachments.get(new Random().nextInt(attachments.size()));
        EmbedBuilder embed = EmbedHelper.embedBuilder("Committing Sudoku").setImage("attachment://" + file);

        MessageCreateAction mca = ctx.getChannel().sendMessageEmbeds(embed.build()).addFiles(FileUpload.fromData(new File(Config.DIRECTORY_PATH + "resources/images/" + file)));
        sendMessageComplete(mca, 64);

        ctx.getJDA().shutdownNow();
        HibernateUtil.closeSessionFactory();
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
