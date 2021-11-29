package services.listener;

import commandHandling.commands.ownerCommands.Kill;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import resources.CONFIG;

public class ButtonListener extends ListenerAdapter {
    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        if (event.getComponentId().startsWith("$") && !event.getUser().getId().equals(CONFIG.OwnerID.get())) {
            event.reply("Did you really think this would work?").setEphemeral(true).queue();
            return;
        }

        if (event.getComponentId().equals("$KillAbort")) {
            event.getMessage().delete().queue();
        } else if (event.getComponentId().equals("$KillProceed")) {
            event.getMessage().delete().queue();
            Kill.executeKill();
        }
    }
}
