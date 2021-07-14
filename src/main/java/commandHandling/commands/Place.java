package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import commandHandling.commands.place.*;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import resources.CONFIG;
import services.BotExceptions;
import services.DiscordLogger;
import services.Miscellaneous;
import services.PermissionManager;

import java.awt.*;

public class Place implements CommandInterface {
    private static PlaceData placeData = new PlaceData();

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
                DiscordLogger.command(ctx, "place", true);
                ctx.getChannel().sendMessageEmbeds(getHelp().setTitle("Help - Place")
                        .setColor(new Color(0xb074ad)).build()).queue(
                        msg -> Miscellaneous.deleteMsg(msg, 64)
                );
                break;
            case "view":
                (new Thread(new PlaceView(ctx))).start();
                break;
            default:
                DiscordLogger.command(ctx, "place", false);
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
            DiscordLogger.command(ctx, "place", true);
        } else {
            DiscordLogger.command(ctx, "place", false);
            BotExceptions.missingPermissionException(ctx);
        }
    }

    private void stopQ (CommandContext ctx) {
        if (PermissionManager.authenticateOwner(ctx)) {
            placeData.stopQ = !placeData.stopQ;
            DiscordLogger.command(ctx, "place", true);
        } else {
            DiscordLogger.command(ctx, "place", false);
            BotExceptions.missingPermissionException(ctx);
        }
    }

    private void delete (CommandContext ctx) {
        if (PermissionManager.authenticateOwner(ctx)) {
            new PlaceDelete(ctx);
        } else {
            DiscordLogger.command(ctx, "place", false);
            BotExceptions.missingPermissionException(ctx);
        }
    }

    private void getFile (CommandContext ctx) {
        new PlaceGetFile(ctx);
    }

    private void verify (CommandContext ctx) {
        if (PermissionManager.authenticateOwner(ctx)) {
            placeData.verify = !placeData.verify;
            DiscordLogger.command(ctx, "place", true);
        } else {
            DiscordLogger.command(ctx, "place", false);
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
        String prefix = CONFIG.Prefix.get();

        embed.setDescription("Place has been created by <@!153929916977643521>\n" +
                "[Live View & Time Lapse](https://place.battlerush.dev/)");

        embed.addField("__Encode__", "Returns a txt file with the draw commands for the attached image\n" +
                "```" + prefix + "place encode <x> <y> <width> <height> [<mode>] ```\n", false);

        embed.addField("__Preview__", "Returns a preview of the attached or referenced txt file\n" +
                        "```" + prefix + "place preview```", false);

        embed.addField("__Queue__", "Queues the attached or referenced txt file\n" +
                "```" + prefix + "place queue```", false);

        embed.addField("__Draw__", "Starts the drawing process or returns the status if its already " +
                "drawing\n```" + prefix + "place draw```", false);

        embed.addField("__Status__", "Returns the progress of the current drawing\n" +
                "```" + prefix + "place status```", false);

        embed.addField("__ViewQueue__", "Returns a list of all the queued files\n" +
                "```" + prefix + "place viewQ```", false);

        embed.addField("__GetFile__", "Returns the requested txt file from the queue\n" +
                "```" + prefix + "place getFile <file id>```", false);

        embed.addField("", "**OWNER ONLY**", false);

        embed.addField("__Stop__", "Stop drawing\n" +
                "```" + prefix + "place stop```", true);

        embed.addField("__StopQ__", "Stop queueing\n" +
                "```" + prefix + "place stopQ```", true);

        embed.addField("__Delete__", "Delete a file from the Queue\n" +
                "```" + prefix + "place delete <file id>```", true);

        return embed;
    }
}
