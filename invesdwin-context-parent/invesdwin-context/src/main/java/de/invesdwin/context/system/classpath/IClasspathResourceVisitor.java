package de.invesdwin.context.system.classpath;

import java.io.InputStream;

public interface IClasspathResourceVisitor {
    /**
     * @return {@code true} if the algorithm should visit more results, {@code false} if it should terminate now.
     */
    boolean visit(String fullPath, String resourcePath, InputStream inputStream);

    void finish();
}