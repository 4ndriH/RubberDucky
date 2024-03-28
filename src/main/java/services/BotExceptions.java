package services;

import commandhandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import assets.Emotes;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import services.discordhelpers.MessageDeleteHelper;

import java.awt.*;

import static services.discordhelpers.ReactionHelper.addReaction;

public class BotExceptions {
    public static void invalidArgumentsException(CommandContext ctx) {
        sendMessage("invalidArgumentsException", "The given Arguments do not match " +
                "the required criteria", ctx, true);
    }

    public static void commandNotFoundException(CommandContext ctx, String command) {
        sendMessage("commandNotFoundException", "Command \"" + command + "\" not found!", ctx, false);
    }

    public static void missingAttachmentException(CommandContext ctx) {
        sendMessage("missingAttachmentException", "No attachment found!", ctx, false);
    }

    public static void fileDoesNotExistException(CommandContext ctx) {
        sendMessage("fileDoesNotExistException", "File does not exist!", ctx, true);
    }

    public static void invalidIdException(CommandContext ctx) {
        sendMessage("invalidIdException", "This ID does not exist!", ctx, false);
    }

    public static void emptyQueueException(CommandContext ctx) {
        sendMessage("emptyQueueException", "There are no files in the queue!", ctx, false);
    }

    public static void FileExceedsUploadLimitException(CommandContext ctx) {
        int boost = ctx.getGuild().getBoostCount();
        sendMessage("FileExceedsUploadLimitException","The file exceeds the possible " +
                (boost <= 1 ? 8 : boost == 2 ? 50 : 100) + "mb!", ctx, false);
    }

    public static void faultyPixelFormatException(CommandContext ctx, String s) {
        sendMessage("faultyPixelFormatException", "\"" + s + "\" is not a valid setpixel command!", ctx, false);
    }

    // Builds the embed and sends it as a response to a failed sub command
    private static void sendMessage(String type, String message, CommandContext ctx, boolean arguments) {
        addReaction(ctx, 5);
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle(type);
        embed.setDescription(message);
        embed.setColor(Color.RED);

        if (arguments && ctx.getArguments().size() > 1) {
            StringBuilder args = new StringBuilder();
            for (String s : ctx.getArguments())
                args.append(s).append(", ");
            embed.addField("Arguments:", args.substring(0, args.length() - 1), false);
        }

        ctx.getMessage().replyEmbeds(embed.build()).queue(msg -> {
            msg.addReaction(Emoji.fromFormatted(Emotes.NLD.getAsReaction())).queue();
            MessageDeleteHelper.deleteMessage(msg, 32);
        });
    }
}
