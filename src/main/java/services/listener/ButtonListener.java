package services.listener;

import commandHandling.commands.ownerCommands.Kill;
import commandHandling.commands.publicCommands.CourseReview.CourseReview;
import commandHandling.commands.publicCommands.CourseReview.CourseReviewVerify;
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
        } else if(event.getComponentId().startsWith("$cfv")) {
            if (event.getComponentId().contains("Reject")) {
                CourseReviewVerify.castVerdict(Integer.parseInt(event.getComponentId().split(" - ")[1]), -1);
            } else if (event.getComponentId().contains("Quit")) {
                CourseReviewVerify.castVerdict(Integer.parseInt(event.getComponentId().split(" - ")[1]), 0);
            } else if (event.getComponentId().contains("Accept")) {
                CourseReviewVerify.castVerdict(Integer.parseInt(event.getComponentId().split(" - ")[1]), 1);
            }
            event.getMessage().delete().queue();
        } else if (event.getComponentId().endsWith(event.getUser().getId())) {
            if (event.getComponentId().startsWith("cf")) {
                if (event.getComponentId().contains("Proceed")) {
                    CourseReview.processProceed(event.getComponentId().split(" - ")[1]);
                } else if (event.getComponentId().contains("Abort")) {
                    CourseReview.processAbort(event.getComponentId().split(" - ")[1]);
                }
                event.getMessage().delete().queue();
            }
        } else {
            event.reply("You are not supposed to press this button").setEphemeral(true).queue();
        }
    }
}
