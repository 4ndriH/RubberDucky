package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.BotExceptions;
import services.Miscellaneous;

import java.awt.*;
import java.io.File;

public class Nuke implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(Nuke.class);
    private final EmbedBuilder nukeIncoming = new EmbedBuilder();

    public Nuke(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
        embedSetUp();
    }

    @Override
    public void handle(CommandContext ctx) {
        Miscellaneous.CommandLog(getName(), ctx, true);

        new Thread(() -> {
            long nr = 1;

            if (ctx.getMessage().getReferencedMessage() != null) {
                String id = ctx.getMessage().getReferencedMessage().getId();
                nr += ctx.getChannel().getIterableHistory().stream().takeWhile(msg -> !msg.getId().equals(id)).count();
            } else {
                try {
                    nr += Integer.parseInt(ctx.getArguments().get(0));
                } catch (Exception e) {
                    BotExceptions.invalidArgumentsException(ctx);
                    return;
                }
            }

            ctx.getMessage().delete().queue();
            ctx.getChannel().sendMessageEmbeds(nukeIncoming.build())
                    .addFile(new File("resources/nuke.gif")).complete();

            try {
                Thread.sleep(2048);
            } catch (Exception ignored) {}

            ctx.getChannel().getIterableHistory().takeAsync((int)nr).thenAccept(ctx.getChannel()::purgeMessages);
        }).start();
    }

    private void embedSetUp() {
        nukeIncoming.setTitle("**TACTICAL NUKE INCOMING**");
        nukeIncoming.setColor(new Color(0xb074ad));
        nukeIncoming.setImage("attachment://nuke.gif");
    }

    @Override
    public String getName() {
        return "Nuke";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Nukes the specified amount of messages, or up to the one replied to");
        return embed;
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }
}
