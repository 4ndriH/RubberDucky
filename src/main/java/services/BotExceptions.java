package services;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import resources.EMOTES;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class BotExceptions {
    public static void invalidArgumentsException (CommandContext ctx) {
        EmbedBuilder embed = embed("invalidArgumentsException", "The give Arguments do not match " +
                "the required criterias", ctx);
        ctx.getMessage().reply(embed.build()).queue(msg -> {
            msg.addReaction(EMOTES.NLD.getAsReaction()).queue();
            msg.delete().queueAfter(32, TimeUnit.SECONDS);
        });
    }

    public static void commandNotFoundException (CommandContext ctx, String command) {
        EmbedBuilder embed = embed("commandNotFoundException", "Command \"" + command + "\" not found!",
                null);
        ctx.getMessage().reply(embed.build()).queue(msg -> {
            msg.addReaction(EMOTES.NLD.getAsReaction()).queue();
            msg.delete().queueAfter(32, TimeUnit.SECONDS);
        });
    }

    public static void missingAttachmentException (CommandContext ctx) {
        EmbedBuilder embed = embed("missingAttachmentException", "No attachment found!", null);
        ctx.getMessage().reply(embed.build()).queue(msg -> {
            msg.addReaction(EMOTES.NLD.getAsReaction()).queue();
            msg.delete().queueAfter(32, TimeUnit.SECONDS);
        });
    }

    public static void fileDoesNotExistException (CommandContext ctx) {
        EmbedBuilder embed = embed("fileDoesNotExistException", "File does not exist!", null);
        ctx.getMessage().reply(embed.build()).queue(msg -> {
            msg.addReaction(EMOTES.NLD.getAsReaction()).queue();
            msg.delete().queueAfter(32, TimeUnit.SECONDS);
        });
    }

    public static void missingPermissionException(CommandContext ctx) {
        EmbedBuilder embed = embed("missingPermissionException", "You do not have the required permissions " +
                "to run this command!", null);
        ctx.getMessage().reply(embed.build()).queue(msg -> {
            msg.addReaction(EMOTES.NLD.getAsReaction()).queue();
            msg.delete().queueAfter(32, TimeUnit.SECONDS);
        });
    }

    public static void invalidIdException(CommandContext ctx) {
        EmbedBuilder embed = embed("invalidIdException", "This ID does not exist!", ctx);
        ctx.getMessage().reply(embed.build()).queue(msg -> {
            msg.addReaction(EMOTES.NLD.getAsReaction()).queue();
            msg.delete().queueAfter(32, TimeUnit.SECONDS);
        });
    }

    public static void emptyQueueException (CommandContext ctx) {
        EmbedBuilder embed = embed("emptyQueueException", "There are no files in the queue!", null);
        ctx.getMessage().reply(embed.build()).queue(msg -> {
            msg.addReaction(EMOTES.NLD.getAsReaction()).queue();
            msg.delete().queueAfter(32, TimeUnit.SECONDS);
        });
    }

    public static void fileTooBigException (CommandContext ctx) {
        EmbedBuilder embed = embed("fileTooBigException", "You can not have more than 10.8k lines!", null);
        ctx.getMessage().reply(embed.build()).queue(msg -> {
            msg.addReaction(EMOTES.NLD.getAsReaction()).queue();
            msg.delete().queueAfter(32, TimeUnit.SECONDS);
        });
    }

    private static EmbedBuilder embed (String type, String message, CommandContext ctx) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle(type);
        embed.setDescription(message);
        embed.setColor(Color.RED);

        if (ctx != null) {
            StringBuilder args = new StringBuilder();
            for (String s : ctx.getArguments())
                args.append(s).append(", ");
            embed.addField("Arguments:", args.toString(), false);
        }

        return embed;
    }
}
