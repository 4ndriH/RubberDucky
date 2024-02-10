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
        Color embedColor = switch (eventObject.getLevel().toString()) {
            case "INFO" -> new Color(0x42a2fc);
            case "WARN" -> new Color(0xff9100);
            case "ERROR" -> new Color(0xff0000);
            default -> new Color(0x000000);
        };

        embed.setColor(embedColor);
        embed.setTitle(eventObject.getLevel() + ": " + eventObject.getMessage());

        if (!eventObject.getLevel().toString().equals("INFO")) {
            embed.setDescription(sb.length() > 4096 ? sb.toString().substring(0, 4096) : sb.toString());
            embed.setFooter(eventObject.getThrowableProxy().getClassName());
        }

        jda.getGuildById("817850050013036605").getTextChannelById(Config.logChannelID).sendMessageEmbeds(embed.build()).queue();
    }
}
