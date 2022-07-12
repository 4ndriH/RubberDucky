package services.listener;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CountThreadListener extends ListenerAdapter {
    private String lastDiscordUserId = "";
    private int lastCountedNumber = -1;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        ThreadChannel thread = event.getJDA().getGuildById("747752542741725244").getThreadChannelById("993390913881640970");

        for (Message message : thread.getHistory().retrievePast(64).complete()) {
            try {
                lastCountedNumber = Integer.parseInt(message.getContentRaw());
                lastDiscordUserId = message.getAuthor().getId();
                break;
            } catch (Exception e) {
                System.out.println("wups");
            }
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannel().getId().equals("993390913881640970")) {
            try {
                if (lastDiscordUserId.equals(event.getAuthor().getId()) ||
                        lastCountedNumber + 1 != Integer.parseInt(event.getMessage().getContentRaw())) {
                    event.getMessage().delete().queue();
                    return;
                }

                lastCountedNumber++;
                lastDiscordUserId = event.getAuthor().getId();
            } catch (Exception e) {
                event.getMessage().delete().queue();
            }
        }
    }
}
