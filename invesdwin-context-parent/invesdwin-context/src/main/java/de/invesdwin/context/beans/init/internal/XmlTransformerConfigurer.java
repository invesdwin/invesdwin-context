package de.invesdwin.context.beans.init.internal;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.system.properties.SystemProperties;

@Immutable
public final class XmlTransformerConfigurer {

    public static final boolean INITIALIZED;

    static {
        //as long as xalan does not support jaxp 1.4 the sun impl must be used
        new SystemProperties().setString("javax.xml.transform.TransformerFactory",
                "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
        INITIALIZED = true;
    }

    private XmlTransformerConfigurer() {}

}
