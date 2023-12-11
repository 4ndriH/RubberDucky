package services.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import net.dv8tion.jda.api.EmbedBuilder;
import assets.Config;
import net.dv8tion.jda.api.JDA;

import java.awt.*;

public class DiscordAppender extends AppenderBase<ILoggingEvent> {
    private static JDA jda;
    private static boolean startUpBlock = true;

    public static void setJDA(JDA jda) {
        DiscordAppender.jda = jda;
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        StringBuilder sb = new StringBuilder().append("```");
        EmbedBuilder embed = new EmbedBuilder();

        for (StackTraceElement s : eventObject.getCallerData()) {
            sb.append(s).append("\n");
        }

        sb.append("```");

        embed.setColor(eventObject.getLevel().toString().equals("WARN") ? new Color(0xff9100) : new Color(0xff0000));
        embed.setTitle(eventObject.getLevel() + ": " + eventObject.getMessage());

        if (sb.length() > 4096) {
            embed.setDescription(sb.substring(0, 4096));
        } else {
            embed.setDescription(sb);
        }

        embed.setFooter(eventObject.getThrowableProxy().getClassName());

        jda.getGuildById("817850050013036605").getTextChannelById(Config.logChannelID)
                .sendMessageEmbeds(embed.build()).queue();


        if (eventObject.getMessage().equals("Websocket Dead")) {
            if (!startUpBlock) {
                jda.openPrivateChannelById("153929916977643521").complete().sendMessage("Hi\nI tried accessing the websocket and as it turns out, it is (still) dead.\nCould you please fix it?").queue();
            } else {
                startUpBlock = false;
            }
        }
    }
}
