package services.listener;

import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;

public class BGListener extends ListenerAdapter {
    private static int buttonScore;
    private static int nextNotification = 0;
    private static int myCurrentScore = 1626;

    @Override
    public void onGuildMessageUpdate(@NotNull GuildMessageUpdateEvent event) {
        if (event.getAuthor().getId().equals("778731540359675904")) {
            if (event.getMessage().getContentRaw().contains("button")) {
                for (ActionRow r : event.getMessage().getActionRows()) {
                    for (Button b : r.getButtons()) {
                        buttonScore = Integer.parseInt(b.getLabel());
                    }
                }

                if (buttonScore > myCurrentScore) {
                    if (--nextNotification <= 0) {
                        event.getJDA().openPrivateChannelById("155419933998579713").complete().sendMessage("Button can be pressed [" + myCurrentScore + " -> " + buttonScore + "]\n" +
                                "https://discord.com/channels/747752542741725244/" + event.getChannel().getId() + "/" + event.getMessage().getId()).queue();
                        nextNotification = 10;
                    }
                }
            }
        }
    }
}
