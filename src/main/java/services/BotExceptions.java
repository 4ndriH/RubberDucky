package services;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import resources.EMOTES;

import java.awt.*;

public class BotExceptions {
    public static void invalidArgumentsException (CommandContext ctx) {
        sendMessage("invalidArgumentsException", "The given Arguments do not match " +
                "the required criteria", ctx);
    }

    public static void commandNotFoundException (CommandContext ctx, String command) {
        sendMessage("commandNotFoundException", "Command \"" + command + "\" not found!", ctx);
    }

    public static void missingAttachmentException (CommandContext ctx) {
        sendMessage("missingAttachmentException", "No attachment found!", ctx);
    }

    public static void fileDoesNotExistException (CommandContext ctx) {
        sendMessage("fileDoesNotExistException", "File does not exist!", ctx);
    }

    public static void missingPermissionException(CommandContext ctx) {
        sendMessage("missingPermissionException",
                "You do not have the required permissions to run this command!", ctx);
    }

    public static void invalidIdException(CommandContext ctx) {
        sendMessage("invalidIdException", "This ID does not exist!", ctx);
    }

    public static void emptyQueueException (CommandContext ctx) {
        sendMessage("emptyQueueException", "There are no files in the queue!", ctx);
    }

    public static void fileTooBigException (CommandContext ctx) {
        sendMessage("fileTooBigException", "You can not have more than 10.8k lines!", ctx);
    }

    public static void FileExceedsUploadLimitException (CommandContext ctx) {
        int boost = ctx.getGuild().getBoostCount();
        sendMessage("FileExceedsUploadLimitException","The file exceeds the possible " +
                (boost <= 1 ? 8 : boost == 2 ? 50 : 100) + "mb!", ctx);
    }

    public static void exceedsCharLimitException (CommandContext ctx) {
        int boost = ctx.getGuild().getBoostCount();
        sendMessage("exceedsCharLimitException","The text exceeds the 2000 char limit!", ctx);
    }

    // Builds the embed and sends it as a response to a failed sub command
    private static void sendMessage(String type, String message, CommandContext ctx) {
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

        ctx.getMessage().replyEmbeds(embed.build()).queue(msg -> {
            msg.addReaction(EMOTES.NLD.getAsReaction()).queue();
            Miscellaneous.deleteMsg(ctx, msg, 32);
        });
    }
}
