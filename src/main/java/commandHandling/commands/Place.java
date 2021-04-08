package commandHandling.commands;

import commandHandling.commands.place.*;
import commandHandling.*;
import org.slf4j.Logger;
import services.*;

public class Place implements CommandInterface {
    private static draw drawInstance = null;
    private static Thread drawThread = null;

    public Place(Logger LOGGER) {
        LOGGER.info("Loaded Command Place");
    }

    @Override
    public void handle(CommandContext ctx) {
        String cmd = ctx.getArguments().get(0);

        if (cmd.equals("encode") || cmd.equals("e")){
            if (ctx.getArguments().size() < 5) {
                BotExceptions.invalidArgumentsException(ctx);
                return;
            }
            new encode(ctx);
        } else if (cmd.equals("preview") || cmd.equals("p")) {
            new preview(ctx);
        } else if (cmd.equals("draw") || cmd.equals("d")) {
            if (drawInstance == null || !drawInstance.drawing) {
                drawThread = new Thread(drawInstance = new draw(ctx));
                drawThread.start();
            } else {
                ctx.getChannel().sendMessage("Already drawing \nProgress: **" +
                        drawInstance.progress + "%**").queue();
            }
        } else if (cmd.equals("queue") || cmd.equals("q")) {
            new queue(ctx);
        } else if (cmd.equals("stop")) {
            if (drawInstance != null) {
                if (PermissionManager.authOwner(ctx)) {
                    drawInstance.stop = true;
                } else {
                    BotExceptions.missingPermissionException(ctx);
                }
            }
        } else if (cmd.equals("stopQ") || cmd.equals("stopq") || cmd.equals("sq")) {
            if (drawInstance != null) {
                if (PermissionManager.authOwner(ctx)) {
                    drawInstance.stopQ = true;
                } else {
                    BotExceptions.missingPermissionException(ctx);
                }
            }
        } else if (cmd.equals("delete") || cmd.equals("d")) {
            if (PermissionManager.authOwner(ctx)) {
                if (ctx.getArguments().size() < 2) {
                    BotExceptions.invalidArgumentsException(ctx);
                    return;
                }
                new delete(ctx);
            } else {
                BotExceptions.missingPermissionException(ctx);
            }
        } else if (cmd.equals("viewQ") || cmd.equals("vq")) {
            new viewQ(ctx);
        } else if (cmd.equals("getFile") || cmd.equals("getfile") || cmd.equals("gf")) {
            if (ctx.getArguments().size() < 2) {
                BotExceptions.invalidArgumentsException(ctx);
            } else {
                new getFile(ctx);
            }
        } else if(cmd.equals("status") || cmd.equals("s")) {
            new status(drawInstance, ctx);
        } else {
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
