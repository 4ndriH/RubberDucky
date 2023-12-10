package services.listeners;

import commandhandling.commands.pleb.ChannelEfficiency;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

public class SlashCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("ping")) {
            event.reply("Pong!").setEphemeral(true).queue();
        } else if (event.getName().equals("channelefficiency")) {
            event.reply("Dou you like Duckies?").setEphemeral(true).queue();
            OptionMapping channelOption = event.getOption("channel");
            String parameter;

            if (channelOption == null) {
                parameter = "Count";
            } else {
                parameter = channelOption.getAsString();
            }

            ChannelEfficiency.doCommandStuff(event.getChannel(), parameter);
        } else {
            super.onSlashCommandInteraction(event);
        }
    }
}
