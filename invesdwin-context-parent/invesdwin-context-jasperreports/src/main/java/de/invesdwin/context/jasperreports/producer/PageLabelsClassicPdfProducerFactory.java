package de.invesdwin.context.jasperreports.producer;

import javax.annotation.concurrent.Immutable;

import net.sf.jasperreports.export.pdf.PdfProducer;
import net.sf.jasperreports.export.pdf.PdfProducerContext;
import net.sf.jasperreports.export.pdf.PdfProducerFactory;

@Immutable
public class PageLabelsClassicPdfProducerFactory implements PdfProducerFactory {

    public PageLabelsClassicPdfProducerFactory() {
    }

    @Override
    public PdfProducer createProducer(final PdfProducerContext context) {
        return new PageLabelsClassicPdfProducer(context);
    }

}
