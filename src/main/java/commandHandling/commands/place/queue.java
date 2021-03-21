package commandHandling.commands.place;

import commandHandling.CommandContext;
import services.BotExceptions;

public class queue {
    private final CommandContext ctx;

    public queue(CommandContext ctx) {
        this.ctx = ctx;
        queueing();
    }

    private void queueing () {
        try {
            ctx.getMessage().getAttachments().get(0).downloadToFile("src/tempFiles/RDdraw.txt");
        } catch (Exception e) {
            try {
                ctx.getMessage().getReferencedMessage().getAttachments().get(0)
                        .downloadToFile("src/tempFiles/RDdraw.txt");
            } catch (Exception ee) {
                BotExceptions.missingAttachmentException(ctx);
            }
        }
    }
}
