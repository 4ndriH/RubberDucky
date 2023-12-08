package services.listeners;

import commandhandling.commands.owner.Kill;
import commandhandling.commands.coursereview.CourseReviewVerify;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import assets.Config;

public class ButtonListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getComponentId().startsWith("$") && !event.getUser().getId().equals(Config.ownerID)) {
            event.reply("Did you really think this would work?").setEphemeral(true).queue();
            return;
        }

        if (event.getComponentId().equals("$KillAbort")) {
            event.getMessage().delete().queue();
        } else if (event.getComponentId().equals("$KillProceed")) {
            event.getMessage().delete().queue();
            Kill.executeKill();
        } else if(event.getComponentId().startsWith("cfv") && event.getComponentId().endsWith(event.getUser().getId())) {
            if (event.getComponentId().contains("Reject")) {
                CourseReviewVerify.castVerdict(Integer.parseInt(event.getComponentId().split(" - ")[1]), -1);
            } else if (event.getComponentId().contains("Quit")) {
                CourseReviewVerify.castVerdict(Integer.parseInt(event.getComponentId().split(" - ")[1]), 0);
            } else if (event.getComponentId().contains("Accept")) {
                CourseReviewVerify.castVerdict(Integer.parseInt(event.getComponentId().split(" - ")[1]), 1);
            }
            event.getMessage().delete().queue();
        } else {
            event.reply("You are not supposed to press this button").setEphemeral(true).queue();
        }
    }
}
