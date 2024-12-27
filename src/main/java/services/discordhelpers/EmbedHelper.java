package services.discordhelpers;

import net.dv8tion.jda.api.EmbedBuilder;
import assets.Config;

public class EmbedHelper {
    public static EmbedBuilder embedBuilder(String title) {
        return (new EmbedBuilder()).setColor(Config.EMBED_COLOR).setTitle(title);
    }
}
