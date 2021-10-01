package services.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.LayoutBase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;


public class CustomFileAppenderLayout extends LayoutBase<ILoggingEvent> {
    private static final DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");

    public CustomFileAppenderLayout() {
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        StringBuffer sb = new StringBuffer();
        String[] arr = event.getLoggerName().split("\\.");

        sb.append(formatter.format(event.getTimeStamp()));
        sb.append(String.format(" %-34.43s", event.getThreadName()));
        sb.append(String.format(" %-15.15s", arr[arr.length - 1]));

        if (event.getLoggerName().equals("Command Logger")) {
            sb.append(String.format(" %-6.6s ", "CMD"));
        } else {
            sb.append(String.format(" %-6.6s ", event.getLevel()));
        }

        sb.append(event.getFormattedMessage()).append("\n");

        IThrowableProxy throwableProxy = event.getThrowableProxy();
        if (throwableProxy != null) {
            sb.append(ThrowableProxyUtil.asString(throwableProxy));
        }

        return sb.toString();
    }
}
