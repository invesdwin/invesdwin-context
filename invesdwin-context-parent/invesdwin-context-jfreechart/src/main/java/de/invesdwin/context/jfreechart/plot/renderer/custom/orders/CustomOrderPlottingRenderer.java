package de.invesdwin.context.jfreechart.plot.renderer.custom.orders;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.ImageIcon;

import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.chart.util.XYCoordinateType;
import org.jfree.data.xy.XYDataset;

import de.invesdwin.context.jfreechart.panel.helper.config.LineStyleType;
import de.invesdwin.context.jfreechart.panel.helper.config.LineWidthType;
import de.invesdwin.context.jfreechart.panel.helper.config.PlotConfigurationHelper;
import de.invesdwin.context.jfreechart.panel.helper.config.PriceInitialSettings;
import de.invesdwin.context.jfreechart.panel.helper.icons.PlotIcons;
import de.invesdwin.context.jfreechart.plot.XYPlots;
import de.invesdwin.context.jfreechart.plot.annotation.XYIconAnnotation;
import de.invesdwin.context.jfreechart.plot.annotation.priceline.XYPriceLineAnnotation;
import de.invesdwin.context.jfreechart.plot.renderer.IUpDownColorRenderer;
import de.invesdwin.context.jfreechart.plot.renderer.custom.CustomProfitLossRenderer;
import de.invesdwin.context.jfreechart.plot.renderer.custom.ICustomRendererType;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.swing.icon.AlphaImageIcon;
import de.invesdwin.util.swing.icon.ChangeColorImageFilter;

@NotThreadSafe
public class CustomOrderPlottingRenderer extends AbstractXYItemRenderer
        implements ICustomRendererType, IUpDownColorRenderer {

    public static final Font FONT = XYPriceLineAnnotation.FONT;
    public static final Color UP_COLOR = CustomProfitLossRenderer.UP_COLOR;
    public static final Color DOWN_COLOR = CustomProfitLossRenderer.DOWN_COLOR;
    public static final Color BOTH_COLOR = new Color(230, 145, 56);
    public static final float NOTE_ALPHA = 0.2F;
    private static final ValueAxis ABSOLUTE_AXIS = XYPlots.DRAWING_ABSOLUTE_AXIS;
    private static final int NOTE_ICON_SIZE = 20;
    private static final float NOTE_Y_MODIFICATION = -(NOTE_ICON_SIZE / 2F);

    private static final LineStyleType LINE_STYLE_DEFAULT = LineStyleType.Solid;
    private static final LineStyleType LINE_STYLE_PENDING = LineStyleType.Dashed;

    private final OrderPlottingDataset dataset;
    private Color upColor;
    private Color downColor;
    private ImageIcon noteUpIcon;
    private ImageIcon noteDownIcon;
    private ImageIcon noteBothIcon;

    public CustomOrderPlottingRenderer(final PlotConfigurationHelper plotConfigurationHelper,
            final OrderPlottingDataset dataset) {
        this.dataset = dataset;
        final PriceInitialSettings config = plotConfigurationHelper.getPriceInitialSettings();
        setUpColor(UP_COLOR);
        setDownColor(DOWN_COLOR);
        setSeriesPaint(0, BOTH_COLOR);
        setSeriesStroke(0, config.getSeriesStroke());
    }

    @Override
    public void setSeriesPaint(final int series, final Paint paint, final boolean notify) {
        super.setSeriesPaint(series, paint, notify);
        if (series == 0) {
            noteBothIcon = newNoteIcon((Color) paint);
        }
    }

    private ImageIcon newNoteIcon(final Color color) {
        final ImageIcon icon = PlotIcons.MARKER.newIcon(NOTE_ICON_SIZE);
        final ImageIcon changeColor = ChangeColorImageFilter.apply(icon, color);
        return new AlphaImageIcon(changeColor, NOTE_ALPHA);
    }

    @Override
    public Paint getItemPaint(final int row, final int column) {
        if (dataset.isLastTradeProfit()) {
            return upColor;
        } else {
            return downColor;
        }
    }

    @Override
    public boolean isLineStyleConfigurable() {
        return false;
    }

    @Override
    public boolean isLineWidthConfigurable() {
        return true;
    }

    @Override
    public boolean isSeriesColorConfigurable() {
        return true;
    }

    @Override
    public String getName() {
        return "Order Plotting";
    }

    @Override
    public void setUpColor(final Color upColor) {
        this.upColor = upColor;
        this.noteUpIcon = newNoteIcon(upColor);
        fireChangeEvent();
    }

    @Override
    public Color getUpColor() {
        return upColor;
    }

    @Override
    public void setDownColor(final Color downColor) {
        this.downColor = downColor;
        this.noteDownIcon = newNoteIcon(downColor);
        fireChangeEvent();
    }

    @Override
    public Color getDownColor() {
        return downColor;
    }

    @Override
    public boolean isSeriesRendererTypeConfigurable() {
        return false;
    }

    @Override
    public String getUpColorName() {
        return "Profit";
    }

    @Override
    public String getDownColorName() {
        return "Loss";
    }

    @Override
    public String getSeriesColorName() {
        return "Both";
    }

    //CHECKSTYLE:OFF
    @Override
    public void drawItem(final Graphics2D g2, final XYItemRendererState state, final Rectangle2D dataArea,
            final PlotRenderingInfo info, final XYPlot plot, final ValueAxis domainAxis, final ValueAxis rangeAxis,
            final XYDataset dataset, final int series, final int item, final CrosshairState crosshairState,
            final int pass) {
        //CHECKSTYLE:ON
        final int lastItem = state.getLastItemIndex();
        if (item == lastItem) {
            final OrderPlottingDataset cDataset = (OrderPlottingDataset) dataset;
            final int firstItem = state.getFirstItemIndex();
            final int rendererIndex = getPlot().getIndexOf(this);
            final NumberAxis cRangeAxis = (NumberAxis) rangeAxis;
            final NumberFormat rangeAxisFormat = cRangeAxis.getNumberFormatOverride();
            final PlotOrientation orientation = plot.getOrientation();
            final RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
            final RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(), orientation);
            final LineWidthType lineWidth = LineWidthType.valueOf(getSeriesStroke(series));
            final Map<Integer, List<String>> index_notes = new LinkedHashMap<Integer, List<String>>();
            drawLines(g2, dataArea, info, plot, domainAxis, rangeAxis, lastItem, cDataset, firstItem, rendererIndex,
                    rangeAxisFormat, domainEdge, rangeEdge, lineWidth, index_notes);

            for (final Entry<Integer, List<String>> entry : index_notes.entrySet()) {
                final int noteIndex = entry.getKey();
                final List<String> notes = entry.getValue();
                Strings.COMPARATOR.sortAscending(notes);

                int countProfit = 0;
                int countLoss = 0;
                for (int i = 0; i < notes.size(); i++) {
                    final String noteStr = notes.get(i);
                    final char profitLossChar = noteStr.charAt(0);
                    if (profitLossChar == '+') {
                        countProfit++;
                    } else if (profitLossChar == '-') {
                        countLoss++;
                    }
                }

                final String note = Strings.join(notes, "\n");

                final ImageIcon icon;
                if (countProfit > 0 && countLoss == 0) {
                    // green
                    icon = noteUpIcon;
                } else if (countProfit == 0 && countLoss > 0) {
                    // red
                    icon = noteDownIcon;
                } else {
                    // orange
                    icon = noteBothIcon;
                }
                final double x = noteIndex;
                final double y = cDataset.getOhlcDataset().getHighValue(0, noteIndex);
                final XYIconAnnotation noteAnnotation = new XYIconAnnotation(x, y, icon) {
                    @Override
                    protected float modifyYOutput(final float y) {
                        return y + NOTE_Y_MODIFICATION;
                    }
                };
                noteAnnotation.setCoordinateType(XYCoordinateType.DATA);
                noteAnnotation.draw(g2, plot, dataArea, domainAxis, cRangeAxis, rendererIndex, info);
            }

        }
    }

    //CHECKSTYLE:OFF
    private void drawLines(final Graphics2D g2, final Rectangle2D dataArea, final PlotRenderingInfo info,
            final XYPlot plot, final ValueAxis domainAxis, final ValueAxis rangeAxis, final int lastItem,
            final OrderPlottingDataset cDataset, final int firstItem, final int rendererIndex,
            final NumberFormat rangeAxisFormat, final RectangleEdge domainEdge, final RectangleEdge rangeEdge,
            final LineWidthType lineWidth, final Map<Integer, List<String>> index_notes) {
        //CHECKSTYLE:ON
        final ICloseableIterator<OrderPlottingDataItem> visibleItems = cDataset.getVisibleItems(firstItem, lastItem)
                .iterator();
        try {
            while (true) {
                final OrderPlottingDataItem next = visibleItems.next();
                drawLine(g2, dataArea, info, plot, domainAxis, rangeAxis, rendererIndex, rangeAxisFormat, domainEdge,
                        rangeEdge, lineWidth, next);
                final String note = next.getNote();
                if (Strings.isNotBlank(note)) {
                    final int noteIndex = next.getOpenTimeIndex();
                    List<String> notes = index_notes.get(noteIndex);
                    if (notes == null) {
                        notes = new ArrayList<String>();
                        index_notes.put(noteIndex, notes);
                    }
                    notes.add(note);
                }
            }
        } catch (final NoSuchElementException e) {
            //end reached
        }
    }

    //CHECKSTYLE:OFF
    private void drawLine(final Graphics2D g2, final Rectangle2D dataArea, final PlotRenderingInfo info,
            final XYPlot plot, final ValueAxis domainAxis, final ValueAxis rangeAxis, final int rendererIndex,
            final NumberFormat rangeAxisFormat, final RectangleEdge domainEdge, final RectangleEdge rangeEdge,
            final LineWidthType lineWidth, final OrderPlottingDataItem next) {
        //CHECKSTYLE:ON
        final Color color;
        if (next.isProfit()) {
            color = upColor;
        } else {
            color = downColor;
        }

        final LineStyleType lineStyle;
        if (next.isPending()) {
            lineStyle = LINE_STYLE_PENDING;
        } else {
            lineStyle = LINE_STYLE_DEFAULT;
        }

        final Stroke stroke = lineStyle.getStroke(lineWidth);

        final boolean closed = next.isClosed();
        final double x1 = domainAxis.valueToJava2D(next.getOpenTimeIndex(), dataArea, domainEdge);
        final double x2;
        if (closed) {
            x2 = domainAxis.valueToJava2D(next.getCloseTimeIndex(), dataArea, domainEdge);
        } else {
            x2 = dataArea.getMaxX();
        }

        final double openPrice = next.getOpenPrice();
        final double closePrice = next.getClosePrice();

        final double y1 = rangeAxis.valueToJava2D(openPrice, dataArea, rangeEdge);
        final double y2 = rangeAxis.valueToJava2D(closePrice, dataArea, rangeEdge);
        final XYLineAnnotation lineAnnotation = new XYLineAnnotation(x1, y1, x2, y2, stroke, color);
        lineAnnotation.draw(g2, plot, dataArea, ABSOLUTE_AXIS, ABSOLUTE_AXIS, rendererIndex, info);

        if (!closed) {
            final String label = next.getLabel();
            if (Strings.isNotBlank(label)) {
                final XYTextAnnotation labelAnnotation = new XYTextAnnotation(label, x2 - 1D, y2 - 1D);
                labelAnnotation.setPaint(color);
                labelAnnotation.setFont(FONT);
                labelAnnotation.setTextAnchor(TextAnchor.BOTTOM_RIGHT);
                labelAnnotation.draw(g2, plot, dataArea, ABSOLUTE_AXIS, ABSOLUTE_AXIS, rendererIndex, info);
                final XYTextAnnotation priceAnnotation = new XYTextAnnotation(rangeAxisFormat.format(closePrice),
                        x2 - 1D, y2 + 1D);
                priceAnnotation.setPaint(color);
                priceAnnotation.setFont(FONT);
                priceAnnotation.setTextAnchor(TextAnchor.TOP_RIGHT);
                priceAnnotation.draw(g2, plot, dataArea, ABSOLUTE_AXIS, ABSOLUTE_AXIS, rendererIndex, info);
            }
        }
    }

}
