package services;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.entities.Message;

import java.util.concurrent.TimeUnit;

public class Miscellaneous {
    public static void deleteMsg(CommandContext ctx, Message msg, int seconds) {
        msg.delete().onErrorFlatMap(
                error -> ctx.getJDA().getGuildById("817850050013036605").getTextChannelById("847805084784132137")
                        .sendTyping()
        ).queueAfter(seconds, TimeUnit.SECONDS);
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
}
