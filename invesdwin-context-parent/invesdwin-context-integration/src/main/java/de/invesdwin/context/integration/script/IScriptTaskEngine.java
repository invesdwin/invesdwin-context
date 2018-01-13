package de.invesdwin.context.integration.script;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

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

}
