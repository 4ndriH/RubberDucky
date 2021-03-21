package commandHandling.commands;

import commandHandling.commands.place.*;
import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import org.slf4j.Logger;
import services.BotExceptions;

public class Place implements CommandInterface {
    public Place(Logger LOGGER) {
        LOGGER.info("Loaded Command Place");
    }

    @Override
    public void handle(CommandContext ctx) {
        switch (ctx.getArguments().get(0)) {
            case "encode":
                if (ctx.getArguments().size() < 5) BotExceptions.invalidArgumentsException(ctx);
                 else new encode(ctx);
                break;
            case "preview":
                new preview(ctx);
                break;
            case "draw":
                new draw(ctx);
                break;
            case "queue":
                new queue(ctx);
                break;
            default:
                BotExceptions.commandNotFoundException(ctx, ctx.getArguments().get(0));
        }
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
