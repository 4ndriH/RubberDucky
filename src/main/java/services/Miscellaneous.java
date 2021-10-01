package services;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.CONFIG;
import resources.EMOTES;

import java.util.concurrent.TimeUnit;

public class Miscellaneous {
    private static final Logger cmdLogger = LoggerFactory.getLogger("Command Logger");

    public static void deleteMsg(Message msg, int seconds) {
        msg.delete().queueAfter(seconds, TimeUnit.SECONDS, null, throwable -> {});
    }

    public static String timeFormat(int linesCnt) {
        int seconds = linesCnt % 60;
        int minutes = (linesCnt - seconds) / 60 % 60;
        int hours = ((linesCnt - seconds) / 60 - minutes) / 60;

        String days = "";
        if (hours > 23) {
            days = (hours - (hours %= 24)) / 24 + "";
            if (Integer.parseInt(days) == 1) {
                days += " day, ";
            } else {
                days += " days, ";
            }
        }
        return String.format(days + "%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static void CommandLog(String name, CommandContext ctx, boolean success) {
        if (success) {
            ctx.getMessage().addReaction(EMOTES.RDG.getAsReaction()).queue(
                    null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE)
            );
        } else {
            ctx.getMessage().addReaction(EMOTES.RDR.getAsReaction()).queue(
                    null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE)
            );
        }

        cmdLogger.info(ctx.getAuthor().getName() + " ran command " + CONFIG.Prefix.get() + name.toLowerCase() +
                (ctx.getArguments().size() != 0 ? " " + ctx.getArguments().toString() : "") +
                (success ? " successfully" : " unsuccessfully"));
    }
}
