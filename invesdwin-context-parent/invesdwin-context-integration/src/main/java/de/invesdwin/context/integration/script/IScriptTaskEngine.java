package de.invesdwin.context.integration.script;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.lock.ILock;

@NotThreadSafe
public interface IScriptTaskEngine extends Closeable {

    void eval(String expression);

    default void eval(final CharSequence expression) {
        eval(expression.toString());
    }

    default void eval(final Resource resource) {
        try {
            eval(resource.getInputStream());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    default void eval(final Reader reader) {
        try (Reader in = reader) {
            final String str = IOUtils.toString(in);
            eval(str);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    default void eval(final InputStream input) {
        try (InputStream in = input) {
            final String str = IOUtils.toString(in, StandardCharsets.UTF_8);
            eval(str);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    IScriptTaskInputs getInputs();

    IScriptTaskResults getResults();

    @Override
    void close();

    Object unwrap();

    /**
     * Always acquire this lock first before accessing the engine. Might be a disabled lock if the engine underneath is
     * not shared between instances.
     */
    ILock getSharedLock();

    /**
     * If this is not null, then the engine should only be accessed from within this executor. Or else jvm crashes are
     * to be expected.
     */
    WrappedExecutorService getSharedExecutor();

}
