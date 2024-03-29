package commandhandling.commands.owner;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

import static services.discordhelpers.MessageDeleteHelper.deleteMessage;

public class Delete implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(Delete.class);

    @Override
    public void handle(CommandContext ctx) {
        ctx.getChannel().retrieveMessageById(Objects.requireNonNull(ctx.getMessage().getReferencedMessage()).getId()).queue(
                message -> message.delete().queue()
        );
        LOGGER.debug("Deleted message: " + ctx.getMessage().getReferencedMessage().getContentRaw());
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

    @Override
    public int deleteAfter() {
        return 0;
    }
}
