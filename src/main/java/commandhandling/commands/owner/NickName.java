package commandhandling.commands.owner;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;

public class NickName implements CommandInterface {
    private static final Pattern argumentPattern = Pattern.compile("^\\S+\\s?$");
    private static final Logger LOGGER = LoggerFactory.getLogger(NickName.class);

    @Override
    public void handle(CommandContext ctx) {
        StringBuilder sb = new StringBuilder();

        for (String s : ctx.getArguments()) {
            sb.append(s).append(" ");
        }

        ctx.getSelfMember().modifyNickname(sb.toString()).queue();
        LOGGER.info("Nickname changed to: " + sb);
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
    public boolean argumentCheck(StringBuilder args) {
        return argumentPattern.matcher(args).matches();
    }
}
