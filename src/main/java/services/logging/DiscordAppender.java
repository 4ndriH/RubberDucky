package services.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import net.dv8tion.jda.api.EmbedBuilder;
import assets.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.helpers.MessageFormatter;

import java.awt.*;
import java.util.Objects;

public class DiscordAppender extends AppenderBase<ILoggingEvent> {
    private static TextChannel channel;

    public static void setJDA(JDA jda) {
        channel = Objects.requireNonNull(jda.getGuildById("817850050013036605")).getTextChannelById(Config.LOG_CHANNEL_ID);

        if (channel == null) {
            throw new IllegalArgumentException("Log channel not found");
        }
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        EmbedBuilder embed = new EmbedBuilder();
        String formattedMessage = MessageFormatter.arrayFormat(eventObject.getMessage(), eventObject.getArgumentArray()).getMessage();

        if (formattedMessage.contains("[ErrorResponseException] 503: N/A")) {
            formattedMessage = "AAAAAAAAAAAAAAAAAAAAAAAAa";
        }

        Color embedColor = switch (eventObject.getLevel().toString()) {
            case "INFO" -> new Color(0x42a2fc);
            case "WARN" -> new Color(0xff9100);
            case "ERROR" -> new Color(0xff0000);
            default -> new Color(0x000000);
        };

        embed.setColor(embedColor);
        embed.setTitle(eventObject.getLevel() + ": " + formattedMessage);

        if (eventObject.getThrowableProxy() != null) {
            StringBuilder sb = new StringBuilder().append("```");
            for (StackTraceElement s : eventObject.getCallerData()) {
                sb.append(s).append("\n");
            }
            sb.append("```");

            embed.setDescription(sb.length() > 4096 ? sb.substring(0, 4096) : sb.toString());
            embed.setFooter(eventObject.getThrowableProxy().getMessage());
        }

        channel.sendMessageEmbeds(embed.build()).queue();
    }
}