package services;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import services.logging.EmbedHelper;

import java.awt.*;
import java.util.HashMap;

public class CoolDownManager {
    private static HashMap<String, HashMap<String, Long>> coolDown = new HashMap<>();

    public static boolean coolDownCheck(CommandContext ctx, String command) {
        String user = ctx.getAuthor().getId();

        if (coolDown.containsKey(user) && coolDown.get(user).containsKey(command)) {
            long coolDownMS = coolDown.get(user).get(command);
            if (coolDownMS > System.currentTimeMillis()) {
                ctx.getMessage().replyEmbeds(buildEmbed(coolDownMS, command)).queue(
                        msg -> EmbedHelper.deleteMsg(msg, 32)
                );
                return true;
            } else {
                coolDown.get(user).remove(command);
            }
        }
        return false;
    }

    public static void addCoolDown(String command, String user, int seconds) {
        long coolDownTime = System.currentTimeMillis() + seconds * 1000L;
        if (!coolDown.containsKey(user)){
            coolDown.put(user, new HashMap<>());
        }
        if (!coolDown.get(user).containsKey(command)) {
            coolDown.get(user).put(command, coolDownTime);
        } else {
            coolDown.get(user).replace(command, coolDownTime);
        }
    }

    private static MessageEmbed buildEmbed(Long ms, String command) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("CoolDown - " + command);
        embed.setColor(new Color(0xb074ad));
        embed.setDescription(timeRemaining(ms - System.currentTimeMillis()));
        return embed.build();
    }

    private static String timeRemaining(Long ms) {
        StringBuilder time = new StringBuilder();

        long hours = ms / 3_600_000;
        long minutes = ms / 60_000 - hours * 60;
        long seconds = ms / 1000 - minutes * 60 - hours * 3600;

        time.append(hours < 10 ? "0" : "").append(hours)
                .append(minutes < 10 ? ":0" : ":").append(minutes)
                .append(seconds < 10 ? ":0" : ":").append(seconds);

        return time.toString();
    }
}
