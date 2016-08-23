package de.invesdwin.context.beans.init.internal;

import java.nio.charset.Charset;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.log.Log;
import de.invesdwin.context.system.properties.SystemProperties;

@Immutable
public final class FileEncodingChecker {

    private FileEncodingChecker() {}

    public static void check() {
        final String expectedCharset = "UTF-8";
        final String fileEncodingKey = "file.encoding";
        final SystemProperties systemProperties = new SystemProperties();
        final String fileCharset = Charset.forName(systemProperties.getString(fileEncodingKey)).displayName();
        final String defaultCharset = Charset.defaultCharset().displayName();
        final StringBuilder warning = new StringBuilder();
        if (!expectedCharset.equals(defaultCharset)) {
            warning.append(fileEncodingKey);
            warning.append("=");
            warning.append(fileCharset);
            warning.append(" is not the expected ");
            warning.append(expectedCharset);
            warning.append(". ");
        }
        if (!fileCharset.equals(defaultCharset)) {
            warning.append(fileEncodingKey);
            warning.append("=");
            warning.append(fileCharset);
            warning.append(" does not match the configured charset ");
            warning.append(defaultCharset);
            warning.append(". ");
        }

        if (warning.length() > 0) {
            warning.append(
                    "If you encounter any problems with characters not displayed correctly, please start the JVM with the parameter -D");
            warning.append(fileEncodingKey);
            warning.append("=");
            warning.append(expectedCharset);
            new Log(FileEncodingChecker.class).warn(warning.toString());
        }
    }

}
