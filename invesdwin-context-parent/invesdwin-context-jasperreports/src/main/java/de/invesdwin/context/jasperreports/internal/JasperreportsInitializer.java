package de.invesdwin.context.jasperreports.internal;

import java.io.File;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.beans.hook.IPreStartupHook;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.design.JRCompiler;

@NotThreadSafe
public class JasperreportsInitializer implements IPreStartupHook {

    @Override
    public void preStartup() throws Exception {
        DefaultJasperReportsContext.getInstance()
                .setProperty(JRCompiler.COMPILER_TEMP_DIR,
                        new File(ContextProperties.TEMP_DIRECTORY, JRCompiler.class.getSimpleName()).getAbsolutePath());
    }

}
