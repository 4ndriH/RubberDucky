package commandHandling.commands.ownerCommands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static services.discordHelpers.MessageDeleteHelper.deleteMsg;

public class Say implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(Say.class);
    private volatile HashMap<String, Boolean> sayChannels = new HashMap<>();

    public Say(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        StringBuilder sb = new StringBuilder();
        String channel = ctx.getChannel().getId();
        int repeats, i;

        try {
            repeats = Integer.parseInt(ctx.getArguments().get(0));
            i = 1;
        } catch (Exception e) {
            repeats = 1;
            i = 0;
        }

        if (ctx.getArguments().size() == 0) {
            sayChannels.replace(channel, false);
            return;
        } else if (ctx.getArguments().get(0).equalsIgnoreCase("stopAll")) {
            for (String key : sayChannels.keySet()) {
                sayChannels.replace(key, false);
            }
            return;
        }

        for (; i < ctx.getArguments().size(); i++) {
            if (ctx.getArguments().get(i).equals("-up")) {
                String ping = ctx.getArguments().get(++i);
                sb.append("<@").append(ping).append("> ");
            } else if (ctx.getArguments().get(i).equals("-rp")) {
                String ping = ctx.getArguments().get(++i);
                sb.append("<@&").append(ping).append("> ");
            } else {
                sb.append(ctx.getArguments().get(i)).append(" ");
            }
        }

        deleteMsg(ctx.getMessage(), 0);

        if (!sayChannels.containsKey(channel)) {
            sayChannels.put(channel, true);
        }

        int finalRepeats = repeats;
        (new Thread(() -> {
            for (int j = 0; j < finalRepeats && sayChannels.get(channel); j++) {
                ctx.getChannel().sendMessage(sb.toString()).complete();
            }
            sayChannels.remove(channel);
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
        embed.addField("__Additional functionality__", "For the bot to add a user ping do" +
                "```-up <User ID>``` \n for the bot to add a role ping do" +
                "```-rp <Role ID>```", false);
        return embed;
    }

    @Override
    public int getRestrictionLevel() {
        return 0;
    }
}
