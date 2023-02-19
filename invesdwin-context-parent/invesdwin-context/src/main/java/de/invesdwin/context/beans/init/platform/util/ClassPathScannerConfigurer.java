package de.invesdwin.context.beans.init.platform.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.concurrent.Immutable;

import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.util.classpath.ClassPathScanner;
import de.invesdwin.util.classpath.FastClassPathScanner;

@Immutable
public final class ClassPathScannerConfigurer {

    private ClassPathScannerConfigurer() {}

    public static void configure() {
        //filter out test classes to prevent issues with class not found or resource not found in production
        FastClassPathScanner.addBlacklistPath("de/invesdwin/*Test");
        FastClassPathScanner.addBlacklistPath("de/invesdwin/*Stub");
        FastClassPathScanner.addBlacklistPath("de/invesdwin/*/test/*");
        for (final String basePackage : ContextProperties.getBasePackages()) {
            FastClassPathScanner.addWhitelistPath(basePackage.replace(".", "/"));
        }

        final List<TypeFilter> defaultExcludeFilters = new ArrayList<TypeFilter>();
        //filter out test classes to prevent issues with class not found or resource not found in production
        defaultExcludeFilters.add(new RegexPatternTypeFilter(Pattern.compile("de\\.invesdwin\\..*(Test|Stub)")));
        defaultExcludeFilters.add(new RegexPatternTypeFilter(Pattern.compile("de\\.invesdwin\\..*\\.test\\..*")));
        ClassPathScanner.setDefaultExcludeFilters(defaultExcludeFilters);
    }

}
