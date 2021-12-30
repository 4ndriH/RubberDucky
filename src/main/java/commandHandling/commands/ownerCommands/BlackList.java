package commandHandling.commands.ownerCommands;

import commandHandling.CommandContext;
import commandHandling.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.Miscellaneous;
import services.PermissionManager;
import services.database.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class BlackList implements CommandInterface {
    private final Logger LOGGER = LoggerFactory.getLogger(BlackList.class);

    public BlackList(Logger cmdManagerLogger) {
        cmdManagerLogger.info("Loaded Command " + getName());
    }

    @Override
    public void handle(CommandContext ctx) {
        Miscellaneous.CommandLog(getName(), ctx, true);

        if (ctx.getArguments().size() > 0) {
            String id = ctx.getArguments().get(0);
            if (PermissionManager.blackList.contains(id)) {
                DatabaseHandler.removeBlacklist(id);
            } else {
                DatabaseHandler.insertBlacklist(id);
            }
            PermissionManager.reload();
        } else {
            ArrayList<String> ids = PermissionManager.blackList;
            EmbedBuilder embed = Miscellaneous.embedBuilder("Blacklisted people");

            if (ids.size() == 0) {
                embed.setDescription("-");
            } else {
                StringBuilder sb = new StringBuilder();
                for (String id : ids) {
                    sb.append("<@!").append(id).append(">\n");
                }
                Message msg = ctx.getChannel().sendMessage("beep boop").complete();
                msg.editMessage(sb.toString()).complete();
                msg.delete().queue();
                embed.setDescription(sb.toString());
            }

            ctx.getChannel().sendMessageEmbeds(embed.build()).queue(
                    msg -> Miscellaneous.deleteMsg(msg, 32)
            );
        }
    }

    @Override
    public String getName() {
        return "Blacklist";
    }

    @Override
    public EmbedBuilder getHelp() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Allows me to blacklist your annoying ass");
        return embed;
    }

    @Override
    public List<String> getAliases() {
        return List.of("bl");
    }

    @Override
    public int getRestrictionLevel() {
        return 2;
    }
}