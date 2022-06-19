package services;

import commandHandling.CommandContext;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import resources.EMOTES;

import static services.database.DBHandlerBlacklistedUsers.addUserToBlacklist;

public class ReactionHelper {

    // ---------------------------------------------------------
    // PermissionStatus
    // 0 - Success
    // 1 - Blacklisted
    // 2 - Channel not whitelisted
    // 3 - Server not whitelisted
    // 4 - Security Clearance not high enough
    // ---------------------------------------------------------

    public static void addReaction(CommandContext ctx, int type) {
        String reaction;

        switch (type) {
            case 0:
                reaction = EMOTES.RDSuccess.getAsReaction();
                break;
            case 1:
                reaction = EMOTES.RDBlacklisted.getAsReaction();
                break;
            case 2:
                reaction = EMOTES.RDChannelWhitelist.getAsReaction();
                break;
            case 3:
                reaction = EMOTES.RDServerWhitelist.getAsReaction();
                break;
            case 4:
                reaction = EMOTES.RDLackingPermissions.getAsReaction();
                break;
            case 5:
                reaction = EMOTES.RDError.getAsReaction();
                break;
            default:
                return;
        }

        ctx.getMessage().addReaction(reaction).queue(
                null, new ErrorHandler().handle(ErrorResponse.REACTION_BLOCKED,
                        (ex) -> {
                            addUserToBlacklist(ctx.getAuthor().getId());
                            PermissionManager.reload();
                        })
        );
    }
}
