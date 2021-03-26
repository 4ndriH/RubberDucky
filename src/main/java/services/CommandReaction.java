package services;

import net.dv8tion.jda.api.entities.MessageReaction;
import commandHandling.CommandContext;
import resources.EMOTES;
import java.util.*;

public class CommandReaction {
    public static void success (CommandContext ctx) {
        List<MessageReaction> reactions = ctx.getMessage().getReactions();
        for (MessageReaction mr : reactions) {
            if (mr.getReactionEmote().getAsReactionCode().equals(EMOTES.RDR.getAsReaction()))
                return;
        }
        ctx.getMessage().addReaction(EMOTES.RDG.getAsReaction()).queue();
    }

    public static void fail (CommandContext ctx) {
        ctx.getMessage().addReaction(EMOTES.RDR.getAsReaction()).queue();
    }
}
