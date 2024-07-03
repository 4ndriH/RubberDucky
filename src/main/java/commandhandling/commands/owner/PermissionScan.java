package commandhandling.commands.owner;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class PermissionScan implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        Guild currentGuild = ctx.getGuild();
        Member currentMember = currentGuild.getMember(ctx.getSelfUser());
        StringBuilder sb = new StringBuilder();

        for (GuildChannel c : currentGuild.getChannels()) {
            sb.append("## ").append(c.getName()).append(" ##\n");

            for (Permission p : currentMember.getPermissions(c)) {
                sb.append("| ").append(p.getName()).append("\n");
            }

            sb.append("----------------\n\n");
        }

        InputStream is = new ByteArrayInputStream(sb.toString().getBytes());

        ctx.getAuthor().openPrivateChannel().queue(channel -> {
            channel.sendMessage("Permission scan complete").queue();
            channel.sendFiles(FileUpload.fromData(is, "permissions.txt")).queue();
        });
    }

    @Override
    public String getName() {
        return "PermissionScan";
    }

    @Override
    public EmbedBuilder getHelp() {
        return null;
    }
}
