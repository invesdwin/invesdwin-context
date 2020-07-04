package de.invesdwin.context.beans.init.platform.util.internal;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.reflection.Reflections;

@Immutable
public final class XmlTransformerConfigurer {

    private XmlTransformerConfigurer() {}

    public static void configure() {
        //as long as xalan does not support jaxp 1.4 the sun impl must be used
        //CHECKSTYLE:OFF do not use SystemProperties class to not cause static intializers from firing too early
        final String transformerImpl = "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl";
        if (Reflections.classExists(transformerImpl)) {
            System.setProperty("javax.xml.transform.TransformerFactory", transformerImpl);
        }

        //https://blog.jooq.org/2013/08/25/how-to-speed-up-apache-xalans-xpath-processor-by-factor-10x/
        System.setProperty("org.apache.xml.dtm.DTMManager", "org.apache.xml.dtm.ref.DTMManagerDefault");
        System.setProperty("com.sun.org.apache.xml.internal.dtm.DTMManager",
                "com.sun.org.apache.xml.internal.dtm.ref.DTMManagerDefault");
        //CHECKSTYLE:ON
    }

}
