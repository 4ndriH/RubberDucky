package commandHandling.commands.place;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import resources.EMOTES;
import services.Logger;
import services.PermissionManager;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class PlaceStatus {
    private final PlaceData placeData;
    private final CommandContext ctx;

    public PlaceStatus(PlaceData placeData, CommandContext ctx) {
        this.placeData = placeData;
        this.ctx = ctx;
        main();
    }

    private void main() {
        EmbedBuilder embed = new EmbedBuilder();

        Logger.command(ctx, "place", true);

        embed.setTitle("Status");
        embed.setColor(new Color(0xb074ad));

        if (placeData.drawing) {
            embed.setDescription("Drawing project " + placeData.id);
            embed.addField("__Estimated time remaining__",
                    timeConversion(placeData.totalPixels - placeData.drawnPixels), false);
            embed.addField("__Pixels__", pixelAlignment(), false);
            embed.addField("__Progress__", progress() + " " + placeData.progress + "%", false);


            if (PermissionManager.authenticateOwner(ctx) && ctx.getArguments().size() > 1) {
                embed.addField("__StopQ__", "" + placeData.stopQ, true);
                embed.addField("__Verify__", "" + placeData.verify, true);
            }
        } else {
            embed.setDescription("Currently not drawing");
        }

        ctx.getChannel().sendMessage(embed.build()).queue(
                msg -> msg.delete().queueAfter(32, TimeUnit.SECONDS)
        );
    }

    private String timeConversion(int linesCnt) {
        int seconds = linesCnt % 60;
        int minutes = (linesCnt - seconds) / 60 % 60;
        int hours = ((linesCnt - seconds) / 60 - minutes) / 60;

        String days = "";
        if (hours > 23) {
            days = (hours - (hours %= 24)) / 24 + "";
            if (Integer.parseInt(days) == 1) {
                days += " day, ";
            } else {
                days += " days, ";
            }
        }
        return String.format(days + "%02d:%02d:%02d", hours, minutes, seconds);
    }

    private String pixelAlignment () {
        String pixelInfo = "";
        pixelInfo += String.format("%-10s % 9d%n", "Total pixels:", placeData.totalPixels);
        pixelInfo += String.format("%-10s % 9d%n", "Drawn pixels:", placeData.drawnPixels);
        pixelInfo += String.format("%-13s % 9d%n", "Pixels left:", placeData.totalPixels - placeData.drawnPixels);
        if (placeData.fixedPixels != 0) {
            pixelInfo += String.format("%-10s % 9d%n", "Fixed pixels:", placeData.fixedPixels);
        }
        return pixelInfo;
    }

    private String progress () {
        int progress = placeData.progress;
        StringBuilder bar = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            if (progress - 10 >= 0)
                bar.append(elementSelection(i, 10));
            else
                bar.append(elementSelection(i, progress));
            progress = Math.max(0, progress - 10);
        }

        return bar.toString();
    }

    private String elementSelection(int segment, int percentage) {
        String ret = "";
        if (segment == 0) {
            switch (percentage) {
                case 0:
                    ret = EMOTES.L0.getAsEmote();
                    break;
                case 1:
                    ret = EMOTES.L1.getAsEmote();
                    break;
                case 2:
                    ret = EMOTES.L2.getAsEmote();
                    break;
                case 3:
                    ret = EMOTES.L3.getAsEmote();
                    break;
                case 4:
                    ret = EMOTES.L4.getAsEmote();
                    break;
                case 5:
                    ret = EMOTES.L5.getAsEmote();
                    break;
                case 6:
                    ret = EMOTES.L6.getAsEmote();
                    break;
                case 7:
                    ret = EMOTES.L7.getAsEmote();
                    break;
                case 8:
                    ret = EMOTES.L8.getAsEmote();
                    break;
                case 9:
                    ret = EMOTES.L9.getAsEmote();
                    break;
                case 10:
                    ret = EMOTES.L10.getAsEmote();
                    break;
            }
        } else if (segment == 9) {
            switch (percentage) {
                case 0:
                    ret = EMOTES.R0.getAsEmote();
                    break;
                case 1:
                    ret = EMOTES.R1.getAsEmote();
                    break;
                case 2:
                    ret = EMOTES.R2.getAsEmote();
                    break;
                case 3:
                    ret = EMOTES.R3.getAsEmote();
                    break;
                case 4:
                    ret = EMOTES.R4.getAsEmote();
                    break;
                case 5:
                    ret = EMOTES.R5.getAsEmote();
                    break;
                case 6:
                    ret = EMOTES.R6.getAsEmote();
                    break;
                case 7:
                    ret = EMOTES.R7.getAsEmote();
                    break;
                case 8:
                    ret = EMOTES.R8.getAsEmote();
                    break;
                case 9:
                    ret = EMOTES.R9.getAsEmote();
                    break;
                case 10:
                    ret = EMOTES.R10.getAsEmote();
                    break;
            }
        } else {
            switch (percentage) {
                case 0:
                    ret = EMOTES.M0.getAsEmote();
                    break;
                case 1:
                    ret = EMOTES.M1.getAsEmote();
                    break;
                case 2:
                    ret = EMOTES.M2.getAsEmote();
                    break;
                case 3:
                    ret = EMOTES.M3.getAsEmote();
                    break;
                case 4:
                    ret = EMOTES.M4.getAsEmote();
                    break;
                case 5:
                    ret = EMOTES.M5.getAsEmote();
                    break;
                case 6:
                    ret = EMOTES.M6.getAsEmote();
                    break;
                case 7:
                    ret = EMOTES.M7.getAsEmote();
                    break;
                case 8:
                    ret = EMOTES.M8.getAsEmote();
                    break;
                case 9:
                    ret = EMOTES.M9.getAsEmote();
                    break;
                case 10:
                    ret = EMOTES.M10.getAsEmote();
                    break;
            }
        }
        return ret;
    }
}
