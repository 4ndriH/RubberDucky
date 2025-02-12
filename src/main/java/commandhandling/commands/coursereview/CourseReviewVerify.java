package commandhandling.commands.coursereview;

import assets.objects.Review;
import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.database.DBHandlerCourseReviewVerify;
import services.listeners.ButtonGameListener;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static services.database.DBHandlerCourse.getCourseName;
import static services.discordhelpers.EmbedHelper.embedBuilder;
import static services.discordhelpers.MessageSendHelper.sendMessage;

public class CourseReviewVerify implements CommandInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseReviewVerify.class);
    private static final AtomicBoolean alreadyVerifying = new AtomicBoolean(false);
    private static Map<Integer, Review> reviews;
    private static CommandContext ctx;

    @Override
    public void handle(CommandContext ctx) {
        if (alreadyVerifying.compareAndSet(false, true)) {
            reviews = DBHandlerCourseReviewVerify.getUnverifiedReviews();
            CourseReviewVerify.ctx = ctx;
            sendEmbedCRV();
        } else {
            EmbedBuilder embed = embedBuilder("Someone is already reviewing reviews");
            embed.setDescription(CourseReviewVerify.ctx.getAuthor().getAsTag());
            MessageCreateAction mca = ctx.getChannel().sendMessageEmbeds(embed.build());
            sendMessage(ctx, mca, 32);
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
            EmbedBuilder embed = embedBuilder(getCourseName(review.courseNumber));
            embed.setDescription(review.review);

            embed.setFooter(review.uniqueUserId);

            ctx.getChannel().sendMessageEmbeds(embed.build()).setActionRow(
                    Button.danger("cfvReject - " + review.key + " - " + ctx.getAuthor().getId(), "Reject"),
                    Button.primary("cfvQuit - " + review.key + " - " + ctx.getAuthor().getId(), "Quit"),
                    Button.success("cfvAccept - " + review.key + " - " + ctx.getAuthor().getId(), "Accept")
            ).queue();
        } else {
            Message msg = null;

            for (Message message : ctx.getChannel().getIterableHistory()) {
                if (message.getId() == ButtonGameListener.notificationMessageID) {
                    msg = message;
                    break;
                }
            }

            if (msg != null) {
                msg.addReaction(Emoji.fromFormatted("<a:CheckMark:919320274900500510>")).queue();
            }

            MessageCreateAction mca = ctx.getChannel().sendMessageEmbeds(embedBuilder("Nothing to review").build());
            sendMessage(ctx, mca, 32);
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