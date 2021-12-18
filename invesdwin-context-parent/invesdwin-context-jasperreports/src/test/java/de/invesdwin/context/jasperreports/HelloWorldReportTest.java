package de.invesdwin.context.jasperreports;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

@ThreadSafe
public final class HelloWorldReportTest extends ATest {

    private final String jrxml = "/" + getClass().getPackage().getName().replace(".", "/") + "/HelloWorldReport.jrxml";

    @SuppressWarnings("JUnit4SetUpNotRun")
    @Override
    public void setUp() throws Exception {
        super.setUp();
        for (final Frame frame : JasperViewer.getFrames()) {
            frame.dispose();
        }
    }

    @Test
    public void testCompileAndView() throws JRException, IOException, InterruptedException {
        final ClassPathResource resource = new ClassPathResource(jrxml);
        final JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("SomeParam", "testCompileAndView");
        final JRDataSource dataSource = new JREmptyDataSource();
        final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
        JasperViewer.viewReport(jasperPrint);
        TimeUnit.SECONDS.sleep(1);
        assertViewerOpen();
    }

    private void assertViewerOpen() {
        int visibleFrames = 0;
        for (final Frame f : JasperViewer.getFrames()) {
            if (f.isVisible()) {
                visibleFrames++;
            }
        }
        Assertions.assertThat(visibleFrames).isEqualTo(1);
    }

    @Test
    public void testExportToPDF() throws JRException, IOException {
        final ClassPathResource resource = new ClassPathResource(jrxml);
        final JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("SomeParam", "testExportToPDF");
        final JRDataSource dataSource = new JREmptyDataSource();
        final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
        final File exportedFile = new File(ContextProperties.getCacheDirectory(), "HelloWorldReport.pdf");
        Assertions.assertThat(exportedFile).doesNotExist();
        JasperExportManager.exportReportToPdfFile(jasperPrint, exportedFile.getAbsolutePath());
        Assertions.assertThat(exportedFile).exists();
    }

    @Test
    public void testExportToHtml() throws JRException, IOException {
        final ClassPathResource resource = new ClassPathResource(jrxml);
        final JasperReport jasperReport = JasperCompileManager.compileReport(resource.getInputStream());
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("SomeParam", "testExportToHtml");
        final JRDataSource dataSource = new JREmptyDataSource();
        final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
        final File exportedFile = new File(ContextProperties.getCacheDirectory(), "HelloWorldReport.html");
        Assertions.assertThat(exportedFile).doesNotExist();
        JasperExportManager.exportReportToHtmlFile(jasperPrint, exportedFile.getAbsolutePath());
        Assertions.assertThat(exportedFile).exists();
    }
}
