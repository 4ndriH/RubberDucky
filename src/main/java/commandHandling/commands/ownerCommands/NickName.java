package commandHandling.commands.ownerCommands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.logging.CommandLogger;

import java.util.List;

public class NickName implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(NickName.class);

    public NickName(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        StringBuilder sb = new StringBuilder();

        for (String s : ctx.getArguments()) {
            sb.append(s).append(" ");
        }

        ctx.getSelfMember().modifyNickname(sb.toString()).queue();
        CommandLogger.CommandLog(getName(), ctx, true);
    }

    @Override
    public String getName() {
        return "Nickname";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Changes the bots nickname");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("nn");
    }

    @Override
    public int getRestrictionLevel() {
        return 0;
    }
}
