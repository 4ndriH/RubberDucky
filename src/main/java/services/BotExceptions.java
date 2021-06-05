package services;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import resources.EMOTES;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class BotExceptions {
    public static void invalidArgumentsException (CommandContext ctx) {
        embed("invalidArgumentsException", "The give Arguments do not match " +
                "the required criteria", ctx);
    }

    public static void commandNotFoundException (CommandContext ctx, String command) {
        embed("commandNotFoundException", "Command \"" + command + "\" not found!", ctx);
    }

    public static void missingAttachmentException (CommandContext ctx) {
        embed("missingAttachmentException", "No attachment found!", ctx);
    }

    public static void fileDoesNotExistException (CommandContext ctx) {
        embed("fileDoesNotExistException", "File does not exist!", ctx);
    }

    public static void missingPermissionException(CommandContext ctx) {
        embed("missingPermissionException",
                "You do not have the required permissions to run this command!", ctx);
    }

    public static void invalidIdException(CommandContext ctx) {
        embed("invalidIdException", "This ID does not exist!", ctx);
    }

    public static void emptyQueueException (CommandContext ctx) {
        embed("emptyQueueException", "There are no files in the queue!", ctx);
    }

    public static void fileTooBigException (CommandContext ctx) {
        embed("fileTooBigException", "You can not have more than 10.8k lines!", ctx);
    }

    public static void FileExceedsUploadLimitException (CommandContext ctx) {
        int boost = ctx.getGuild().getBoostCount();
        embed("FileExceedsUploadLimitException","The file exceeds the possible " +
                (boost <= 1 ? 8 : boost == 2 ? 50 : 100) + "mb!", ctx);
    }

    public static void exceedsCharLimitException (CommandContext ctx) {
        int boost = ctx.getGuild().getBoostCount();
        embed("exceedsCharLimitException","The text exceeds the 2000 char limit!", ctx);
    }

    // Builds the embed and sends it as a response to a failed sub command
    private static void embed (String type, String message, CommandContext ctx) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle(type);
        embed.setDescription(message);
        embed.setColor(Color.RED);

        if (ctx.getArguments().size() > 1) {
            StringBuilder args = new StringBuilder();
            for (String s : ctx.getArguments())
                args.append(s).append(", ");
            embed.addField("Arguments:", args.toString(), false);
        }

        ctx.getMessage().reply(embed.build()).queue(msg -> {
            msg.addReaction(EMOTES.NLD.getAsReaction()).queue();
            msg.delete().queueAfter(32, TimeUnit.SECONDS);
        });
    }
}
