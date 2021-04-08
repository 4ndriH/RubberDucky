package services;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import resources.EMOTES;

import java.awt.*;

public class BotExceptions {
    public static void invalidArgumentsException (CommandContext ctx) {
        EmbedBuilder embed = embed("invalidArgumentsException", "The give Arguments do not match " +
                "the required criterias", ctx);
        ctx.getMessage().reply(embed.build()).queue(msg -> msg.addReaction(EMOTES.NLD.getAsReaction()).queue());
    }

    public static void commandNotFoundException (CommandContext ctx, String command) {
        EmbedBuilder embed = embed("commandNotFoundException", "Command \"" + command + "\" not found!",
                null);
        ctx.getMessage().reply(embed.build()).queue(msg -> msg.addReaction(EMOTES.NLD.getAsReaction()).queue());
    }

    public static void missingAttachmentException (CommandContext ctx) {
        EmbedBuilder embed = embed("missingAttachmentException", "No attachment found!", null);
        ctx.getMessage().reply(embed.build()).queue(msg -> msg.addReaction(EMOTES.NLD.getAsReaction()).queue());
    }

    public static void fileDoesNotExistException (CommandContext ctx) {
        EmbedBuilder embed = embed("fileDoesNotExistException", "File does not exist!", null);
        ctx.getMessage().reply(embed.build()).queue(msg -> msg.addReaction(EMOTES.NLD.getAsReaction()).queue());
    }

    public static void missingPermissionException(CommandContext ctx) {
        EmbedBuilder embed = embed("missingPermissionException", "You do not have the required permissions " +
                "to run this command", null);
        ctx.getMessage().reply(embed.build()).queue(msg -> msg.addReaction(EMOTES.NLD.getAsReaction()).queue());
    }

    private static EmbedBuilder embed (String type, String message, CommandContext ctx) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle(type);
        embed.setDescription(message);
        embed.setColor(Color.RED);

        if (ctx != null) {
            String args = "";
            for (String s : ctx.getArguments())
                args += s + ", ";
            embed.addField("Arguments:", args, false);
        }

        return embed;
    }
}
