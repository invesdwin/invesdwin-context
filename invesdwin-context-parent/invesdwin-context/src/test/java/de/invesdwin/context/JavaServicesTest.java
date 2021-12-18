package de.invesdwin.context;

import java.nio.charset.Charset;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

import de.invesdwin.context.beans.init.MergedContext;
import de.invesdwin.context.system.properties.SystemProperties;
import de.invesdwin.context.test.ATest;
import de.invesdwin.util.lang.Strings;

@ThreadSafe
public class JavaServicesTest extends ATest {

    @Test
    public void testServices() throws Exception {
        final Resource[] resources = MergedContext.getInstance().getResources("classpath*:/META-INF/services/*");
        final SystemProperties systemProperties = new SystemProperties();
        systemProperties.getDelegate().setThrowExceptionOnMissing(false);
        for (final Resource res : resources) {
            String sys = systemProperties.getString(res.getFilename());
            if (sys == null) {
                for (final String line : IOUtils.readLines(res.getInputStream(), Charset.defaultCharset())) {
                    if (Strings.isBlank(line) || line.contains("#")) {
                        continue;
                    } else {
                        sys = line.trim();
                        break;
                    }
                }
                sys += "*";
            }
            log.info("%s=%s", res.getFilename(), sys);
        }
    }
}
