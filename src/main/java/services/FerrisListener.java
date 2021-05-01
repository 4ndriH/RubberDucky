package services;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class FerrisListener  extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private final CommandManager manager = new CommandManager();
    private final EmbedBuilder embed = new EmbedBuilder();

    public FerrisListener() {
        embed.setTitle("Pixel War");
        embed.setColor(new Color(0xb074ad));
        embed.setDescription("Yes, Sir!");
        embed.setImage("https://i.pinimg.com/474x/1a/d2/d3/1ad2d30960db1227d27e2498b23fcfce--daisy-duck-disney-pics.jpg");
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        LOGGER.info("Ready to receive commands from General Ferris!");
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        TextChannel ethPlaceBots = event.getJDA().getGuildById(747752542741725244L).getTextChannelById(819966095070330950L);
        User user = event.getAuthor();
        String raw = event.getMessage().getContentRaw();
        FerrisThread ft = null;

        if (user.getId().equals("590453186922545152") && !event.isWebhookMessage()) {
            if (raw.contains("817846061347242026")) {
                if (ft == null) {
                    Thread t = new Thread(ft = new FerrisThread(ethPlaceBots, raw.split("\n")));
                    t.start();
                } else {
                    ft.updateQueue(raw.split("\n"));
                }
                event.getChannel().sendMessage(embed.build()).queue();
            }
        }
    }

    private class FerrisThread implements Runnable {
        private TextChannel ethPlaceBots;
        private final ArrayList<String> commands = new ArrayList<>();

        public FerrisThread(TextChannel ethPlaceBots, String[] commands) {
            this.ethPlaceBots = ethPlaceBots;
            Collections.addAll(this.commands, commands);
        }

        @Override
        public void run() {
            while (true) {
                String command = "";
                synchronized (commands) {
                    if (commands.size() > 0) {
                        command = commands.get(0);
                        commands.remove(0);
                        if (!command.contains("817846061347242026")) {
                            ethPlaceBots.sendMessage(command).queue();
                        }
                    }
                }
                if (command.length() == 0) {
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void updateQueue(String[] commands) {
            synchronized (this.commands) {
                this.commands.addAll(Arrays.asList(commands));
            }
        }
    }
}
