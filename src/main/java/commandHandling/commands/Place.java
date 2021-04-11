package commandHandling.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import commandHandling.commands.place.*;
import commandHandling.*;
import org.slf4j.Logger;
import services.*;

import java.util.concurrent.TimeUnit;
import java.awt.*;

public class Place implements CommandInterface {
    private static placeData placeData = new placeData();

    public Place(Logger LOGGER) {
        LOGGER.info("Loaded Command Place");
    }

    @Override
    public void handle(CommandContext ctx) {
        String cmd;

        try {
            cmd = ctx.getArguments().get(0);
        } catch (Exception e) {
            cmd = "help";
        }

        switch (cmd) {
            case "encode": case "e":
                encode(ctx);
                break;
            case "preview": case "p":
                new preview(ctx);
                break;
            case "draw": case "d":
                draw(ctx);
                break;
            case "queue": case "q":
                new queue(ctx);
                break;
            case "stop":
                stop(ctx);
                break;
            case "stopQ": case "stopq": case "sq":
                stopQ(ctx);
                break;
            case "delete":
                delete(ctx);
                break;
            case "viewQ": case "vq":
                new viewQ(ctx);
                break;
            case "getFile": case "getfile": case "gf":
                getFile(ctx);
                break;
            case "status": case "s":
                new status(placeData, ctx);
                break;
            case "help":
                ctx.getChannel().sendMessage(getHelp().build()).queue(
                        msg -> msg.delete().queueAfter(64, TimeUnit.SECONDS)
                );
                break;
            default:
                BotExceptions.commandNotFoundException(ctx, ctx.getArguments().get(0));
                break;
        }
    }

    private void encode (CommandContext ctx) {
        Thread encodeThread = new Thread(new encode(ctx));
        encodeThread.start();
    }

    private void draw (CommandContext ctx) {
        if (!placeData.drawing) {
            placeData.reset();
            Thread drawThread = new Thread(new draw(ctx, placeData));
            drawThread.start();
        } else {
            new status(placeData, ctx);
        }
    }

    private void stop (CommandContext ctx) {
        if (PermissionManager.authOwner(ctx)) {
            placeData.stop = true;
        } else {
            BotExceptions.missingPermissionException(ctx);
        }
    }

    private void stopQ (CommandContext ctx) {
        if (PermissionManager.authOwner(ctx)) {
            placeData.stopQ = !placeData.stopQ;
        } else {
            BotExceptions.missingPermissionException(ctx);
        }
    }

    private void delete (CommandContext ctx) {
        if (PermissionManager.authOwner(ctx)) {
            new delete(ctx);
        } else {
            BotExceptions.missingPermissionException(ctx);
        }
    }

    private void getFile (CommandContext ctx) {
        new getFile(ctx);
    }

    @Override
    public String getName() {
        return "place";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Help - Place");
        embed.setColor(new Color(0xb074ad));
        embed.setDescription("Place has been created by <@!153929916977643521>\n" +
                "[Live View & Time Lapse](http://52.142.4.222:81/)");

        embed.addField("__Encode__", "Returns a txt file with the draw commands for the attached image\n" +
                "```rdplace encode <x> <y> <widht> <height> [<mode>] ```\n", false);

        embed.addField("__Preview__", "Returns a preview of the attached or referenced txt file\n" +
                        "```rdplace preview```", false);

        embed.addField("__Queue__", "Queues the attached or referenced txt file. Limited to 10.8k lines!\n" +
                "```rdplace queue```", false);

        embed.addField("__Draw__", "Starts the drawing process or returns the status if its already drawing\n" +
                "```rdplace draw```", false);

        embed.addField("__Status__", "Returns the progress of the current drawing\n" +
                "```rdplace status```", false);

        embed.addField("__ViewQueue__", "Returns a list of all the queued files\n" +
                "```rdplace viewQ```", false);

        embed.addField("__GetFile__", "Returns the requested txt file from the queue\n" +
                "```rdplace getFile <file id>```", false);

        embed.addField("", "**OWNER ONLY**", false);

        embed.addField("__Stop__", "Stop drawing\n" +
                "```rdplace stop```", true);

        embed.addField("__StopQ__", "Stop queueing\n" +
                "```rdplace stopQ```", true);

        embed.addField("__Delete__", "Delete a file from the Queue\n" +
                "```rdplace delete <file id>```", true);

        embed.setFooter("");
        return embed;
    }
}
