package services.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EventEvaluatorBase;

public class CommandEvaluator extends EventEvaluatorBase<ILoggingEvent> {
    @Override
    public boolean evaluate(ILoggingEvent event) {
        return event.getLoggerName().contains("Command Logger");
    }
}
