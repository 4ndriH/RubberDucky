package commandHandling;

import me.duncte123.botcommons.commands.ICommandContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.List;

public class CommandContext implements ICommandContext {
    private final GuildMessageReceivedEvent event;
    private final List<String> arguments;

    public CommandContext(GuildMessageReceivedEvent event, List<String> arguments) {
        this.event = event;
        this.arguments = arguments;
    }

    @Override
    public Guild getGuild() {
        return this.getEvent().getGuild();
    }

    @Override
    public GuildMessageReceivedEvent getEvent() {
        return event;
    }

    public List<String> getArguments() {
        return arguments;
    }
}
