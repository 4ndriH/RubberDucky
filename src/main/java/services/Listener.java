package services;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class Listener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private final CommandManager manager = new CommandManager();

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        LOGGER.info("{} is ready", event.getJDA().getSelfUser().getAsTag());
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        User user = event.getAuthor();

        if (user.isBot() || event.isWebhookMessage()) {
            return;
        }

        String prefix = config.get("PREFIX");
        String raw = event.getMessage().getContentRaw();
        if (raw.equals(prefix + "shutdown") && user.getId().equals(config.get("OWNER_ID"))) {
            LOGGER.info("Shutting down");
            event.getChannel().sendMessage("Shutting down").addFile(new File("src/main/resources/shutdown.gif"))
                    .queue();
            event.getJDA().shutdown();
            return;
        }

        if (raw.startsWith(prefix)) {
            manager.handle(event);
        }
    }
}
