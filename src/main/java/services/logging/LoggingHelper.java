package services.logging;

import commandHandling.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingHelper {
    private static final Logger cmdLogger = LoggerFactory.getLogger("Command Logger");

    public static void commandLogger(CommandContext ctx) {
        cmdLogger.info(ctx.getAuthor().getAsTag() + " ran command " + ctx.getMessage().getContentRaw());
    }
}
