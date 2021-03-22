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
        draw drawInstance = null;

        switch (ctx.getArguments().get(0)) {
            case "encode":
                if (ctx.getArguments().size() < 5) BotExceptions.invalidArgumentsException(ctx);
                 else new encode(ctx);
                break;
            case "preview":
                new preview(ctx);
                break;
            case "draw":
                if (drawInstance == null || !drawInstance.drawing)
                    drawInstance = new draw(ctx);
                else
                    ctx.getMessage().reply("Already Drawing \n Progress: " +
                            String.format("%,.2f", drawInstance.progress)).queue();
                break;
            case "queue":
                new queue(ctx);
                break;
            case "stop":
                if (drawInstance != null)
                    drawInstance.draw = false;
                else
                    ctx.getChannel().sendMessage("Currently not drawing").queue();
                break;
            case "stopQ":
                drawInstance.stopQ = true;
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
