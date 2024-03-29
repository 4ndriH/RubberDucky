package commandhandling.commands.coursereview;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import services.discordhelpers.EmbedHelper;

import java.util.List;

import static services.database.DBHandlerCourseReviewStats.getPublishedReviews;
import static services.database.DBHandlerCourseReviewStats.getReviewedCourseCount;
import static services.discordhelpers.MessageSendHelper.sendMessage;

public class CourseReviewStats implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        EmbedBuilder embed = EmbedHelper.embedBuilder("CourseReview Statistics");
        embed.setDescription("**" + getPublishedReviews() + "** reviews for **" + getReviewedCourseCount() + "** courses have been published");
        embed.setFooter("If you are curious about other stats let me know and maybe I add them");
        embed.setThumbnail("https://n.ethz.ch/~lteufelbe/coursereview/icon.png");

        MessageCreateAction mca = ctx.getChannel().sendMessageEmbeds(embed.build());
        sendMessage(ctx, mca, 128);
    }

    @Override
    public String getName() {
        return "CourseReviewStats";
    }

    @Override
    public EmbedBuilder getHelp() {
        return null;
    }

    @Override
    public List<String> getAliases() {
        return List.of("crs", "stats");
    }
}
