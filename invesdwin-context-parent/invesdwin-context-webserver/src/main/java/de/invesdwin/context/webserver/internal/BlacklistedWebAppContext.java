package de.invesdwin.context.webserver.internal;

import java.net.MalformedURLException;

import javax.annotation.concurrent.NotThreadSafe;

import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.integration.IntegrationProperties;
import de.invesdwin.context.log.Log;
import de.invesdwin.util.lang.Strings;

/**
 * Works around the Jetty limitation, that it only loads web-fragments from WEB-INF/lib/*.jar.
 * 
 * @see <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=330189">Source</a>
 * 
 * @author subes
 * 
 */
@NotThreadSafe
public class BlacklistedWebAppContext extends WebAppContext {

    private static final String[] CLASSPATH_RESOURCE_BLACKLIST = { "META-INF", "WEB-INF", ".class", ".java",
            ".properties", ".xml" };
    private final Log log = new Log(this);

    @Override
    public Resource getResource(final String uriInContext) throws MalformedURLException {
        final Resource cpResource = Resource.newClassPathResource(uriInContext);
        if (cpResource != null && cpResource.exists()) {
            for (final String blacklist : CLASSPATH_RESOURCE_BLACKLIST) {
                if (Strings.containsIgnoreCase(uriInContext, blacklist)) {
                    log.warn("Ignoring resource path [%s] because it matches blacklist entry [%s]", uriInContext,
                            blacklist);
                    return null;
                }
            }
            return cpResource;
        } else {
            return super.getResource(uriInContext);
        }
    }

    @Override
    protected void startContext() throws Exception {
        logServerStarting();
        super.startContext();
    }

    private void logServerStarting() {
        log.info("Starting webserver at: %s", IntegrationProperties.WEBSERVER_BIND_URI);
        if (ContextProperties.IS_TEST_ENVIRONMENT) {
            IntegrationProperties.setWebserverTest(true);
        }
    }

}
