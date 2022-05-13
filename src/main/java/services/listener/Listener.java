package services.listener;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.CONFIG;
import services.CommandManager;

public class Listener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private final CommandManager manager = new CommandManager();

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        LOGGER.info("{} is ready", event.getJDA().getSelfUser().getName());
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.isWebhookMessage()) {
            return;
        }

        if (event.getMessage().getContentRaw().startsWith(CONFIG.Prefix.get())) {
            manager.handle(event);
        }

        if (event.getChannel().getId().equals("819966095070330950") || event.getChannel().getId().equals("955751651942211604")) {
            event.getJDA().getGuildById("817850050013036605").getTextChannelById("969901898389925959")
                    .sendMessage(event.getAuthor().getName() + " [" + event.getChannel().getName() +"]: " + event.getMessage().getContentRaw()).queue();
        }
    }
}
