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

    public static void fileDoesNotExistException(CommandContext ctx) {
        ctx.getMessage().reply("File does not exist!").queue();
    }

    public static void permissionException(CommandContext ctx) {
        ctx.getMessage().reply("You do not have the required permissions to run this command").queue();
    }
}
