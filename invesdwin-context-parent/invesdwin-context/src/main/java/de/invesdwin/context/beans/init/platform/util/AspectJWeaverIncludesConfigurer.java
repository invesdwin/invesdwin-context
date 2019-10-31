package de.invesdwin.context.beans.init.platform.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.beans.init.platform.util.internal.BasePackagesConfigurer;
import de.invesdwin.context.log.error.Err;
import de.invesdwin.util.lang.Files;

@ThreadSafe
public final class AspectJWeaverIncludesConfigurer {

    @GuardedBy("AspectJWeaverIncludesConfigurer.class")
    private static File alreadyGenerated;

    private AspectJWeaverIncludesConfigurer() {}

    public static synchronized void configure() {
        try {
            if (alreadyGenerated == null || !alreadyGenerated.exists()) {
                final StringBuilder includes = new StringBuilder();
                final Set<String> basePackages = BasePackagesConfigurer.getBasePackages();
                for (final String basePackage : basePackages) {
                    if (includes.length() > 0) {
                        includes.append("\n");
                    }
                    //<include within="de.invesdwin..*"/>
                    includes.append("\t\t<include within=\"");
                    includes.append(basePackage);
                    includes.append("..*\"/>");
                }
                String template;
                try (InputStream in = new ClassPathResource("/META-INF/template.weaver.aop.xml").getInputStream()) {
                    template = IOUtils.toString(in, Charset.defaultCharset());
                }
                template = template.replace("<!--INCLUDES-->", includes);
                final File file = new File(ContextProperties.TEMP_CLASSPATH_DIRECTORY, "META-INF/aop.xml");
                Files.writeStringToFile(file, template, Charset.defaultCharset());
                alreadyGenerated = file;
            }
        } catch (final IOException e) {
            throw Err.process(e);
        }
    }
}
