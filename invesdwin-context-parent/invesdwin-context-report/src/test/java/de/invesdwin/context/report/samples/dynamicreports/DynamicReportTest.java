package de.invesdwin.context.report.samples.dynamicreports;

import java.awt.Frame;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.Test;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.view.JasperViewer;

@ThreadSafe
public class DynamicReportTest extends ATest {

    @SuppressWarnings("JUnit4SetUpNotRun")
    @Override
    public void setUp() throws Exception {
        super.setUp();
        for (final Frame frame : JasperViewer.getFrames()) {
            frame.dispose();
        }
    }

    @Test
    public void testView() throws DRException, InterruptedException {
        final DynamicReportDesign design = new DynamicReportDesign();
        final JasperReportBuilder report = design.build();
        report.show();
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
    public void testPdfExport() throws FileNotFoundException, DRException {
        final DynamicReportDesign design = new DynamicReportDesign();
        final JasperReportBuilder report = design.build();
        final File exportFile = new File(ContextProperties.getCacheDirectory(), "DynamicReports.pdf");
        Assertions.assertThat(exportFile).doesNotExist();
        report.toPdf(new FileOutputStream(exportFile));
        Assertions.assertThat(exportFile).exists();
    }

    @Test
    public void testHtmlExport() throws DRException, FileNotFoundException {
        final DynamicReportDesign design = new DynamicReportDesign();
        final JasperReportBuilder report = design.build();
        final File exportFile = new File(ContextProperties.getCacheDirectory(), "DynamicReports.html");
        Assertions.assertThat(exportFile).doesNotExist();
        report.toHtml(new FileOutputStream(new File(ContextProperties.getCacheDirectory(), "DynamicReports.html")));
        Assertions.assertThat(exportFile).exists();
    }

}
