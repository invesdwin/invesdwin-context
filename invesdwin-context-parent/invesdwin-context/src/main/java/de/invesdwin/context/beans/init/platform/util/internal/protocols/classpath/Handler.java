package de.invesdwin.context.beans.init.platform.util.internal.protocols.classpath;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import javax.annotation.concurrent.Immutable;

import org.springframework.util.ClassUtils;

@Immutable
public class Handler extends URLStreamHandler {

    @Override
    protected URLConnection openConnection(final URL u) throws IOException {
        final URL resourceUrl = ClassUtils.getDefaultClassLoader().getResource(u.getPath());
        if (resourceUrl != null) {
            return resourceUrl.openConnection();
        } else {
            return null;
        }
    }
}
