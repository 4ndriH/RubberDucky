package commandHandling.commands.CourseReview;

import assets.Objects.Review;
import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.DBHandlerCourseReviewVerify;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static services.database.DBHandlerCourse.getCourseName;
import static services.discordHelpers.EmbedHelper.embedBuilder;
import static services.discordHelpers.EmbedHelper.sendEmbed;

public class CourseReviewVerify implements CommandInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseReviewVerify.class);
    private static final AtomicBoolean alreadyVerifying = new AtomicBoolean(false);
    private static Map<Integer, Review> reviews;
    private static CommandContext ctx;

    public CourseReviewVerify(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        if (alreadyVerifying.compareAndSet(false, true)) {
            reviews = DBHandlerCourseReviewVerify.getUnverifiedReviews();
            CourseReviewVerify.ctx = ctx;
            sendEmbedCRV();
        } else {
            EmbedBuilder embed = embedBuilder("Someone is already reviewing reviews");
            embed.setDescription(CourseReviewVerify.ctx.getAuthor().getAsTag());
            sendEmbed(ctx, embed, 32);
        }
    }

    public static void castVerdict(int key, int status) {
        if (status != 0) {
            DBHandlerCourseReviewVerify.updateVerifiedStatus(reviews.get(key).uniqueUserId, reviews.get(key).courseNumber, status);
            LOGGER.info("Review from" + reviews.get(key).uniqueUserId + " for " + reviews.get(key).courseNumber + " has been " + (status == 1 ? "accepted" : "rejected") + "by " + ctx.getAuthor().getAsTag());
            reviews.remove(key);
            sendEmbedCRV();
        }
        alreadyVerifying.set(false);
    }

    private static void sendEmbedCRV() {
        if (!reviews.isEmpty()) {
            Review review  = reviews.entrySet().iterator().next().getValue();
            EmbedBuilder embed = embedBuilder(review.courseNumber + " - " + getCourseName(review.courseNumber));
            embed.setDescription(review.review);

            if (review.discordUserId != null) {
                embed.setFooter(ctx.getJDA().getUserById(review.discordUserId).getAsTag());
            } else {
                embed.setFooter("eth_id");
            }

            ctx.getChannel().sendMessageEmbeds(embed.build()).setActionRow(
                    Button.danger("cfvReject - " + review.key + " - " + ctx.getAuthor().getId(), "Reject"),
                    Button.primary("cfvQuit - " + review.key + " - " + ctx.getAuthor().getId(), "Quit"),
                    Button.success("cfvAccept - " + review.key + " - " + ctx.getAuthor().getId(), "Accept")
            ).queue();
        } else {
            sendEmbed(ctx, embedBuilder("Nothing to review"), 32);
            alreadyVerifying.set(false);
        }
    }

    @Override
    public String getName() {
        return "CourseReviewVerify";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Weed out course feedbacks which are pointless rants");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("crv");
    }

    @Override
    public int getRestrictionLevel() {
        return 0;
    }
}
