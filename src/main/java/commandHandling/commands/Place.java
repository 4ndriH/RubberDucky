package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import commandHandling.commands.place.*;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import services.BotExceptions;
import services.PermissionManager;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Place implements CommandInterface {
    private static placeData placeData = new placeData();

    public Place(Logger LOGGER) {
        LOGGER.info("Loaded Command Place");
    }

    @Override
    public void handle(CommandContext ctx) {
        String cmd;

        try {
            cmd = ctx.getArguments().get(0).toLowerCase();
        } catch (Exception e) {
            cmd = "help";
        }

        switch (cmd) {
            case "encode": case "e":
                encode(ctx);
                break;
            case "preview": case "p":
                new Thread(new PlacePreview(ctx)).start();
                break;
            case "draw": case "d":
                draw(ctx);
                break;
            case "queue": case "q":
                new PlaceQueue(ctx);
                break;
            case "stop":
                stop(ctx);
                break;
            case "stopq": case "sq":
                stopQ(ctx);
                break;
            case "delete":
                delete(ctx);
                break;
            case "viewQ": case "vq":
                new PlaceViewQ(ctx);
                break;
            case "getfile": case "gf":
                getFile(ctx);
                break;
            case "status": case "s":
                new PlaceStatus(placeData, ctx);
                break;
            case "verify": case "v":
                verify(ctx);
                break;
            case "help":
                ctx.getChannel().sendMessage(getHelp().build()).queue(
                        msg -> msg.delete().queueAfter(64, TimeUnit.SECONDS)
                );
                break;
            default:
                services.Logger.command(ctx, "place", false);
                break;
        }
    }

    private void encode (CommandContext ctx) {
        Thread encodeThread = new Thread(new PlaceEncode(ctx));
        encodeThread.start();
    }

    private void draw (CommandContext ctx) {
        if (!placeData.drawing || placeData.stop) {
            placeData.reset();
            Thread drawThread = new Thread(new PlaceDraw(ctx, placeData));
            drawThread.start();
        } else {
            new PlaceStatus(placeData, ctx);
        }
    }

    private void stop (CommandContext ctx) {
        if (PermissionManager.authenticateOwner(ctx)) {
            placeData.stop = true;
            services.Logger.command(ctx, "place", true);
        } else {
            services.Logger.command(ctx, "place", false);
            BotExceptions.missingPermissionException(ctx);
        }
    }

    private void stopQ (CommandContext ctx) {
        if (PermissionManager.authenticateOwner(ctx)) {
            placeData.stopQ = !placeData.stopQ;
            services.Logger.command(ctx, "place", true);
        } else {
            services.Logger.command(ctx, "place", false);
            BotExceptions.missingPermissionException(ctx);
        }
    }

    private void delete (CommandContext ctx) {
        if (PermissionManager.authenticateOwner(ctx)) {
            new PlaceDelete(ctx);
        } else {
            services.Logger.command(ctx, "place", false);
            BotExceptions.missingPermissionException(ctx);
        }
    }

    private void getFile (CommandContext ctx) {
        new PlaceGetFile(ctx);
    }

    private void verify (CommandContext ctx) {
        if (PermissionManager.authenticateOwner(ctx)) {
            placeData.verify = !placeData.verify;
            services.Logger.command(ctx, "place", true);
        } else {
            services.Logger.command(ctx, "place", false);
            BotExceptions.missingPermissionException(ctx);
        }

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
                "[Live View & Time Lapse](https://place.battlerush.dev/)");

        embed.addField("__Encode__", "Returns a txt file with the draw commands for the attached image\n" +
                "```rdplace encode <x> <y> <width> <height> [<mode>] ```\n", false);

        embed.addField("__Preview__", "Returns a preview of the attached or referenced txt file\n" +
                        "```rdplace preview```", false);

        embed.addField("__Queue__", "Queues the attached or referenced txt file\n" +
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
