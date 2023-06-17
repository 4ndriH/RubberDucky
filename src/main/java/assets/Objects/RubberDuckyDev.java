package assets.Objects;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class RubberDuckyDev extends DiscordServer{
    public RubberDuckyDev(net.dv8tion.jda.api.JDA jda) {
        super(jda);
    }

    public TextChannel randomShit() {
        return JDA.getGuildById(817850050013036605L).getTextChannelById(1020951518582673478L);
    }

    public TextChannel hq() {
        return JDA.getGuildById(817850050013036605L).getTextChannelById(991686525651800175L);
    }

    public TextChannel botExceptionLog() {
        return JDA.getGuildById(817850050013036605L).getTextChannelById(841393155478650920L);
    }

    public TextChannel devBotExceptions() {
        return JDA.getGuildById(817850050013036605L).getTextChannelById(865693419376738315L);
    }

    public TextChannel placeLog() {
        return JDA.getGuildById(817850050013036605L).getTextChannelById(969901898389925959L);
    }
}
