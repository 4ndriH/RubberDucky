package assets.Objects;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class Dinfk extends DiscordServer {
    public Dinfk(JDA jda) {
        super(jda);
    }

    public TextChannel ethPlaceBots() {
        return JDA.getGuildById(747752542741725244L).getTextChannelById(819966095070330950L);
    }

    public TextChannel spam() {
        return JDA.getGuildById(747752542741725244L).getTextChannelById(768600365602963496L);
    }
}
