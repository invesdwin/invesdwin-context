package de.invesdwin.context.jasperreports.internal;

import javax.annotation.concurrent.NotThreadSafe;
import jakarta.inject.Named;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.beans.hook.IPreStartupHook;
import de.invesdwin.context.jasperreports.producer.PageLabelsClassicPdfProducerFactory;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.design.JRCompiler;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.fill.JRSubreportRunnerFactory;

@Named
@NotThreadSafe
public class JasperReportsInitializer implements IPreStartupHook {

    @Override
    public void preStartup() throws Exception {
        final DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();
        context.setProperty(JRCompiler.COMPILER_TEMP_DIR, ContextProperties.TEMP_DIRECTORY.getAbsolutePath());
        context.setProperty(JRSubreportRunnerFactory.SUBREPORT_RUNNER_FACTORY,
                ConfiguredSubreportRunnerFactory.class.getName());
        DefaultJasperReportsContext.getInstance()
                .setProperty(JRPdfExporter.PDF_PRODUCER_FACTORY_PROPERTY,
                        PageLabelsClassicPdfProducerFactory.class.getName());
    }

}
