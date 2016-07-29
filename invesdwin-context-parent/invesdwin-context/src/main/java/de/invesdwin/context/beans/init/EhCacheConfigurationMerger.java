package de.invesdwin.context.beans.init;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.log.Log;
import de.invesdwin.context.log.error.Err;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Resources;
import de.invesdwin.util.lang.Strings;

@Named
@ThreadSafe
public class EhCacheConfigurationMerger {

    public static final String DEFAULT_EHCACHE_CLASSPATH_LOCATION = "ehcache.xml";
    private static final String EHCACHE_PATH = "/META-INF/ehcache/";
    @GuardedBy("this.class")
    private static File alreadyGenerated;
    private final Log log = new Log(this);

    public URL newEhCacheXml() {
        synchronized (EhCacheConfigurationMerger.class) {
            try {
                if (alreadyGenerated == null || !alreadyGenerated.exists()) {
                    Assertions.assertThat(ContextProperties.EHCACHE_DISK_STORE_DIRECTORY).isNotNull();
                    final String placeholder = "<!-- MergedCachesPlaceholder -->";
                    final String template = getXmlTemplate();
                    Assertions.assertThat(template.contains(placeholder))
                            .as("\"%s\" was not found in template!", placeholder)
                            .isTrue();
                    final String merged = generateNewXmlContent();
                    final File file = new File(ContextProperties.TEMP_CLASSPATH_DIRECTORY,
                            DEFAULT_EHCACHE_CLASSPATH_LOCATION);
                    FileUtils.write(file, template.replace(placeholder, merged));
                    alreadyGenerated = file;
                }
                return new FileSystemResource(alreadyGenerated).getURL();
            } catch (final IOException | TransformerException | XMLStreamException e) {
                throw Err.process(e);
            }
        }
    }

    private String getXmlTemplate() throws IOException {
        final ClassPathResource templateResource = new ClassPathResource("META-INF/template.ehcache.xml");
        final String template = IOUtils.toString(templateResource.getInputStream());
        return template;
    }

    private String generateNewXmlContent() throws IOException, TransformerException, XMLStreamException {
        final Resource[] xmls = PreMergedContext.getInstance().getResources(
                "classpath*:" + EHCACHE_PATH + "*.ehcache.xml");

        logXmlsBeingLoaded(xmls);

        final StringBuilder sb = new StringBuilder();

        for (final Resource resource : xmls) {
            final XMLInputFactory xif = XMLInputFactory.newInstance();
            final XMLStreamReader xsr = xif.createXMLStreamReader(resource.getInputStream());
            xsr.nextTag(); //ehcache tag skipped

            while (xsr.nextTag() == XMLStreamConstants.START_ELEMENT) {
                final TransformerFactory tf = TransformerFactory.newInstance();
                final Transformer t = tf.newTransformer();
                final StringWriter res = new StringWriter();
                t.transform(new StAXSource(xsr), new StreamResult(res));
                final String element = Strings.substringAfter(res.toString(), "?>");
                sb.append(element);
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private void logXmlsBeingLoaded(final Resource[] xmls) {
        if (log.isInfoEnabled() && xmls.length > 0) {
            final List<String> xmlsForLog = Resources.extractMetaInfResourceLocations(Arrays.asList(xmls));
            String filesSingularPlural = "config";
            if (xmls.length != 1) {
                filesSingularPlural += "s";
            }
            log.info("Loading " + xmls.length + " ehcache " + filesSingularPlural + " " + xmlsForLog.toString());
        }
    }

}
