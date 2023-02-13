package assets;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

public class CHANNELS {
    // DINFK channels
    public static TextChannel ethPlaceBots;
    public static TextChannel ethPlaceBots2;
    public static TextChannel spam;
    public static ThreadChannel toInfinityAndBeyond;

    // RubberDuckyDev channels
    public static TextChannel randomShit;
    public static TextChannel hq;
    public static TextChannel botExceptionLog;
    public static TextChannel devBotExceptions;
    public static TextChannel placeLog;

    public CHANNELS() {
        if (SERVERS.DINFK != null) {
            ethPlaceBots = SERVERS.DINFK.getTextChannelById("819966095070330950");
            ethPlaceBots2 = SERVERS.DINFK.getTextChannelById("955751651942211604");
            spam = SERVERS.DINFK.getTextChannelById("768600365602963496");
            toInfinityAndBeyond = SERVERS.DINFK.getThreadChannelById("996746797236105236");
        }

        if (SERVERS.RubberDuckyDev != null) {
            randomShit = SERVERS.RubberDuckyDev.getTextChannelById("1020951518582673478");
            hq = SERVERS.RubberDuckyDev.getTextChannelById("991686525651800175");
            botExceptionLog = SERVERS.RubberDuckyDev.getTextChannelById("841393155478650920");
            devBotExceptions = SERVERS.RubberDuckyDev.getTextChannelById("865693419376738315");
            placeLog = SERVERS.RubberDuckyDev.getTextChannelById("969901898389925959");
        }
    }
}