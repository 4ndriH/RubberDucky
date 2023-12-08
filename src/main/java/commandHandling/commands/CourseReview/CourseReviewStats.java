package commandHandling.commands.CourseReview;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.discordHelpers.EmbedHelper;

import java.util.List;

import static services.database.DBHandlerCourseReviewStats.getPublishedReviews;
import static services.database.DBHandlerCourseReviewStats.getReviewedCourseCount;
import static services.discordHelpers.EmbedHelper.sendEmbed;
import static services.discordHelpers.MessageDeleteHelper.deleteMsg;

public class CourseReviewStats implements CommandInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseReviewStats.class);

    public CourseReviewStats(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        EmbedBuilder embed = EmbedHelper.embedBuilder("CourseReview Statistics");
        embed.setDescription("**" + getPublishedReviews() + "** reviews for **" + getReviewedCourseCount() + "** courses have been published");
        embed.setFooter("If you are curious about other stats let me know and maybe I add them");
        embed.setThumbnail("https://n.ethz.ch/~lteufelbe/coursereview/icon.png");

        sendEmbed(ctx, embed, 128);
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
