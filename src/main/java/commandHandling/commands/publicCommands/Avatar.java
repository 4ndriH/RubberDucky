//package commandHandling.commands.publicCommands;
//
//import commandHandling.CommandContext;
//import commandHandling.CommandInterface;
//import net.dv8tion.jda.api.EmbedBuilder;
//import net.dv8tion.jda.api.entities.Member;
//import net.dv8tion.jda.api.entities.User;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import services.BotExceptions;
//import services.discordHelpers.EmbedHelper;
//
//import static services.discordHelpers.ReactionHelper.addReaction;
//
//public class Avatar implements CommandInterface {
//    private final Logger LOGGER = LoggerFactory.getLogger(Avatar.class);
//
//    public Avatar(Logger cmdManagerLogger) {
//        cmdManagerLogger.info("Loaded Command " + getName());
//    }
//
//    @Override
//    public void handle(CommandContext ctx) {
//        EmbedBuilder embed = EmbedHelper.embedBuilder();
//        User user = null;
//        Member member = null;
//
//        if (ctx.getArguments().size() > 0) {
//            if (ctx.getMessage().getMentionedUsers().size() > 0) {
//                member = ctx.getMessage().getMentionedMembers().get(0);
//                user = ctx.getMessage().getMentionedUsers().get(0);
//            } else {
//                String id = ctx.getArguments().get(0).replace("<@!", "")
//                        .replace(">", "");
//                member = ctx.getGuild().getMemberById(id);
//                user = ctx.getGuild().getJDA().getUserById(id);
//            }
//        } else {
//            user = ctx.getAuthor();
//        }
//
//        if (member != null) {
//            embed.setTitle((member.getNickname() != null ? member.getNickname() : user.getName()) + "s avatar");
//            embed.setImage(member.getEffectiveAvatarUrl() + "?size=512");
//        } else if (user != null) {
//            embed.setTitle(user.getName() + "s avatar");
//            embed.setImage(user.getEffectiveAvatarUrl() + "?size=512");
//        } else {
//            BotExceptions.invalidArgumentsException(ctx);
//            return;
//        }
//
//        addReaction(ctx, 0);
//        EmbedHelper.sendEmbed(ctx, embed, 32);
//    }
//
//    @Override
//    public String getName() {
//        return "Avatar";
//    }
//
//    @Override
//    public EmbedBuilder getHelp() {
//        return null;
//    }
//}
