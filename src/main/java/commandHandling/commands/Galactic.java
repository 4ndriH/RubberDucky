package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import services.BotExceptions;
import services.DiscordLogger;

import java.util.List;

public class Galactic implements CommandInterface {
    public Galactic(Logger LOGGER) {
        LOGGER.info("Loaded Command Galactic");
    }

    @Override
    public void handle(CommandContext ctx) {
        StringBuilder message = new StringBuilder();
        boolean reply = true;

        try {
            message = new StringBuilder(ctx.getMessage().getReferencedMessage().getContentRaw() + " ");
        } catch (Exception e) {
            for (String s : ctx.getArguments()) {
                message.append(s).append(" ");
            }
            ctx.getMessage().delete().queue();
            reply = false;
        }

        if (!inputNotEmpty(message.toString())) {
            BotExceptions.invalidArgumentsException(ctx);
            DiscordLogger.command(ctx, "galactic", false);
            return;
        }

        DiscordLogger.command(ctx, "galactic", true);

        if (isEnglish(message.toString())) {
            message = new StringBuilder(message.toString().replace("a", "ᔑ"));
            message = new StringBuilder(message.toString().replace("b", "ʖ"));
            message = new StringBuilder(message.toString().replace("c", "ᓵ"));
            message = new StringBuilder(message.toString().replace("d", "↸"));
            message = new StringBuilder(message.toString().replace("e", "ᒷ"));
            message = new StringBuilder(message.toString().replace("f", "⎓"));
            message = new StringBuilder(message.toString().replace("g", "⊣"));
            message = new StringBuilder(message.toString().replace("h", "⍑"));
            message = new StringBuilder(message.toString().replace("i", "╎"));
            message = new StringBuilder(message.toString().replace("j", "⋮"));
            message = new StringBuilder(message.toString().replace("k", "ꖌ"));
            message = new StringBuilder(message.toString().replace("l", "ꖎ"));
            message = new StringBuilder(message.toString().replace("m", "ᒲ"));
            message = new StringBuilder(message.toString().replace("n", "リ"));
            message = new StringBuilder(message.toString().replace("o", "\uD835\uDE79"));
            message = new StringBuilder(message.toString().replace("p", "!¡"));
            message = new StringBuilder(message.toString().replace("q", "ᑑ"));
            message = new StringBuilder(message.toString().replace("r", "∷"));
            message = new StringBuilder(message.toString().replace("s", "ᓭ"));
            message = new StringBuilder(message.toString().replace("t", "ℸ ̣ "));
            message = new StringBuilder(message.toString().replace("u", "⚍"));
            message = new StringBuilder(message.toString().replace("v", "⍊"));
            message = new StringBuilder(message.toString().replace("w", "∴"));
            message = new StringBuilder(message.toString().replace("x", "̇/"));
            message = new StringBuilder(message.toString().replace("y", "\\||"));
            message = new StringBuilder(message.toString().replace("z", "⨅"));
        } else {
            message = new StringBuilder(message.toString().replace("ᔑ", "a"));
            message = new StringBuilder(message.toString().replace("ʖ", "b"));
            message = new StringBuilder(message.toString().replace("ᓵ", "c"));
            message = new StringBuilder(message.toString().replace("↸", "d"));
            message = new StringBuilder(message.toString().replace("ᒷ", "e"));
            message = new StringBuilder(message.toString().replace("⎓", "f"));
            message = new StringBuilder(message.toString().replace("⊣", "g"));
            message = new StringBuilder(message.toString().replace("⍑", "h"));
            message = new StringBuilder(message.toString().replace("╎", "i"));
            message = new StringBuilder(message.toString().replace("⋮", "j"));
            message = new StringBuilder(message.toString().replace("ꖌ", "k"));
            message = new StringBuilder(message.toString().replace("ꖎ", "l"));
            message = new StringBuilder(message.toString().replace("ᒲ", "m"));
            message = new StringBuilder(message.toString().replace("リ", "n"));
            message = new StringBuilder(message.toString().replace("\uD835\uDE79", "o"));
            message = new StringBuilder(message.toString().replace("!¡", "p"));
            message = new StringBuilder(message.toString().replace("ᑑ", "q"));
            message = new StringBuilder(message.toString().replace("∷", "r"));
            message = new StringBuilder(message.toString().replace("ᓭ", "s"));
            message = new StringBuilder(message.toString().replace("ℸ ̣ ", "t"));
            message = new StringBuilder(message.toString().replace("⚍", "u"));
            message = new StringBuilder(message.toString().replace("⍊", "v"));
            message = new StringBuilder(message.toString().replace("∴", "w"));
            message = new StringBuilder(message.toString().replace("̇/", "x"));
            message = new StringBuilder(message.toString().replace("\\||", "y"));
            message = new StringBuilder(message.toString().replace("||", "y"));
            message = new StringBuilder(message.toString().replace("⨅", "z"));
        }

        if (message.length() > 2000) {
            BotExceptions.exceedsCharLimitException(ctx);
            return;
        }

        if (reply) {
            ctx.getMessage().getReferencedMessage().reply(message.toString()).queue();
        } else {
            ctx.getChannel().sendMessage(message.toString()).queue();
        }
    }

    private boolean isEnglish(String s) {
        return s.contains("a") || s.contains("b") || s.contains("c") || s.contains("d") || s.contains("e") ||
                s.contains("f") || s.contains("g") || s.contains("h") || s.contains("i") || s.contains("j") ||
                s.contains("k") || s.contains("l") || s.contains("m") || s.contains("n") || s.contains("o") ||
                s.contains("p") || s.contains("q") || s.contains("r") || s.contains("s") || s.contains("t") ||
                s.contains("u") || s.contains("v") || s.contains("w") || s.contains("x") || s.contains("y") ||
                s.contains("z");
    }

    private boolean inputNotEmpty(String s) {
        return s.replace(" ", "").length() > 0;
    }

    @Override
    public String getName() {
        return "Galactic";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Translates from and to the villager enchanting table language by either" +
                "replying to a message or adding the text after `rdgalactic <text>`");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("g");
    }
}
