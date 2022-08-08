package de.invesdwin.context.webserver.internal;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.core.io.Resource;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.beans.init.PreMergedContext;
import de.invesdwin.context.integration.IntegrationProperties;
import de.invesdwin.context.log.Log;
import de.invesdwin.maven.plugin.util.AWebFragmentConfigurationMerger;
import de.invesdwin.maven.plugin.util.WebFragmentResource;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.Strings;

@NotThreadSafe
public class WebFragmentConfigurationMerger extends AWebFragmentConfigurationMerger {

    private static volatile File alreadyGenerated;
    private final Log log = new Log(this);

    @Override
    protected void logWebFragmentsBeingLoaded(final List<WebFragmentResource> webFragments) {
        if (log.isInfoEnabled()) {
            String webFragmentSingularPlural = "web-fragment";
            final List<String> locationStrings = extractWebFragmentNames(webFragments);
            if (locationStrings.size() != 1) {
                webFragmentSingularPlural += "s";
            }
            log.info("Loading " + locationStrings.size() + " " + webFragmentSingularPlural + " " + locationStrings);
        }
    }

    private List<String> extractWebFragmentNames(final List<WebFragmentResource> webFragments) {
        final List<String> names = new ArrayList<String>();
        for (final WebFragmentResource r : webFragments) {
            names.addAll(r.getAllWebFragmentNames());
        }
        return names;
    }

    public String getContextPath() {
        final String contextPath = IntegrationProperties.WEBSERVER_BIND_URI.getPath();
        if (Strings.isBlank(contextPath)) {
            return "/";
        }
        return Strings.putPrefix(contextPath, "/");
    }

    public String getResourceBase() throws IOException {
        if (alreadyGenerated == null || !alreadyGenerated.exists()) {
            final File webappDirectory = new File(ContextProperties.TEMP_DIRECTORY, "webapp");
            final File webinfDirectory = new File(webappDirectory, "WEB-INF");
            final File webFragmentFile = new File(webinfDirectory, "web.xml");
            final String merged = mergeConfigs();
            Files.forceMkdir(webinfDirectory);
            Files.writeStringToFile(webFragmentFile, merged, Charset.defaultCharset());
            WebFragmentConfigurationMerger.alreadyGenerated = webappDirectory;
        }
        return alreadyGenerated.getAbsolutePath();
    }

    @Override
    protected Iterable<Resource> getResources() throws IOException {
        return Arrays.asList(PreMergedContext.getInstance().getResources("classpath*:/META-INF/web/web-fragment.xml"));
    }

}
