package de.invesdwin.context.beans.init.internal;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class XmlTransformerConfigurer {

    public static final boolean INITIALIZED;

    static {
        //as long as xalan does not support jaxp 1.4 the sun impl must be used
        //CHECKSTYLE:OFF do not use SystemProperties class to not cause static intializers from firing too early
        System.setProperty("javax.xml.transform.TransformerFactory",
                "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
        //CHECKSTYLE:ON
        INITIALIZED = true;
    }

    private XmlTransformerConfigurer() {}

}
