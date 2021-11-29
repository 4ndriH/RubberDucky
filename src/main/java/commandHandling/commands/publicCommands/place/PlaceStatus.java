package commandHandling.commands.publicCommands.place;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import resources.EMOTES;
import services.Miscellaneous;
import services.PermissionManager;

import java.awt.*;
import java.text.DecimalFormat;

public class PlaceStatus {
    private final PlaceData pD;
    private final CommandContext ctx;

    public PlaceStatus(PlaceData pD, CommandContext ctx) {
        this.pD = pD;
        this.ctx = ctx;
        main();
    }

    private void main() {
        EmbedBuilder embed = new EmbedBuilder();

        Miscellaneous.CommandLog("Place", ctx, true);

        embed.setTitle("Status");
        embed.setColor(new Color(0xb074ad));

        if (pD.drawing) {
            embed.setDescription("Drawing project " + pD.id);
            embed.addField("__Estimated time remaining__",
                    Miscellaneous.timeFormat(pD.totalPixels - pD.drawnPixels), false);

            embed.addField("__Total Pixels:__", "" + formatNr(pD.totalPixels), true);
            embed.addField("__Drawn Pixels:__", "" + formatNr(pD.drawnPixels), true);
            embed.addField("__Pixels Left:__", "" + formatNr(pD.totalPixels - pD.drawnPixels), true);

            if (pD.fixedPixels > 0) {
                embed.addField("__Fixed Pixels:__", "" + formatNr(pD.fixedPixels), true);
            }

            embed.addField("__Progress__", progress() + " " + pD.progress + "%", false);

            if (PermissionManager.authenticateOwner(ctx) && ctx.getArguments().size() > 1) {
                embed.addField("__StopQ__", "" + pD.stopQ, true);
                embed.addField("__Verify__", "" + pD.verify, true);
            }
        } else {
            embed.setDescription("Currently not drawing");
        }

        ctx.getChannel().sendMessageEmbeds(embed.build()).queue(
                msg -> Miscellaneous.deleteMsg(msg, 32)
        );
    }

    private String formatNr(int n) {
        return new DecimalFormat("###,###,###").format(n);
    }

    private String progress () {
        int progress = pD.progress;
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
