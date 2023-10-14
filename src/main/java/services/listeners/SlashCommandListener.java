package services.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;
import services.discordHelpers.EmbedHelper;

import static commandHandling.commands.publicCommands.ChannelEfficiency.*;

public class SlashCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("ping")) {
            event.reply("Pong!").setEphemeral(true).queue();
        } else if (event.getName().equals("channelefficiency")) {
            //   event.deferReply().queue(); // Tell discord we received the command, send a thinking... message to the user
            OptionMapping channelOption = event.getOption("channel");
            String parameter;

            if (channelOption == null) {
                parameter = "Count";
            } else {
                parameter = channelOption.getAsString();
            }

            String embedTitel = parameter.equals("Count") ? "To Infinity And Beyond" : "ETH-Place-Bots";
            EmbedBuilder embed = EmbedHelper.embedBuilder(embedTitel);
            embed.setImage("attachment://LineChart.jpg");
            event.replyEmbeds(embed.build()).addFiles(FileUpload.fromData(convert(generatePlot(parameter)), "LineChart.jpg")).queue();
        } else {
            super.onSlashCommandInteraction(event);
        }
    }
}
