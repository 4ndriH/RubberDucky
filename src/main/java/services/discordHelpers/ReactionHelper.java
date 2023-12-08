package services.discordHelpers;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import assets.EMOTES;
import services.PermissionManager;

import static services.database.DBHandlerBlacklistedUsers.addUserToBlacklist;

public class ReactionHelper {

    // ---------------------------------------------------------
    // PermissionStatus
    // 0 - Success
    // 1 - Blacklisted
    // 2 - Channel not whitelisted
    // 3 - Server not whitelisted
    // 4 - Security Clearance not high enough
    // 5 - Error
    // ---------------------------------------------------------

    public static void addReaction(CommandContext ctx, int type) {
        String reaction;

        switch (type) {
            case 0 -> reaction = EMOTES.RDSuccess.getAsReaction();
            case 1 -> reaction = EMOTES.RDBlacklisted.getAsReaction();
            case 2 -> reaction = EMOTES.RDChannelWhitelist.getAsReaction();
            case 3 -> reaction = EMOTES.RDServerWhitelist.getAsReaction();
            case 4 -> reaction = EMOTES.RDLackingPermissions.getAsReaction();
            case 5 -> reaction = EMOTES.RDError.getAsReaction();
            default -> {return;}
        }

        reactionAdder(ctx.getMessage(), reaction);
    }

    public static void addReaction(Message msg, String reaction) {
        reactionAdder(msg, reaction);
    }

    private static void reactionAdder(Message message, String reaction) {
        message.addReaction(Emoji.fromFormatted(reaction)).queue(
                null, new ErrorHandler().handle(ErrorResponse.REACTION_BLOCKED,
                        (ex) -> {
                            addUserToBlacklist(message.getAuthor().getId());
                            PermissionManager.reload();
                        }).ignore(ErrorResponse.UNKNOWN_MESSAGE)
        );
    }
}
