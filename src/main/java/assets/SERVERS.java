package assets;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class SERVERS {
    public static Guild RubberDuckyDev;
    public static Guild DINFK;

    public SERVERS(JDA jda) {
        RubberDuckyDev = jda.getGuildById("817850050013036605");
        DINFK = jda.getGuildById("747752542741725244");
    }
}
