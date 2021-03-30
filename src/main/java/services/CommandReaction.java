package services;

import commandHandling.CommandContext;
import resources.EMOTES;

public class CommandReaction {
    public static void success (CommandContext ctx) {
        ctx.getMessage().addReaction(EMOTES.RDG.getAsReaction()).queue();
    }

    public static void fail (CommandContext ctx) {
        ctx.getMessage().addReaction(EMOTES.RDR.getAsReaction()).queue();
    }
}
