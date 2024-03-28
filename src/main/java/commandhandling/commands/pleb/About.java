package commandhandling.commands.pleb;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import services.miscellaneous.Format;
import services.discordhelpers.EmbedHelper;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

import static services.discordhelpers.MessageSendHelper.sendMessage;

public class About implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        RuntimeMXBean rmb = ManagementFactory.getRuntimeMXBean();
        EmbedBuilder embed = EmbedHelper.embedBuilder("About RubberDucky");

        embed.setThumbnail(ctx.getSelfUser().getAvatarUrl());
        embed.setDescription("[GitHub](https://github.com/4ndriH/RubberDucky)");
        embed.addField("**JDA version:**", JDAInfo.VERSION_MAJOR + "."
                + JDAInfo.VERSION_MINOR + "." + JDAInfo.VERSION_REVISION, true);
        embed.addField("**Uptime:**", Format.Time((int)(rmb.getUptime() / 1000)), true);

        MessageCreateAction mca = ctx.getChannel().sendMessageEmbeds(embed.build());
        sendMessage(mca, 32);
    }

    @Override
    public String getName() {
        return "About";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Provides some information about me");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("info", "source");
    }
}
