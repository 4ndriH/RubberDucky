package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import services.BotExceptions;

import java.awt.*;
import java.util.List;

public class Galactic implements CommandInterface {
    public Galactic(Logger LOGGER) {
        LOGGER.info("Loaded Command Galactic");
    }

    @Override
    public void handle(CommandContext ctx) {
        String message = "";
        boolean reply = true;

        try {
            message = ctx.getMessage().getReferencedMessage().getContentRaw() + " ";
        } catch (Exception e) {
            for (String s : ctx.getArguments()) {
                message += s + " ";
            }
            ctx.getMessage().delete().queue();
            reply = false;
        }

        if (isEnglish(message)) {
            message = message.replace("a", "ᔑ");
            message = message.replace("b", "ʖ");
            message = message.replace("c", "ᓵ");
            message = message.replace("d", "↸");
            message = message.replace("e", "ᒷ");
            message = message.replace("f", "⎓");
            message = message.replace("g", "⊣");
            message = message.replace("h", "⍑");
            message = message.replace("i", "╎");
            message = message.replace("j", "⋮");
            message = message.replace("k", "ꖌ");
            message = message.replace("l", "ꖎ");
            message = message.replace("m", "ᒲ");
            message = message.replace("n", "リ");
            message = message.replace("o", "\uD835\uDE79");
            message = message.replace("p", "!¡");
            message = message.replace("q", "ᑑ");
            message = message.replace("r", "∷");
            message = message.replace("s", "ᓭ");
            message = message.replace("t", "ℸ ̣ ");
            message = message.replace("u", "⚍");
            message = message.replace("v", "⍊");
            message = message.replace("w", "∴");
            message = message.replace("x", "̇/");
            message = message.replace("y", "\\||");
            message = message.replace("z", "⨅");
        } else {
            message = message.replace("ᔑ", "a");
            message = message.replace("ʖ", "b");
            message = message.replace("ᓵ", "c");
            message = message.replace("↸", "d");
            message = message.replace("ᒷ", "e");
            message = message.replace("⎓", "f");
            message = message.replace("⊣", "g");
            message = message.replace("⍑", "h");
            message = message.replace("╎", "i");
            message = message.replace("⋮", "j");
            message = message.replace("ꖌ", "k");
            message = message.replace("ꖎ", "l");
            message = message.replace("ᒲ", "m");
            message = message.replace("リ", "n");
            message = message.replace("\uD835\uDE79", "o");
            message = message.replace("!¡", "p");
            message = message.replace("ᑑ", "q");
            message = message.replace("∷", "r");
            message = message.replace("ᓭ", "s");
            message = message.replace("ℸ ̣ ", "t");
            message = message.replace("⚍", "u");
            message = message.replace("⍊", "v");
            message = message.replace("∴", "w");
            message = message.replace("̇/", "x");
            message = message.replace("\\||", "y");
            message = message.replace("||", "y");
            message = message.replace("⨅", "z");
        }

        if (message.length() > 2000) {
            BotExceptions.exceedsCharLimitException(ctx);
            return;
        }

        if (reply) {
            ctx.getMessage().getReferencedMessage().reply(message).queue();
        } else {
            ctx.getChannel().sendMessage(message).queue();
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

    @Override
    public String getName() {
        return "Galactic";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Help - Galactic");
        embed.setDescription("Translates from and to the villager enchanting table language by either" +
                "replying to a message or adding the text after `rdgalactic <text>`");
        embed.addField("Aliases", "```rdg```", false);
        embed.setColor(new Color(0xb074ad));

        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("g");
    }
}
