package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;

import java.util.List;

public class Delete implements CommandInterface {
    public Delete(Logger LOGGER) {
        LOGGER.info("Loaded Command Delete");
    }

    @Override
    public void handle(CommandContext ctx) {
        String id = ctx.getArguments().size() == 1 ? ctx.getArguments().get(0) :
                ctx.getMessage().getReferencedMessage().getId();

        ctx.getChannel().retrieveMessageById(id).queue(
                message ->  {
                    message.delete().queue();
                    services.Logger.command(ctx, "delete", true);
                },
                failure -> services.Logger.command(ctx, "delete", false)
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
    public boolean isOwnerOnly() {
        return true;
    }
}
