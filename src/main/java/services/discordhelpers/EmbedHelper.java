package services.discordhelpers;

import commandhandling.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import assets.Config;

import java.io.File;
import java.util.EnumSet;

import static services.discordhelpers.MessageDeleteHelper.deleteMsg;

public class EmbedHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmbedHelper.class);

    public static EmbedBuilder embedBuilder(String title) {
        return embedBuilder().setTitle(title);
    }

    public static EmbedBuilder embedBuilder() {
        return (new EmbedBuilder()).setColor(Config.embedColor);
    }

    public static void sendEmbed(CommandContext ctx, EmbedBuilder embed, int secondsDelete) {
        sendEmbed(ctx, embed, secondsDelete, false, "", "");
    }

    public static void sendEmbedWithFile(CommandContext ctx, EmbedBuilder embed, int secondsDelete, String path, String name) {
        sendEmbed(ctx, embed, secondsDelete, true, path, name);
    }

    private static void sendEmbed(CommandContext ctx, EmbedBuilder embed, int seconds, boolean file, String path, String name) {
        int secondsUntilDeletion = (ctx.getPersist() ? -1 : seconds);
        try {
            if (file) {
                ctx.getChannel().sendMessageEmbeds(embed.build()).addFiles(FileUpload.fromData(new File(path), name)).queue(
                        msg -> deleteMsg(msg, secondsUntilDeletion)
                );
            } else {
                ctx.getChannel().sendMessageEmbeds(embed.build()).queue(
                        msg -> deleteMsg(msg, secondsUntilDeletion)
                );
            }
        } catch (InsufficientPermissionException ipe) {
            EnumSet<Permission> channelPermissions = ctx.getSelfMember().getPermissionsExplicit(ctx.getChannel().asGuildMessageChannel());
            StringBuilder sb = new StringBuilder();
            Permission[] requiredPermissions = new Permission[]{
                    Permission.MESSAGE_SEND,
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
}
