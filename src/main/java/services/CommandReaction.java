package services;

import commandHandling.CommandContext;
import resources.EMOTES;

public class CommandReaction {
    // Adds a green rubber ducky to the message
    public static void success (CommandContext ctx) {
        ctx.getMessage().addReaction(EMOTES.RDG.getAsReaction()).queue();
    }

    // adds a red rubber ducky to the message
    public static void fail (CommandContext ctx) {
        ctx.getMessage().addReaction(EMOTES.RDR.getAsReaction()).queue();
    }
}
