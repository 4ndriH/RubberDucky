package commandhandling.commands.owner;

import commandhandling.CommandContext;
import commandhandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.util.HashMap;
import java.util.regex.Pattern;

public class Say implements CommandInterface {
    private static final Pattern argumentPattern = Pattern.compile("^.*\\S+.*\\s?$");
    private final HashMap<MessageChannelUnion, Boolean> sayChannels = new HashMap<>();

    @Override
    public void handle(CommandContext ctx) {
        ctx.getMessage().delete().queue();

        StringBuilder sb = new StringBuilder();
        MessageChannelUnion channel = ctx.getChannel();
        int repeats, i;

        try {
            repeats = Integer.parseInt(ctx.getArguments().get(0));
            i = 1;
        } catch (Exception e) {
            repeats = 1;
            i = 0;
        }

        if (ctx.getArguments().isEmpty()) {
            sayChannels.replace(channel, false);
            return;
        } else if (ctx.getArguments().get(0).equalsIgnoreCase("stopAll")) {
            for (MessageChannelUnion c : sayChannels.keySet()) {
                sayChannels.replace(c, false);
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

        if (!sayChannels.containsKey(channel)) {
            sayChannels.put(channel, true);
        }
        sb.append(" hgfdsdfghjkjhgfdsdfghj");
        for (int j = 0; j < repeats && sayChannels.get(channel); j++) {
            channel.sendMessage(sb.toString()).complete();
        }
        sayChannels.remove(channel);
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
    public boolean argumentCheck(StringBuilder args) {
        return argumentPattern.matcher(args).matches();
    }
}
