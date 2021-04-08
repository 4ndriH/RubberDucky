package commandHandling.commands.place;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import resources.EMOTES;
import services.database.dbHandlerQ;

import java.awt.*;

public class status {
    private final draw drawInstance;
    private final CommandContext ctx;

    public status(draw drawInstance, CommandContext ctx) {
        this.drawInstance = new draw(ctx);//drawInstance;

        this.drawInstance.progress = 23;
        this.drawInstance.id = 6969;
        this.drawInstance.total = 120000;
        this.drawInstance.drawing = true;


        this.ctx = ctx;
        statusReporting();
    }

    private void statusReporting() {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("Status");
        embed.setColor(new Color(0xb074ad));

        if (drawInstance != null && drawInstance.drawing) {
            embed.setDescription("Drawing project " + drawInstance.id);
            embed.addField("__Estimated time remaining__",
                    timeConversion(drawInstance.total - dbHandlerQ.getProgress(drawInstance.id)), false);
            embed.addField("__Progress__", progress() + " " + drawInstance.progress + "%", false);
        } else {
            embed.setDescription("Currently not drawing");
        }

        ctx.getChannel().sendMessage(embed.build()).queue();
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

    private String progress () {
        int progress = drawInstance.progress;
        StringBuilder bar = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            if (progress - 10 >= 0)
                bar.append("<").append(elementSelection(i, 10)).append(">");
            else
                bar.append("<").append(elementSelection(i, progress)).append(">");
            progress = Math.max(0, progress - 10);
        }

        return bar.toString();
    }

    private String elementSelection(int segment, int percentage) {
        String ret = "";
        if (segment == 0) {
            switch (percentage) {
                case 0:
                    ret = EMOTES.L0.getAsReaction();
                    break;
                case 1:
                    ret = EMOTES.L1.getAsReaction();
                    break;
                case 2:
                    ret = EMOTES.L2.getAsReaction();
                    break;
                case 3:
                    ret = EMOTES.L3.getAsReaction();
                    break;
                case 4:
                    ret = EMOTES.L4.getAsReaction();
                    break;
                case 5:
                    ret = EMOTES.L5.getAsReaction();
                    break;
                case 6:
                    ret = EMOTES.L6.getAsReaction();
                    break;
                case 7:
                    ret = EMOTES.L7.getAsReaction();
                    break;
                case 8:
                    ret = EMOTES.L8.getAsReaction();
                    break;
                case 9:
                    ret = EMOTES.L9.getAsReaction();
                    break;
                case 10:
                    ret = EMOTES.L10.getAsReaction();
                    break;
            }
        } else if (segment == 9) {
            switch (percentage) {
                case 0:
                    ret = EMOTES.R0.getAsReaction();
                    break;
                case 1:
                    ret = EMOTES.R1.getAsReaction();
                    break;
                case 2:
                    ret = EMOTES.R2.getAsReaction();
                    break;
                case 3:
                    ret = EMOTES.R3.getAsReaction();
                    break;
                case 4:
                    ret = EMOTES.R4.getAsReaction();
                    break;
                case 5:
                    ret = EMOTES.R5.getAsReaction();
                    break;
                case 6:
                    ret = EMOTES.R6.getAsReaction();
                    break;
                case 7:
                    ret = EMOTES.R7.getAsReaction();
                    break;
                case 8:
                    ret = EMOTES.R8.getAsReaction();
                    break;
                case 9:
                    ret = EMOTES.R9.getAsReaction();
                    break;
                case 10:
                    ret = EMOTES.R10.getAsReaction();
                    break;
            }
        } else {
            switch (percentage) {
                case 0:
                    ret = EMOTES.M0.getAsReaction();
                    break;
                case 1:
                    ret = EMOTES.M1.getAsReaction();
                    break;
                case 2:
                    ret = EMOTES.M2.getAsReaction();
                    break;
                case 3:
                    ret = EMOTES.M3.getAsReaction();
                    break;
                case 4:
                    ret = EMOTES.M4.getAsReaction();
                    break;
                case 5:
                    ret = EMOTES.M5.getAsReaction();
                    break;
                case 6:
                    ret = EMOTES.M6.getAsReaction();
                    break;
                case 7:
                    ret = EMOTES.M7.getAsReaction();
                    break;
                case 8:
                    ret = EMOTES.M8.getAsReaction();
                    break;
                case 9:
                    ret = EMOTES.M9.getAsReaction();
                    break;
                case 10:
                    ret = EMOTES.M10.getAsReaction();
                    break;
            }
        }
        return ret;
    }
}
