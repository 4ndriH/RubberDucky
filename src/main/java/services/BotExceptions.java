package services;

import commandHandling.CommandContext;

public class BotExceptions {
    public static void invalidArgumentsException(CommandContext ctx) {
        ctx.getMessage().reply("Invalid Arguments!").queue();
    }

    public static void commandNotFoundException(CommandContext ctx, String command) {
        ctx.getMessage().reply("Command \"" + command + "\" not found!").queue();
    }

    public static void missingAttachmentException(CommandContext ctx) {
        ctx.getMessage().reply("No attachment found!").queue();
    }
}
