package services.logging;

import ch.qos.logback.core.AppenderBase;
import net.dv8tion.jda.api.EmbedBuilder;
import assets.CONFIG;

import java.awt.*;

public class DiscordAppender extends AppenderBase {
    @Override
    protected void append(Object eventObject) {
        String[] split = eventObject.toString().split("]");
        EmbedBuilder embed = new EmbedBuilder();

        if (split[0].contains("WARN")) {
            embed.setColor(new Color(0xff9100));
        } else {
            embed.setColor(new Color(0xff0000));
        }

        embed.setTitle(split[0].substring(1));
        embed.setDescription(split[1]);

        CONFIG.instance.getGuildById(817850050013036605L).getTextChannelById(CONFIG.LogChannel.get())
                .sendMessageEmbeds(embed.build()).queue();
    }
}
