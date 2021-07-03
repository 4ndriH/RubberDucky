package services;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import resources.EMOTES;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DiscordLogger {
    public static void commandAndException(CommandContext ctx, String command, Throwable t, boolean pass) {
        command(ctx, command, pass);
        exception(ctx, t);
    }

    // Creates the embed for commands uses
    public static void command(CommandContext ctx, String command, boolean pass) {
        EmbedBuilder embed = new EmbedBuilder();
        StringBuilder sb = new StringBuilder();

        if (pass) {
            ctx.getMessage().addReaction(EMOTES.RDG.getAsReaction()).queue(
                    null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE)
            );
            embed.setColor(new Color(0x00d919));
        } else {
            ctx.getMessage().addReaction(EMOTES.RDR.getAsReaction()).queue(
                    null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE)
            );
            embed.setColor(new Color(0xff6a00));
        }

        if (!ctx.getArguments().isEmpty()) {
            for (String s : ctx.getArguments()) {
                sb.append(" " + s);
            }
        }

        embed.setDescription("<@!" + ctx.getAuthor().getId() + "> ran command *rd" + command + sb + "*");
        embed.setFooter("Server: " + ctx.getGuild().getName() + " [" + ctx.getGuild().getId() + "]\n" +
                        "Channel: " + ctx.getChannel().getName() + " [" + ctx.getChannel().getId() + "]\n" + time());

        send(embed, ctx.getJDA());
    }

    // Creates the embed for exceptions
    public static void exception(CommandContext ctx, Throwable t) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(0xFF0000));

        ctx.getMessage().addReaction(EMOTES.RDR.getAsReaction()).queue(
                null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE)
        );

        String s = throwableToString(t), exceptionType = s.split("\n")[0];
        embed.setTitle(exceptionType);

        for (int i = exceptionType.length(); i < s.length();) {
            int length = Math.min(1024, s.length() - i);

            embed.addField(" ", s.substring(i, i + length), false);

            i += length;
        }

        embed.setFooter("Server: " + ctx.getGuild().getName() + " [" + ctx.getGuild().getId() + "]\n" +
                        "Channel: " + ctx.getChannel().getName() + " [" + ctx.getChannel().getId() + "]\n" + time());

        send(embed, ctx.getJDA());
        notifyOnException(ctx);
    }

    private static void notifyOnException(CommandContext ctx) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("EXCEPTION");
        embed.setColor(new Color(0xFF0000));
        ctx.getJDA().openPrivateChannelById("155419933998579713").queue(
                channel -> channel.sendMessageEmbeds(embed.build()).queue()
        );
    }

    // Creates the embeds for bot status updates (idk yet what exactly this will be used for tbh)
    public static void botStatus(JDA jda, String t, String d) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(new Color(0x4B0082));
        embed.setTitle(t);
        embed.setDescription(d);
        embed.setFooter(time());

        send(embed, jda);
    }


    // Sends the embed to the bot-log channel
    private static void send(EmbedBuilder embed, JDA jda) {
        jda.getGuildById("817850050013036605").getTextChannelById("841393155478650920")
                .sendMessageEmbeds(embed.build()).queue();
    }

    // Returns current date and time
    private static String time() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        return new SimpleDateFormat("E dd.MM.yyyy HH:mm:ss").format(new Date());
    }

    // Creates a usable string out of the throwable
    private static String throwableToString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String error = sw.toString();
        pw.close();
        return error;
    }
}
