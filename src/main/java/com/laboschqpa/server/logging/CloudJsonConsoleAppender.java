package com.laboschqpa.server.logging;

import com.janosgats.logging.flexibleappender.FlexibleAppender;
import com.janosgats.logging.flexibleappender.enableable.CompositeOrEnableable;
import com.janosgats.logging.flexibleappender.enableable.EnvironmentVariableEnableable;
import com.janosgats.logging.flexibleappender.enableable.SystemPropertyEnableable;
import com.janosgats.logging.flexibleappender.loglinebuilder.AbstractLogLineBuilder;
import com.janosgats.logging.flexibleappender.loglinebuilder.specific.JsonLogLineBuilder;
import com.janosgats.logging.flexibleappender.loglineoutput.AbstractLogLineOutput;
import com.janosgats.logging.flexibleappender.loglineoutput.specific.StdOutLogLineOutput;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Plugin(name = "CloudJsonConsoleAppender", category = "Core", elementType = "appender", printObject = true)
public class CloudJsonConsoleAppender extends FlexibleAppender {

    private CloudJsonConsoleAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC);

        CompositeOrEnableable compositeOrEnableable = new CompositeOrEnableable();
        compositeOrEnableable.getAbstractEnableables().add(new EnvironmentVariableEnableable("LOGGING_ENABLE_CLOUD_JSON_CONSOLE_APPENDER"));
        compositeOrEnableable.getAbstractEnableables().add(new SystemPropertyEnableable("LOGGING_ENABLE_CLOUD_JSON_CONSOLE_APPENDER"));

        AbstractLogLineBuilder logLineBuilder = new JsonLogLineBuilder(dateTimeFormatter);

        AbstractLogLineOutput logLineOutput = new StdOutLogLineOutput();

        super.setUpAppender(compositeOrEnableable, logLineBuilder, logLineOutput);
    }

    @PluginFactory
    public static CloudJsonConsoleAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter,
            @PluginAttribute("otherAttribute") String otherAttribute) {
        if (name == null) {
            System.out.println("No name provided for CloudJsonConsoleAppender");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();//A layout has to be provided to instantiate the appender
        }
        return new CloudJsonConsoleAppender(name, filter, layout, false, new Property[0]);
    }
}