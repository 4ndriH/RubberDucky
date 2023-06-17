package services.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static services.database.DBHandlerPingHell.*;
import static services.discordHelpers.EmbedHelper.embedBuilder;

public class PingHellListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(PingHellListener.class);
    private final Random random = new Random();
    private Role formerPingHellMember;
    private Role pingHell;

    private float r = 0.0f;
    private float g = 0.0f;
    private float b = 0.0f;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        formerPingHellMember = event.getJDA().getGuildById("817850050013036605").getRoleById("997211963002191942");
        pingHell = event.getJDA().getGuildById("817850050013036605").getRoleById("991687045644824679");
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().getId().equals("774276700557148170") && event.getMessage().getContentRaw().contains("PingHell") && event.getMessage().getContentRaw().contains("<@")) {
            event.getGuild().getTextChannelById(1020951518582673478L).sendMessage(event.getMessage().getContentRaw().replace("@", "")).queue(
                    (msg) -> msg.delete().queueAfter(5, TimeUnit.SECONDS)
            );
        } else if (event.getChannel().getId().equals("1020951518582673478") && event.getAuthor().getId().equals("817846061347242026")) {
            String discordUserId = event.getMessage().getContentRaw().replaceAll("\\D", "");

            if (event.getMessage().getContentRaw().endsWith("welcome to PingHell!")) {
                if (!isInPinghellHQ(discordUserId)) {
                    addPingHellMember(discordUserId);
                }

                if (isServerMember(discordUserId)) {
                    event.getGuild().addRoleToMember(UserSnowflake.fromId(discordUserId), pingHell).complete();
                    event.getGuild().removeRoleFromMember(UserSnowflake.fromId(discordUserId), formerPingHellMember).complete();
                } else {
                    try {
                        if (event.getJDA().getUserById(discordUserId).isBot()) {
                            return;
                        }
                    } catch (Exception ignored) {}

                    String inviteLink = event.getGuild().getTextChannelById(991686525651800175L).createInvite().setMaxAge(1800).setMaxUses(1).complete().getUrl();

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
        } else if (event.getAuthor().getId().equals("774276700557148170") && event.getMessage().getContentRaw().equalsIgnoreCase("Message graph for last day") || event.getAuthor().getId().equals("155419933998579713") && event.getMessage().getContentRaw().equalsIgnoreCase("Rubberducky, please change the color")) {
            r = (r + random.nextFloat()) % 1.0f;
            g = (g + random.nextFloat()) % 1.0f;
            b = (b + random.nextFloat()) % 1.0f;
            Color color = new Color(r, g, b);

            pingHell.getManager().setColor(color).queue();

            BufferedImage image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);

            for (int i = 0; i < image.getWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    image.setRGB(i, j, color.getRGB());
                }
            }

            EmbedBuilder embed = embedBuilder("Today's Color is #" + Integer.toHexString(color.getRGB()).substring(2));
            embed.setThumbnail("attachment://PingHellColor.png");
            embed.setDescription("Have a lovely day and happy Pinging! \n\n<@&991687045644824679>");

            event.getJDA().getGuildById("817850050013036605").getTextChannelById("991686525651800175")
                    .sendMessageEmbeds(embed.build())
                    .addFiles(FileUpload.fromData(convert(image), "PingHellColor.png")).queue();
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

    private InputStream convert (BufferedImage img) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(os.toByteArray());
    }
}
