package commandHandling.commands;

import commandHandling.commands.place.*;
import commandHandling.*;
import org.slf4j.Logger;
import services.*;

public class Place implements CommandInterface {
    private static draw drawInstance = null;

    public Place(Logger LOGGER) {
        LOGGER.info("Loaded Command Place");
    }

    @Override
    public void handle(CommandContext ctx) {

        switch (ctx.getArguments().get(0)) {
            case "encode": case "e":
                if (ctx.getArguments().size() < 5) BotExceptions.invalidArgumentsException(ctx);
                else new encode(ctx);
                break;
            case "preview": case "p":
                new preview(ctx);
                break;
            case "draw": case "d":
                if (drawInstance == null || !drawInstance.drawing) drawInstance = new draw(ctx);
                else ctx.getMessage().reply("Already Drawing \n Progress: " +
                            String.format("%,.2f", drawInstance.progress)).queue();
                break;
            case "queue": case "q":
                new queue(ctx);
                break;
            case "stop": case "s":
                if (drawInstance != null) drawInstance.draw = false;
                else ctx.getChannel().sendMessage("Currently not drawing").queue();
                break;
            case "stopQ": case "stopq": case "sq":
                if (drawInstance != null) drawInstance.stopQ = true;
                break;
            case "viewQ": case "vq":
                new viewQ(ctx);
                break;
            case "getfile": case "gf":
                if (ctx.getArguments().size() < 2) BotExceptions.invalidArgumentsException(ctx);
                else new getFile(ctx);
                break;
            default:
                BotExceptions.commandNotFoundException(ctx, ctx.getArguments().get(0));
                CommandReaction.fail(ctx);
                return;
        }
        CommandReaction.success(ctx);
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
