package com.laboschcst.server.logging.appender;

import com.laboschcst.server.logging.LoggingHelper;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Plugin(name = "LocaldevConsoleAppender", category = "Core", elementType = "appender", printObject = true)
public class LocaldevConsoleAppender extends AbstractAppender {

    private final DateTimeFormatter dateTimeFormatter;

    private boolean enableAppender = false;

    private LocaldevConsoleAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);

        dateTimeFormatter = DateTimeFormatter.ofPattern("dd HH:mm:ss.SSS");

        try {
            this.setEnabled(Boolean.parseBoolean(System.getenv("LOGGING_ENABLE_LOCALDEV_CONSOLE_APPENDER")));
        } catch (Exception e) {
            this.setEnabled(false);
            System.out.println("Error reading and parsing value from env var: LOGGING_ENABLE_LOCALDEV_CONSOLE_APPENDER");
            e.printStackTrace();
        }
    }

    @Override
    public void append(LogEvent logEvent) {
        if(!this.isEnabled())
            return;

        Instant javaTimeInstant = Instant.ofEpochMilli(logEvent.getInstant().getEpochMillisecond());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(javaTimeInstant, ZoneId.systemDefault());

        String timeCodeString = dateTimeFormatter.format(localDateTime);

        StringBuilder logBuilder = new StringBuilder();

        logBuilder
            .append("[")
            .append(timeCodeString)
            .append("] [")
            .append(logEvent.getLevel())
            .append("] [")
            .append(logEvent.getThreadName())
            .append(",")
            .append(logEvent.getThreadId())
            .append(",")
            .append(logEvent.getThreadPriority())
            .append("] [");
        appendFormattedLoggerName(logBuilder, logEvent.getLoggerName());
        logBuilder.append("] ");


        if (logEvent.getMarker() != null) {
            logBuilder.append("[")
                .append(logEvent.getMarker())
                .append("] ");
        }

        logBuilder.append("> ")
            .append(logEvent.getMessage().toString())
            .append(" ");

        if (logEvent.getThrown() != null) {
            logBuilder
                .append(">>>>>>>>>>>>>>\n")
                .append(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n")
                .append(">>>>>>>>>>>>>>>> A Thrown is present in the LogEvent at: " + timeCodeString + " >>>>>>>>>>>>>>>>\n\n")
                .append(LoggingHelper.getStackTraceAsString(logEvent.getThrown()))
                .append("\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ");
        }

        System.out.println(logBuilder.toString());
    }

    private void appendFormattedLoggerName(StringBuilder stringBuilder, String loggerName) {
        String[] nameParts = loggerName.split("\\.");

        int i = 0;
        for (; i < nameParts.length - 2; ++i) {
            stringBuilder.append(nameParts[i].charAt(0));

            if (nameParts[i].length() >= 2)
                stringBuilder.append(nameParts[i].charAt(1));

            if (nameParts[i].length() >= 3)
                stringBuilder.append(nameParts[i].charAt(2));

            stringBuilder.append(".");
        }

        for (; i < nameParts.length - 1; ++i) {
            stringBuilder
                .append(nameParts[i])
                .append(".");
        }

        stringBuilder
            .append(nameParts[i]);
    }

    public boolean isEnabled() {
        return enableAppender;
    }

    public void setEnabled(boolean enabled) {
        enableAppender = enabled;
        System.out.println("Logging Setup->" + this.getClass().getSimpleName() + "::isEnabled: " + this.isEnabled());
    }

    @PluginFactory
    public static LocaldevConsoleAppender createAppender(
        @PluginAttribute("name") String name,
        @PluginElement("Layout") Layout<? extends Serializable> layout,
        @PluginElement("Filter") final Filter filter,
        @PluginAttribute("otherAttribute") String otherAttribute) {
        if (name == null) {
            LOGGER.error("No name provided for LocaldevConsoleAppender");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();//A layout has to be provided to instantiate the appender
        }
        return new LocaldevConsoleAppender(name, filter, layout, false, new Property[0]);
    }
}
