package de.invesdwin.context.jfreechart.panel.basis;

import java.awt.Graphics2D;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.JFreeChart;

@NotThreadSafe
public class CustomChartTransferable implements Transferable {

    private static final DataFlavor IMAGE_FLAVOR = new DataFlavor("image/x-java-image; class=java.awt.Image", "Image");

    private final BufferedImage chartData;

    public CustomChartTransferable(final JFreeChart chart, final int width, final int height) {
        this(chart, width, height, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public CustomChartTransferable(final JFreeChart chart, final int width, final int height, final int minDrawW,
            final int minDrawH, final int maxDrawW, final int maxDrawH) {
        this.chartData = createBufferedImage(chart, width, height, minDrawW, minDrawH, maxDrawW, maxDrawH);
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { IMAGE_FLAVOR };
    }

    @Override
    public boolean isDataFlavorSupported(final DataFlavor flavor) {
        return IMAGE_FLAVOR.equals(flavor);
    }

    @Override
    public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (IMAGE_FLAVOR.equals(flavor)) {
            return chartData;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    private BufferedImage createBufferedImage(final JFreeChart chart, final int w, final int h, final int minDrawW,
            final int minDrawH, final int maxDrawW, final int maxDrawH) {

        final BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = image.createGraphics();

        // work out if scaling is required...
        boolean scale = false;
        double drawWidth = w;
        double drawHeight = h;
        double scaleX = 1.0;
        double scaleY = 1.0;
        if (drawWidth < minDrawW) {
            scaleX = drawWidth / minDrawW;
            drawWidth = minDrawW;
            scale = true;
        } else if (drawWidth > maxDrawW) {
            scaleX = drawWidth / maxDrawW;
            drawWidth = maxDrawW;
            scale = true;
        }
        if (drawHeight < minDrawH) {
            scaleY = drawHeight / minDrawH;
            drawHeight = minDrawH;
            scale = true;
        } else if (drawHeight > maxDrawH) {
            scaleY = drawHeight / maxDrawH;
            drawHeight = maxDrawH;
            scale = true;
        }

        final Rectangle2D chartArea = new Rectangle2D.Double(0.0, 0.0, drawWidth, drawHeight);
        if (scale) {
            final AffineTransform st = AffineTransform.getScaleInstance(scaleX, scaleY);
            g2.transform(st);
        }
        chart.draw(g2, chartArea, null, null);
        g2.dispose();
        return image;

    }

}
