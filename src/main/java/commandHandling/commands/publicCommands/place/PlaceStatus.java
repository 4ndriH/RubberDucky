package commandHandling.commands.publicCommands.place;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.EMOTES;
import services.logging.EmbedHelper;
import services.place.PlaceData;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.List;

public class PlaceStatus implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(PlaceStatus.class);

    public PlaceStatus(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        EmbedBuilder embed = EmbedHelper.embedBuilder("Status");

        new PlaceData(4);

        if (PlaceData.drawing) {
            embed.setDescription("Drawing project " + PlaceData.ID);
            embed.addField("__Estimated completion date__", "<t:" +
                    (Instant.now().getEpochSecond() + PlaceData.totalPixels - PlaceData.drawnPixels) + ":F>", false);

            embed.addField("__Total Pixels:__", "" + formatNr(PlaceData.totalPixels), true);
            embed.addField("__Drawn Pixels:__", "" + formatNr(PlaceData.drawnPixels), true);
            embed.addField("__Pixels Left:__", "" + formatNr(PlaceData.totalPixels - PlaceData.drawnPixels), true);

            if (PlaceData.fixedPixels > 0) {
                embed.addField("__Fixed Pixels:__", "" + formatNr(PlaceData.fixedPixels), true);
            }

            embed.addField("__Progress__", progress() + " " + PlaceData.getProgress() + "%", false);
            embed.addField("__StopQ__", "" + PlaceData.stopQ, true);
            embed.addField("__Verify__", "" + PlaceData.verify, true);
        } else {
            embed.setDescription("Currently not drawing");
        }

        EmbedHelper.sendEmbed(ctx, embed, 64);
    }

    private String formatNr(int n) {
        return new DecimalFormat("###,###,###").format(n).replaceAll("[,,.]", "'");
    }

    private String progress () {
        int progress = PlaceData.getProgress();
        StringBuilder bar = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            if (progress - 10 >= 0) {
                bar.append(elementSelection(i, 10));
            } else {
                bar.append(elementSelection(i, progress));
            }
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

    @Override
    public String getName() {
        return "PlaceStatus";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Returns information about the current projects progress");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("ps");
    }
}
