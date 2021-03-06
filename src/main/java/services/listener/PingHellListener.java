package services.listener;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Random;

import static services.database.DBHandlerPingHell.*;

public class PingHellListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(PingHellListener.class);
    private final Random random = new Random();
    private Role formerPingHellMember;
    private Role pingHell;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        formerPingHellMember = event.getJDA().getGuildById("817850050013036605").getRoleById("997211963002191942");
        pingHell = event.getJDA().getGuildById("817850050013036605").getRoleById("991687045644824679");
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().getId().equals("774276700557148170") && event.getMessage().getContentRaw().contains("PingHell") && event.getMessage().getContentRaw().contains("<@")) {
            event.getJDA().getGuildById("817850050013036605").getTextChannelById("997215232562827274").sendMessage(event.getMessage().getContentRaw().replace("@", "")).queue();
        } else if (event.getChannel().getId().equals("997215232562827274") && event.getAuthor().getId().equals("817846061347242026")) {
            String discordUserId = event.getMessage().getContentRaw().replaceAll("\\D", "");

            if (event.getMessage().getContentRaw().endsWith("welcome to PingHell!")) {
                if (!isInPinghellHQ(discordUserId)) {
                    addPingHellMember(discordUserId);
                }

                if (isServerMember(discordUserId)) {
                    event.getGuild().addRoleToMember(UserSnowflake.fromId(discordUserId), pingHell).complete();
                    event.getGuild().removeRoleFromMember(UserSnowflake.fromId(discordUserId), formerPingHellMember).complete();
                } else {
                    String inviteLink = event.getJDA().getGuildById("817850050013036605").getTextChannelById("991686525651800175")
                            .createInvite().setMaxAge(1800).setMaxUses(1).complete().getUrl();

                    event.getJDA().openPrivateChannelById(discordUserId).complete().sendMessage(inviteLink).complete();
                    event.getJDA().openPrivateChannelById(discordUserId).complete().sendMessage("Welcome to Pinghell! Feel free to join the Pinghell HQ with this link").queue();
                }

                updatePinghellStatus(discordUserId, 1);
            } else if(event.getMessage().getContentRaw().endsWith("finally escaped PingHell. May you never ping it ever again.")) {
                if (isServerMember(discordUserId)) {
                    event.getGuild().addRoleToMember(UserSnowflake.fromId(discordUserId), formerPingHellMember).complete();
                    event.getGuild().removeRoleFromMember(UserSnowflake.fromId(discordUserId), pingHell).complete();
                    updatePinghellStatus(discordUserId, 0);
                }
            }

            event.getMessage().delete().queue();
        } else if (event.getAuthor().getId().equals("774276700557148170") && event.getMessage().getContentRaw().contains("Message graph for last day")) {
            pingHell.getManager().setColor(new Color(random.nextFloat(), random.nextFloat(), random.nextFloat())).queue();
        }
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        if (event.getGuild().getId().equals("817850050013036605")) {
            if (isInPinghell(event.getUser().getId())) {
                event.getGuild().addRoleToMember(event.getMember(), pingHell).complete();

                updatePinghellStatus(event.getUser().getId(), 1);
                updateServerMemberStatus(event.getUser().getId(), 1);
            }
        }
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        if (isInPinghellHQ(event.getUser().getId())) {
            updateServerMemberStatus(event.getUser().getId(), 0);
        }
    }
}
