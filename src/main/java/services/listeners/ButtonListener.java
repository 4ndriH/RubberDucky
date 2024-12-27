package services.listeners;

import commandhandling.commands.owner.Kill;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import assets.Config;

public class ButtonListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getComponentId().startsWith("$") && !event.getUser().getId().equals(Config.OWNER_ID)) {
            event.reply("Did you really think this would work?").setEphemeral(true).queue();
            return;
        }

        if (event.getComponentId().equals("$KillAbort")) {
            event.getMessage().delete().queue();
        } else if (event.getComponentId().equals("$KillProceed")) {
            event.getMessage().delete().queue();
            Kill.executeKill();
        } else {
            event.reply("You are not supposed to press this button").setEphemeral(true).queue();
        }
    }
}
