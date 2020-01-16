package de.invesdwin.context.jasperreports.internal;

import javax.annotation.concurrent.NotThreadSafe;
import javax.inject.Named;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.beans.hook.IPreStartupHook;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.design.JRCompiler;

@Named
@NotThreadSafe
public class JasperreportsInitializer implements IPreStartupHook {

    @Override
    public void preStartup() throws Exception {
        DefaultJasperReportsContext.getInstance()
                .setProperty(JRCompiler.COMPILER_TEMP_DIR, ContextProperties.TEMP_DIRECTORY.getAbsolutePath());
    }

}
