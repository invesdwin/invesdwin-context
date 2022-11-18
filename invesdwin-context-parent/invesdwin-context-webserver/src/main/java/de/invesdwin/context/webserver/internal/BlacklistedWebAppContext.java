package de.invesdwin.context.webserver.internal;

import java.net.MalformedURLException;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.beans.init.MergedContext;
import de.invesdwin.context.integration.IntegrationProperties;
import de.invesdwin.context.log.Log;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.lang.string.Strings;
import jakarta.websocket.server.ServerEndpointConfig;

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
        final jakarta.websocket.server.ServerContainer serverContainer = Reflections.staticMethod("initialize")
                .withReturnType(jakarta.websocket.server.ServerContainer.class)
                .withParameterTypes(org.eclipse.jetty.servlet.ServletContextHandler.class)
                .in(org.eclipse.jetty.websocket.jakarta.server.config.JakartaWebSocketServletContainerInitializer.class)
                .invoke(this);
        final Map<String, ServerEndpointConfig> endpointConfigs = MergedContext.getInstance()
                .getBeansOfType(ServerEndpointConfig.class);
        if (endpointConfigs != null) {
            for (final ServerEndpointConfig endpointConfig : endpointConfigs.values()) {
                serverContainer.addEndpoint(endpointConfig);
            }
        }
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
