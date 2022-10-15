package de.invesdwin.context.beans.init.platform.util.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.core.io.Resource;

import de.invesdwin.context.log.error.Err;
import de.invesdwin.context.log.error.LoggedRuntimeException;
import de.invesdwin.util.lang.string.Strings;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;

@NotThreadSafe
public class LogbackConfigurationMerger {

    private static final String CONFIGURATION_OPEN = "<configuration>";
    private static final String CONFIGURATION_CLOSE = "</configuration>";

    private final List<String> appenders = new ArrayList<String>();
    private final List<String> loggers = new ArrayList<String>();
    private final List<String> miscs = new ArrayList<String>();

    public LogbackConfigurationMerger(final List<Resource> resources) {
        for (final Resource r : resources) {
            try {
                extractComponents(r);
            } catch (final IOException e) {
                throw handleException(e, r);
            } catch (final XMLStreamException e) {
                throw handleException(e, r);
            } catch (final TransformerException e) {
                throw handleException(e, r);
            }
        }
    }

    private LoggedRuntimeException handleException(final Throwable cause, final Resource r) {
        try {
            return Err.process(new RuntimeException("At: " + r.getURI(), cause));
        } catch (final IOException e) {
            return Err.process(e);
        }
    }

    private void extractComponents(final Resource resource)
            throws XMLStreamException, IOException, TransformerException {
        final XMLInputFactory xif = XMLInputFactory.newInstance();
        final InputStream in = resource.getInputStream();
        final XMLStreamReader xsr = xif.createXMLStreamReader(in);
        xsr.nextTag(); //configuration tag skipped

        while (xsr.nextTag() == XMLStreamConstants.START_ELEMENT) {
            final TransformerFactory tf = TransformerFactory.newInstance();
            final Transformer t = tf.newTransformer();
            final StringWriter res = new StringWriter();
            t.transform(new StAXSource(xsr), new StreamResult(res));
            final String element = Strings.substringAfter(res.toString(), "?>");
            if (element.startsWith("<appender")) {
                appenders.add(element);
            } else if (element.startsWith("<logger")) {
                loggers.add(element);
            } else {
                miscs.add(element);
            }
        }
        xsr.close();
        in.close();
    }

    public InputStream getInputStream() {
        final String mergedXmlConfig = mergeConfigs();
        return new FastByteArrayInputStream(mergedXmlConfig.getBytes());
    }

    private String mergeConfigs() {
        final StringBuilder merged = new StringBuilder(CONFIGURATION_OPEN);
        merged.append("\n");
        for (final String appender : appenders) {
            merged.append(appender);
            merged.append("\n");
        }
        for (final String logger : loggers) {
            merged.append(logger);
            merged.append("\n");
        }
        for (final String misc : miscs) {
            merged.append(misc);
            merged.append("\n");
        }
        merged.append(CONFIGURATION_CLOSE);
        return merged.toString();
    }

}
