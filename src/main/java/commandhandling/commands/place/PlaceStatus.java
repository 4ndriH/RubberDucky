package commandhandling.commands.place;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import assets.Emotes;
import services.miscellaneous.Format;
import services.discordhelpers.EmbedHelper;
import assets.objects.PlaceData;

import java.time.Instant;
import java.util.List;

public class PlaceStatus implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(PlaceStatus.class);

    @Override
    public void handle(CommandContext ctx) {
        EmbedBuilder embed = EmbedHelper.embedBuilder("Status");

        if (PlaceData.drawing) {
            embed.setDescription("Drawing project " + PlaceData.ID);
            embed.addField("__Estimated completion time__", "<t:" +
                    (Instant.now().getEpochSecond() + (int)((PlaceData.totalPixels - PlaceData.drawnPixels + PlaceData.fixingQ.size() - PlaceData.fixedPixels) * 1.0587)) + ":F>", false);

            embed.addField("__Total Pixels:__", "" + Format.Number(PlaceData.totalPixels), true);
            embed.addField("__Drawn Pixels:__", "" + Format.Number(PlaceData.drawnPixels), true);
            embed.addField("__Pixels Left:__", "" + Format.Number(PlaceData.totalPixels - PlaceData.drawnPixels), true);

            if (!PlaceData.fixingQ.isEmpty() || PlaceData.fixedPixels > 0) {
                embed.addField("__Pixels to fix:__", "" + Format.Number(PlaceData.fixingQ.size()), true);
                embed.addField("__Fixed Pixels:__", "" + Format.Number(PlaceData.fixedPixels), true);
                embed.addBlankField(true);
            }

            embed.addField("__Progress__", progress() + " " + PlaceData.getProgress() + "%", false);
            embed.addField("__StopQ__", "" + PlaceData.stopQ, true);
            embed.addField("__Verify__", "" + PlaceData.verify, true);
        } else {
            embed.setDescription("Currently not drawing");
        }

        EmbedHelper.sendEmbed(ctx, embed, 64);
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
                case 0 -> ret = Emotes.L0.getAsEmote();
                case 1 -> ret = Emotes.L1.getAsEmote();
                case 2 -> ret = Emotes.L2.getAsEmote();
                case 3 -> ret = Emotes.L3.getAsEmote();
                case 4 -> ret = Emotes.L4.getAsEmote();
                case 5 -> ret = Emotes.L5.getAsEmote();
                case 6 -> ret = Emotes.L6.getAsEmote();
                case 7 -> ret = Emotes.L7.getAsEmote();
                case 8 -> ret = Emotes.L8.getAsEmote();
                case 9 -> ret = Emotes.L9.getAsEmote();
                case 10 -> ret = Emotes.L10.getAsEmote();
            }
        } else if (segment == 9) {
            ret = switch (percentage) {
                case 0 -> Emotes.R0.getAsEmote();
                case 1 -> Emotes.R1.getAsEmote();
                case 2 -> Emotes.R2.getAsEmote();
                case 3 -> Emotes.R3.getAsEmote();
                case 4 -> Emotes.R4.getAsEmote();
                case 5 -> Emotes.R5.getAsEmote();
                case 6 -> Emotes.R6.getAsEmote();
                case 7 -> Emotes.R7.getAsEmote();
                case 8 -> Emotes.R8.getAsEmote();
                case 9 -> Emotes.R9.getAsEmote();
                case 10 -> Emotes.R10.getAsEmote();
                default -> ret;
            };
        } else {
            ret = switch (percentage) {
                case 0 -> Emotes.M0.getAsEmote();
                case 1 -> Emotes.M1.getAsEmote();
                case 2 -> Emotes.M2.getAsEmote();
                case 3 -> Emotes.M3.getAsEmote();
                case 4 -> Emotes.M4.getAsEmote();
                case 5 -> Emotes.M5.getAsEmote();
                case 6 -> Emotes.M6.getAsEmote();
                case 7 -> Emotes.M7.getAsEmote();
                case 8 -> Emotes.M8.getAsEmote();
                case 9 -> Emotes.M9.getAsEmote();
                case 10 -> Emotes.M10.getAsEmote();
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
