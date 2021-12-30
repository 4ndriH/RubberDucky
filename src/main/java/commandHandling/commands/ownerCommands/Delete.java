package commandHandling.commands.ownerCommands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.Miscellaneous;

import java.util.List;

public class Delete implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(Delete.class);

    public Delete(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        String id = ctx.getArguments().size() == 1 ? ctx.getArguments().get(0) :
                ctx.getMessage().getReferencedMessage().getId();

        Miscellaneous.deleteMsg(ctx.getMessage(), 0);

        ctx.getChannel().retrieveMessageById(id).queue(
                message ->  {
                    message.delete().queue();
                    Miscellaneous.CommandLog(getName(), ctx, true);
                },
                failure -> Miscellaneous.CommandLog(getName(), ctx, false)
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