package de.invesdwin.context.beans.init.platform.util.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.core.io.Resource;

import de.invesdwin.context.log.error.Err;
import de.invesdwin.context.log.error.LoggedRuntimeException;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.lang.string.Strings;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;

@NotThreadSafe
public class Log4j2ConfigurationMerger {

    private static final String CONFIGURATION_OPEN = "<Configuration status=\"WARN\">";
    private static final String CONFIGURATION_CLOSE = "</Configuration>";

    // LinkedHashMap preserves the order of wrapper tags as they are encountered,
    // while dynamically grouping all children by their wrapper name.
    private final Map<String, List<String>> mergedSections = ILockCollectionFactory.getInstance(false).newLinkedMap();

    public Log4j2ConfigurationMerger(final List<Resource> resources) {
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

        try {
            final XMLStreamReader xsr = xif.createXMLStreamReader(in);
            final TransformerFactory tf = TransformerFactory.newInstance();

            int depth = 0;
            String currentWrapper = null;

            while (xsr.hasNext()) {
                final int event = xsr.next();

                if (event == XMLStreamConstants.START_ELEMENT) {
                    depth++;

                    if (depth == 1) {
                        // Depth 1 is the root <Configuration> tag. Skip.
                        continue;
                    } else if (depth == 2) {
                        // Depth 2 tags are the generic category wrappers.
                        // Capitalize to normalize (e.g. <appenders> and <Appenders> become "Appenders")
                        currentWrapper = capitalize(xsr.getLocalName());
                    } else if (depth == 3 && currentWrapper != null) {
                        // Depth 3 tags are the nested children meant to be extracted and merged[cite: 1]
                        final Transformer t = tf.newTransformer();
                        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                        final StringWriter res = new StringWriter();

                        // The transformer consumes the item and its entire sub-tree[cite: 1]
                        t.transform(new StAXSource(xsr), new StreamResult(res));

                        String element = res.toString().trim();
                        if (element.startsWith("<?xml") && element.contains("?>")) {
                            element = Strings.substringAfter(element, "?>").trim();
                        }

                        // Initialize the list if the generic wrapper was never encountered before
                        List<String> items = mergedSections.get(currentWrapper);
                        if (items == null) {
                            items = new ArrayList<String>();
                            mergedSections.put(currentWrapper, items);
                        }
                        items.add(element);

                        // Because Transformer consumed the element up to its END_ELEMENT,
                        // the reader is now conceptually exiting Depth 3. Manually adjust depth.
                        depth--;
                    }
                } else if (event == XMLStreamConstants.END_ELEMENT) {
                    depth--;
                    if (depth == 1) {
                        // We have exited the wrapper tag
                        currentWrapper = null;
                    }
                }
            }
            xsr.close();
        } finally {
            in.close();
        }
    }

    public InputStream getInputStream() {
        final String mergedXmlConfig = mergeConfigs();
        return new FastByteArrayInputStream(mergedXmlConfig.getBytes());
    }

    private String mergeConfigs() {
        final StringBuilder merged = new StringBuilder(CONFIGURATION_OPEN);
        merged.append("\n");

        // Force a sensible standard order for well-known Log4j2 tags so the output remains clean
        final List<String> standardOrder = Arrays.asList("Properties", "Scripts", "CustomLevels", "Filters",
                "Appenders", "Loggers");

        // First pass: Output recognized tags in standard layout order
        for (final String key : standardOrder) {
            appendSection(merged, key);
        }

        // Second pass: Automatically support and output any arbitrary/future tags that were parsed
        for (final String key : mergedSections.keySet()) {
            if (!standardOrder.contains(key)) {
                appendSection(merged, key);
            }
        }

        merged.append(CONFIGURATION_CLOSE);
        return merged.toString();
    }

    private void appendSection(final StringBuilder merged, final String key) {
        final List<String> items = mergedSections.get(key);
        if (items != null && !items.isEmpty()) {
            merged.append("    <").append(key).append(">\n");
            for (final String item : items) {
                merged.append("        ").append(item).append("\n");
            }
            merged.append("    </").append(key).append(">\n");
        }
    }

    private String capitalize(final String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}