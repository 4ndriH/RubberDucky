package commandhandling.commands.owner;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import assets.Config;

import java.util.regex.Pattern;

public class Prefix implements CommandInterface {
    private static final Pattern argumentPattern = Pattern.compile("^\\S+\\s?$");

    @Override
    public void handle(CommandContext ctx) {
        Config.updateConfig("prefix", ctx.getArguments().get(0));
    }

    @Override
    public String getName() {
        return "Prefix";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Changes the prefix");
        return embed;
    }

    @Override
    public int getRestrictionLevel() {
        return 0;
    }

    @Override
    public boolean argumentCheck(StringBuilder args) {
        return argumentPattern.matcher(args).matches();
    }
}
