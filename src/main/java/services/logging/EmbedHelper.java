package services.logging;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resources.CONFIG;
import services.database.DBHandlerMessageDeleteTracker;

import java.awt.*;
import java.io.File;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public class EmbedHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmbedHelper.class);

    public static EmbedBuilder embedBuilder(String title) {
        return embedBuilder().setTitle(title);
    }

    public static EmbedBuilder embedBuilder() {
        return (new EmbedBuilder()).setColor(new Color(Integer.decode(CONFIG.embedColor.get())));
    }

    public static void sendEmbed(CommandContext ctx, EmbedBuilder embed, int secondsDelete) {
        sendEmbed(ctx, embed, secondsDelete, false, "", "");
    }

    public static void sendEmbedWithFile(CommandContext ctx, EmbedBuilder embed, int secondsDelete, String path, String name) {
        sendEmbed(ctx, embed, secondsDelete, true, path, name);
    }

    private static void sendEmbed(CommandContext ctx, EmbedBuilder embed, int secsDelete, boolean file, String path, String name) {
        try {
            if (file) {
                ctx.getChannel().sendMessageEmbeds(embed.build()).addFile(new File(path), name).queue(
                        msg -> deleteMsg(msg, secsDelete)
                );
            } else {
                ctx.getChannel().sendMessageEmbeds(embed.build()).queue(
                        msg -> deleteMsg(msg, secsDelete)
                );
            }
        } catch (InsufficientPermissionException ipe) {
            EnumSet<Permission> channelPermissions = ctx.getSelfMember().getPermissionsExplicit(ctx.getChannel());
            StringBuilder sb = new StringBuilder();
            Permission[] requiredPermissions = new Permission[]{
                    Permission.MESSAGE_WRITE,
                    Permission.MESSAGE_ADD_REACTION,
                    Permission.MESSAGE_ATTACH_FILES,
                    Permission.MESSAGE_EMBED_LINKS,
                    Permission.MESSAGE_EXT_EMOJI,
                    Permission.MESSAGE_MANAGE
            };

            for (Permission p : requiredPermissions) {
                if (!channelPermissions.contains(p)) {
                    sb.append(p).append("\n");
                }
            }

            LOGGER.warn("Missing permissions: " + sb.toString().replace("\n", ", "));

            try {
                ctx.getChannel().sendMessage("Missing permissions:\n" + sb).queue();
            } catch (InsufficientPermissionException ipe2) {
                ctx.getAuthor().openPrivateChannel().queue(
                        channel -> channel.sendMessage("Missing permissions:\n" + sb).queue()
                );
            }
        }
    }

    public static void deleteMsg(Message msg, int seconds) {
        try {
            if (seconds >= 0) {
                msg.delete().queueAfter(seconds, TimeUnit.SECONDS, null, failure -> {});
            }
        } catch (Exception ignored) {}

        if (seconds > 0) {
            DBHandlerMessageDeleteTracker.insertDeleteMessage(msg.getGuild().getId(), msg.getChannel().getId(), msg.getId(),
                    System.currentTimeMillis() + seconds * 1000L);
        }
    }
}
