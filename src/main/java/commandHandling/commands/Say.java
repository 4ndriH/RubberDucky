package commandHandling.commands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import resources.CONFIG;
import services.BotExceptions;
import services.CoolDownManager;
import services.DiscordLogger;

import java.util.HashMap;

public class Say implements CommandInterface {
    private volatile HashMap<String, HashMap<String, Boolean>> sayState = new HashMap<>();

    public Say(Logger LOGGER) {
        LOGGER.info("Loaded Command Say");
    }

    @Override
    public void handle(CommandContext ctx) {
        StringBuilder sb = new StringBuilder();
        String channel = ctx.getChannel().getId();
        String user = ctx.getAuthor().getId();
        int repeats;

        if (user.equals(CONFIG.OwnerID.get()) && ctx.getArguments().size() == 1) {
            if (ctx.getArguments().get(0).equalsIgnoreCase("stop")) {
                for (String key : sayState.get(channel).keySet()) {
                    sayState.get(channel).replace(key, false);
                }
            } else if (ctx.getArguments().get(0).equalsIgnoreCase("stopAll")) {
                for (String channelKey : sayState.keySet()) {
                    for (String userKey : sayState.get(channelKey).keySet()) {
                        sayState.get(channelKey).replace(userKey, false);
                    }
                }
            }
            DiscordLogger.command(ctx, "say", true);
            return;
        }

        try {
            if (user.equals(CONFIG.OwnerID.get())){
                repeats = Integer.parseInt(ctx.getArguments().get(0));
                for (int i = 1; i < ctx.getArguments().size(); i++) {
                    sb.append(ctx.getArguments().get(i)).append(" ");
                }
            } else {
                repeats = Math.min(32, Integer.parseInt(ctx.getArguments().get(0)));
                for (int i = 1; i < ctx.getArguments().size(); i++) {
                    String temp = ctx.getArguments().get(i);

                    if (temp.startsWith("<@!")) {
                        temp = ctx.getJDA().retrieveUserById(temp.substring(3, temp.length() - 1)).complete().getName();
                    } else if (temp.startsWith("<@&")) {
                        temp = ctx.getGuild().getRoleById(temp.substring(3, temp.length() - 1)).getName();
                    } else if (temp.contains("@everyone") || temp.contains("@here")) {
                        temp = "<@!" + ctx.getAuthor().getId() + ">";
                    }

                    sb.append(temp).append(" ");
                }
            }

            CoolDownManager.addCoolDown("say", ctx.getAuthor().getId(), 3600);
            DiscordLogger.command(ctx, "say", true);
        } catch (Exception e) {
            e.printStackTrace();
            DiscordLogger.command(ctx, "say", false);
            BotExceptions.invalidArgumentsException(ctx);
            return;
        }

        if (!sayState.containsKey(channel)) {
            sayState.put(channel, new HashMap<>());
        }
        sayState.get(channel).put(user, true);

        (new Thread(() -> {
            for (int i = 0; i < repeats && sayState.get(channel).get(user); i++) {
                ctx.getChannel().sendMessage(sb.toString()).complete();
            }
            sayState.get(channel).remove(user);
        })).start();
    }

    @Override
    public String getName() {
        return "Say";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Repeats a message a certain amount of times");
        embed.addField("__How To__", "```" + CONFIG.Prefix.get() +
                "say <amount> <message>```", false);
        embed.addField("__Limitations__", "Your message will be sent at most 32 times and " +
                "there is a 1 hour cool down", false);
        return embed;
    }
}
