package services.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class CatchListener extends ListenerAdapter {
    MessageReactionAddEvent event = null;
    EmbedBuilder embed = new EmbedBuilder();
    EmbedBuilder goGetIt = new EmbedBuilder();

    public CatchListener() {
        embed.setColor(new Color(0xb074ad));
        embed.setTitle("The item has been stolen");
        goGetIt.setColor(new Color(0xb074ad));
        goGetIt.setTitle("T-5 seconds");
    }

    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        if (event.getAuthor().getId().equals("778731540359675904")) {
            Message message = event.getMessage();
            String content = message.getContentRaw();
            if (content.startsWith("Someone stole the item, the cooldown is 600.0 seconds.")) {
                embed.setDescription(this.event.getUser().getAsTag());

                event.getJDA().getGuildById("817850050013036605").getTextChannelById("841374763938873425")
                        .sendMessageEmbeds(embed.build()).queue();
                event.getJDA().getGuildById("817850050013036605").getTextChannelById("841374763938873425")
                        .sendMessageEmbeds(goGetIt.build()).queueAfter(115, TimeUnit.SECONDS);
            }
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        this.event = event;
    }
}
