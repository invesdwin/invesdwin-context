package de.invesdwin.context.jasperreports.producer;

import java.util.function.Supplier;

import javax.annotation.concurrent.NotThreadSafe;

import com.lowagie.text.pdf.PdfPageLabels;

import io.netty.util.concurrent.FastThreadLocal;
import net.sf.jasperreports.export.pdf.PdfProducerContext;
import net.sf.jasperreports.export.pdf.classic.ClassicPdfProducer;

@NotThreadSafe
public class PageLabelsClassicPdfProducer extends ClassicPdfProducer {

    private static final FastThreadLocal<Supplier<PdfPageLabels>> PAGE_LABELS_HOLDER = new FastThreadLocal<>();

    private final Supplier<PdfPageLabels> pageLabelsSupplier;

    public PageLabelsClassicPdfProducer(final PdfProducerContext context) {
        super(context);
        pageLabelsSupplier = PAGE_LABELS_HOLDER.get();
        PAGE_LABELS_HOLDER.remove();
    }

    /**
     * Don't know if there is a better way to pass this information from the document builder to the producer.
     */
    public static void setPageLabelsSupplier(final Supplier<PdfPageLabels> pageLabels) {
        PAGE_LABELS_HOLDER.set(pageLabels);
    }

    @Override
    public void close() {
        if (pageLabelsSupplier != null) {
            final PdfPageLabels pageLabels = pageLabelsSupplier.get();
            if (pageLabels != null) {
                getPdfWriter().setPageLabels(pageLabels);
            }
        }
        super.close();
    }

}
