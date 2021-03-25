package services;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.entities.MessageReaction;

import java.util.*;

public class CommandReaction {
    public static void success (CommandContext ctx) {
        ctx.getMessage().addReaction(":RubberDuckyGreen:820700438084845568").queue();
    }

    public static void fail (CommandContext ctx) {
        ctx.getMessage().addReaction(":RubberDuckyRed:820700478467604502").queue();
    }
}
