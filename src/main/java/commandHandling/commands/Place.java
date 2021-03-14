package commandHandling.commands;

import commandHandling.commands.place.*;
import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import org.slf4j.Logger;

public class Place implements CommandInterface {
    public Place(Logger LOGGER) {
        LOGGER.info("Loaded Command Place");
    }

    @Override
    public void handle(CommandContext ctx) {
        switch (ctx.getArguments().get(0)) {
            case "encode":
                if (ctx.getArguments().size() != 5) invalidArgumentsException(ctx);
                 else new encode(ctx);
                break;
            case "preview":
                new preview(ctx);
                break;
            case "draw":

                break;
            case "spread":

                break;
            case "decode":

                break;
            case "random":

                break;
            case "profilepicture":

                break;
        }
    }

    private void invalidArgumentsException(CommandContext ctx) {
        ctx.getMessage().reply("Invalid Arguments!").queue();
    }

    @Override
    public String getName() {
        return "place";
    }

    @Override
    public String getHelp() {
        return null;
    }
}
