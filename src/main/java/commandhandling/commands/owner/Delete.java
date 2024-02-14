package commandhandling.commands.owner;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Delete implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(Delete.class);

    @Override
    public void handle(CommandContext ctx) {
        String id = ctx.getArguments().size() == 1 ? ctx.getArguments().get(0) :
                ctx.getMessage().getReferencedMessage().getId();

        ctx.getChannel().retrieveMessageById(id).queue(
                message -> message.delete().queue()
        );
    }

    @Override
    public String getName() {
        return "Delete";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Deletes the referenced message or the message corresponding to the provided id");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("d");
    }
}
