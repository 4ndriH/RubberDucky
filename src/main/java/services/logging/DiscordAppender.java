package services.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import net.dv8tion.jda.api.EmbedBuilder;
import assets.CONFIG;

import java.awt.*;

public class DiscordAppender extends AppenderBase<ILoggingEvent> {
    @Override
    protected void append(ILoggingEvent eventObject) {
        StringBuilder sb = new StringBuilder().append("```");
        EmbedBuilder embed = new EmbedBuilder();

        for (StackTraceElement s : eventObject.getCallerData()) {
            sb.append(s).append("\n");
        }

        sb.append("```");

        embed.setColor(eventObject.getLevel().toString().equals("WARN") ? new Color(0xff9100) : new Color(0xff0000));
        embed.setTitle(eventObject.getLevel() + ": " + eventObject.getThrowableProxy().getClassName());

        if (sb.length() > 4096) {
            embed.setDescription(sb.substring(0, 4096));
        } else {
            embed.setDescription(sb);
        }

        embed.setFooter(eventObject.getLoggerName());

        CONFIG.instance.getGuildById("817850050013036605").getTextChannelById(CONFIG.logChannelID)
                .sendMessageEmbeds(embed.build()).queue();
    }
}
