package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.Miscellaneous;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PurgeDMs implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(PurgeDMs.class);
    private final EmbedBuilder purgeCommenced = new EmbedBuilder();
    private final EmbedBuilder purgeEnded = new EmbedBuilder();

    public PurgeDMs(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
        embedSetUp();
    }

    @Override
    public void handle(CommandContext ctx) {
        Miscellaneous.CommandLog(getName(), ctx, true);

        (new Thread(() -> {
            final String[] id = new String[1];
            ctx.getAuthor().openPrivateChannel().queue(
                    channel -> id[0] = channel.getId()
            );

            while (id[0] == null) {
                try {
                    Thread.sleep(32);
                } catch (Exception ignored) {}
            }

            PrivateChannel channel = ctx.getJDA().getPrivateChannelById(id[0]);
            channel.sendMessageEmbeds(purgeCommenced.build())
                    .addFile(new File("resources/purge/purgeCommenced.jpg"))
                    .queueAfter(1, TimeUnit.SECONDS);
            List<Message> messages;
            do {
                messages = channel.getHistory().retrievePast(64).complete();
                for (int i = messages.size() - 1; i >= 0; i--) {
                    if (!messages.get(i).getAuthor().getId().equals(ctx.getSelfUser().getId())) {
                        messages.remove(i);
                        continue;
                    }
                    messages.get(i).delete().complete();
                    try {
                        Thread.sleep(1024);
                    } catch (Exception ignored) {}
                }
            } while(messages.size() != 0);

            Message msg = channel.sendMessageEmbeds(purgeEnded.build())
                    .addFile(new File("resources/purge/purgeEnded.jpg")).complete();
            msg.delete().queueAfter(32, TimeUnit.SECONDS);
        })).start();
    }

    private void embedSetUp() {
        purgeCommenced.setTitle("Happy purging");
        purgeCommenced.setColor(new Color(0xb074ad));
        purgeCommenced.setImage("attachment://purgeCommenced.jpg");
        purgeEnded.setTitle("Thank you for participating in the purge <3");
        purgeEnded.setColor(new Color(0xb074ad));
        purgeEnded.setImage("attachment://purgeEnded.jpg");
    }

    @Override
    public String getName() {
        return "PurgeDMs";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Deletes all DMs you and me have ;)");
        return embed;
    }
}
