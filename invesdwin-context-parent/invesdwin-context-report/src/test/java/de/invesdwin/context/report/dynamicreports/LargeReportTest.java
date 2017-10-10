package de.invesdwin.context.report.dynamicreports;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.concurrent.ThreadSafe;
import javax.imageio.ImageIO;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.log.error.Err;
import de.invesdwin.context.report.jasperreports.Virtualizers;
import de.invesdwin.context.test.ATest;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.constant.HorizontalImageAlignment;
import net.sf.dynamicreports.report.constant.StretchType;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JREmptyDataSource;

/**
 * To acquire heap dump: -XX:+HeapDumpOnOutOfMemoryError
 */
@ThreadSafe
public class LargeReportTest extends ATest {

    @Test
    @Ignore("manual test")
    public void testLargeReport() throws FileNotFoundException, DRException {
        final JasperReportBuilder document = createReport();
        log.info("Generating");
        document.toPdf(new FileOutputStream(new File(ContextProperties.getCacheDirectory(), "Report.pdf")));
        log.info("Finished");
    }

    private JasperReportBuilder createReport() {
        final JasperReportBuilder document = DynamicReports.report()
                .setDataSource(new JREmptyDataSource(1))
                .setVirtualizer(Virtualizers.newSwapFileVirtualizer(getClass().getSimpleName()))
                .tableOfContents(
                        DynamicReports.tableOfContentsCustomizer().setTextFixedWidth(350).setPageIndexFixedWidth(20));
        //        document.pageHeader(createPageHeader());
        for (int i = 1; i <= 3000; i++) {
            log.info("Page " + i);
            addPageWithImage(document, i);
        }
        return document;
    }

    private void addPageWithImage(final JasperReportBuilder document, final int page) {
        final JasperReportBuilder report = DynamicReports.report().setDataSource(new JREmptyDataSource(1));
        report.detailHeader(DynamicReports.cmp.text("Page " + page));
        report.detail(DynamicReports.cmp.image(getUniqueSampleImage())
                .setStretchType(StretchType.CONTAINER_HEIGHT)
                .setHorizontalImageAlignment(HorizontalImageAlignment.CENTER)
                .setUsingCache(false));
        report.detail(DynamicReports.cmp.pageBreak());
        document.detail(DynamicReports.cmp.subreport(report));
    }

    private InputStream getUniqueSampleImage() {
        try {
            final int width = 1500, height = 900;

            // TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed
            // into integer pixels
            final BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            final Graphics2D ig2 = bi.createGraphics();

            final Toolkit toolkit = Toolkit.getDefaultToolkit();
            final Image image = toolkit.getImage(new ClassPathResource(
                    "/" + LargeReportTest.class.getPackage().getName().replace('.', '/') + "/sampleImage.png")
                            .getURL());

            ig2.drawImage(image, 0, 0, width, height, null);

            final Font font = new Font("TimesRoman", Font.BOLD, 20);
            ig2.setFont(font);
            final String message = RandomStringUtils.randomAlphanumeric(100);
            final FontMetrics fontMetrics = ig2.getFontMetrics();
            final int stringWidth = fontMetrics.stringWidth(message);
            final int stringHeight = fontMetrics.getAscent();
            ig2.setPaint(Color.black);
            ig2.drawString(message, (width - stringWidth) / 2, height / 2 + stringHeight / 4);

            final File file = File.createTempFile(getClass().getSimpleName(), ".png");
            ImageIO.write(bi, "PNG", file);
            return new FileInputStream(file);
        } catch (final IOException e) {
            throw Err.process(e);
        }
    }

}
