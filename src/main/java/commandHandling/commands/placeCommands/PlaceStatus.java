package commandHandling.commands.placeCommands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import assets.EMOTES;
import services.discordHelpers.EmbedHelper;
import assets.Objects.PlaceData;

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

        if (PlaceData.drawing) {
            embed.setDescription("Drawing project " + PlaceData.ID);
            embed.addField("__Estimated completion time", "<t:" +
                    (Instant.now().getEpochSecond() + (int)((PlaceData.totalPixels - PlaceData.drawnPixels) * 1.0587)) + ":F>", false);

            embed.addField("__Total Pixels:__", "" + formatNr(PlaceData.totalPixels), true);
            embed.addField("__Drawn Pixels:__", "" + formatNr(PlaceData.drawnPixels), true);
            embed.addField("__Pixels Left:__", "" + formatNr(PlaceData.totalPixels - PlaceData.drawnPixels), true);

            if (!PlaceData.fixingQ.isEmpty() || PlaceData.fixedPixels > 0) {
                embed.addField("__Pixels to fix:__", "" + formatNr(PlaceData.fixingQ.size()), true);
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
                case 0 -> ret = EMOTES.L0.getAsEmote();
                case 1 -> ret = EMOTES.L1.getAsEmote();
                case 2 -> ret = EMOTES.L2.getAsEmote();
                case 3 -> ret = EMOTES.L3.getAsEmote();
                case 4 -> ret = EMOTES.L4.getAsEmote();
                case 5 -> ret = EMOTES.L5.getAsEmote();
                case 6 -> ret = EMOTES.L6.getAsEmote();
                case 7 -> ret = EMOTES.L7.getAsEmote();
                case 8 -> ret = EMOTES.L8.getAsEmote();
                case 9 -> ret = EMOTES.L9.getAsEmote();
                case 10 -> ret = EMOTES.L10.getAsEmote();
            }
        } else if (segment == 9) {
            ret = switch (percentage) {
                case 0 -> EMOTES.R0.getAsEmote();
                case 1 -> EMOTES.R1.getAsEmote();
                case 2 -> EMOTES.R2.getAsEmote();
                case 3 -> EMOTES.R3.getAsEmote();
                case 4 -> EMOTES.R4.getAsEmote();
                case 5 -> EMOTES.R5.getAsEmote();
                case 6 -> EMOTES.R6.getAsEmote();
                case 7 -> EMOTES.R7.getAsEmote();
                case 8 -> EMOTES.R8.getAsEmote();
                case 9 -> EMOTES.R9.getAsEmote();
                case 10 -> EMOTES.R10.getAsEmote();
                default -> ret;
            };
        } else {
            ret = switch (percentage) {
                case 0 -> EMOTES.M0.getAsEmote();
                case 1 -> EMOTES.M1.getAsEmote();
                case 2 -> EMOTES.M2.getAsEmote();
                case 3 -> EMOTES.M3.getAsEmote();
                case 4 -> EMOTES.M4.getAsEmote();
                case 5 -> EMOTES.M5.getAsEmote();
                case 6 -> EMOTES.M6.getAsEmote();
                case 7 -> EMOTES.M7.getAsEmote();
                case 8 -> EMOTES.M8.getAsEmote();
                case 9 -> EMOTES.M9.getAsEmote();
                case 10 -> EMOTES.M10.getAsEmote();
                default -> ret;
            };
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
