package de.invesdwin.context.log.log4j2;

import javax.annotation.concurrent.Immutable;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.AbstractFilter;

@Immutable
@Plugin(name = "StdoutExpression", category = Core.CATEGORY_NAME, elementType = Filter.ELEMENT_TYPE, printObject = true)
public final class StdoutExpressionFilter extends AbstractFilter {

    private static final int INFO_LEVEL_INT = Level.INFO.intLevel();

    private StdoutExpressionFilter(final Result onMatch, final Result onMismatch) {
        super(onMatch, onMismatch);
    }

    @PluginFactory
    public static StdoutExpressionFilter createFilter(@PluginAttribute("onMatch") final String match,
            @PluginAttribute("onMismatch") final String mismatch) {

        final Result onMatch = match == null ? Result.NEUTRAL : Result.valueOf(match);
        final Result onMismatch = mismatch == null ? Result.DENY : Result.valueOf(mismatch);

        return new StdoutExpressionFilter(onMatch, onMismatch);
    }

    @Override
    public Result filter(final LogEvent event) {
        final Level level = event.getLevel();
        final int levelInt = level.intLevel();
        // Accept only if level is INFO or below (TRACE, DEBUG, INFO)
        return levelInt >= INFO_LEVEL_INT ? onMatch : onMismatch;
    }

}
