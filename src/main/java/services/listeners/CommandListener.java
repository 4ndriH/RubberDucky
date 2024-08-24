package services.listeners;

import commandhandling.CommandContext;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import assets.Config;
import services.CommandManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class CommandListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandListener.class);
    private final CommandManager manager = new CommandManager();

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        LOGGER.info("{} is ready", event.getJDA().getSelfUser().getName());
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.isWebhookMessage()) {
            return;
        }

        if (event.getMessage().getContentRaw().startsWith(Config.prefix)) {
            String[] split = event.getMessage().getContentRaw().replaceFirst("(?i)" + Pattern.quote(Config.prefix), "").split("\\s+");
            ArrayList<String> arguments = new ArrayList<>(Arrays.asList(split).subList(1, split.length));

            String invoke = split[0].toLowerCase();

            CommandContext ctx = new CommandContext(event, arguments, event.getMessage().getAttachments());

            manager.handle(ctx, invoke);
        }
    }
}
