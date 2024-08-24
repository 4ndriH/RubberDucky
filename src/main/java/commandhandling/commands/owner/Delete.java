package commandhandling.commands.owner;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;
import java.util.Objects;

public class Delete implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        ctx.getChannel().retrieveMessageById(Objects.requireNonNull(ctx.getMessage().getReferencedMessage()).getId()).queue(
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

    @Override
    public int deleteAfter() {
        return 0;
    }
}
