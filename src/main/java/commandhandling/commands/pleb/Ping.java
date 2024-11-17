package commandhandling.commands.pleb;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import services.discordhelpers.EmbedHelper;

import static services.discordhelpers.MessageSendHelper.sendMessage;

public class Ping implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        JDA jda = ctx.getJDA();

        jda.getRestPing().queue(
                ping -> {
                    EmbedBuilder embed = EmbedHelper.embedBuilder("Ping");
                    embed.addField("__Discord Server Ping:__", ping + "ms", false);
                    embed.addField("__Discord Websocket Ping:__", jda.getGatewayPing() + "ms", false);
                    MessageCreateAction mca = ctx.getChannel().sendMessageEmbeds(embed.build());
                    sendMessage(ctx, mca, 32);
                }
        );
    }

    @Override
    public String getName() {
        return "Ping";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Returns the current ping");
        return embed;
    }
}
