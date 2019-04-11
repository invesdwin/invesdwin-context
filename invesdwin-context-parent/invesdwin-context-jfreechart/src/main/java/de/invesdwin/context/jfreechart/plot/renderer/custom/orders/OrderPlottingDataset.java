package de.invesdwin.context.jfreechart.plot.renderer.custom.orders;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.AbstractXYDataset;

import de.invesdwin.context.jfreechart.panel.helper.config.series.expression.IExpressionSeriesProvider;
import de.invesdwin.context.jfreechart.panel.helper.config.series.indicator.IIndicatorSeriesProvider;
import de.invesdwin.context.jfreechart.plot.dataset.IIndexedDateTimeXYDataset;
import de.invesdwin.context.jfreechart.plot.dataset.IPlotSourceDataset;
import de.invesdwin.context.jfreechart.plot.dataset.IndexedDateTimeOHLCDataset;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.iterable.ASkippingIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.math.expression.IExpression;

@NotThreadSafe
public class OrderPlottingDataset extends AbstractXYDataset implements IPlotSourceDataset, IIndexedDateTimeXYDataset {

    private final String seriesKey;
    private String seriesTitle;
    private final IndexedDateTimeOHLCDataset ohlcDataset;
    private final Set<DatasetChangeListener> changeListeners = new LinkedHashSet<>();
    private Integer precision;
    private XYPlot plot;
    private DatasetGroup group;
    private String rangeAxisId;
    private final Map<String, OrderPlottingDataItem> orderId_item = new LinkedHashMap<>();
    private boolean lastTradeProfit = true;
    private IIndicatorSeriesProvider indicatorSeriesProvider;
    private IExpression[] indicatorSeriesArguments;
    private IExpressionSeriesProvider expressionSeriesProvider;
    private String expressionSeriesArguments;

    public OrderPlottingDataset(final String seriesKey, final IndexedDateTimeOHLCDataset ohlcDataset) {
        Assertions.checkNotNull(seriesKey);
        this.seriesKey = seriesKey;
        this.seriesTitle = seriesKey;
        this.ohlcDataset = ohlcDataset;
    }

    @Override
    public void addChangeListener(final DatasetChangeListener listener) {
        changeListeners.add(listener);
    }

    @Override
    public void removeChangeListener(final DatasetChangeListener listener) {
        changeListeners.remove(listener);
    }

    @Override
    public DatasetGroup getGroup() {
        return group;
    }

    @Override
    public void setGroup(final DatasetGroup group) {
        this.group = group;
    }

    @Override
    public XYPlot getPlot() {
        return plot;
    }

    @Override
    public void setPlot(final XYPlot plot) {
        this.plot = plot;
    }

    @Override
    public Integer getPrecision() {
        return precision;
    }

    @Override
    public void setPrecision(final Integer precision) {
        this.precision = precision;
    }

    @Override
    public String getRangeAxisId() {
        return rangeAxisId;
    }

    @Override
    public void setRangeAxisId(final String rangeAxisId) {
        this.rangeAxisId = rangeAxisId;
    }

    @Override
    public int getItemCount(final int series) {
        return ohlcDataset.getItemCount(series);
    }

    @Override
    public Number getX(final int series, final int item) {
        return ohlcDataset.getX(series, item);
    }

    @Override
    public double getXValue(final int series, final int item) {
        return ohlcDataset.getXValue(series, item);
    }

    @Override
    public Number getY(final int series, final int item) {
        //need to return the price here in orger to get axis scaling done properly
        return ohlcDataset.getY(series, item);
    }

    @Override
    public double getYValue(final int series, final int item) {
        return ohlcDataset.getYValue(series, item);
    }

    @Override
    public int getSeriesCount() {
        return 1;
    }

    @Override
    public String getSeriesKey(final int series) {
        return seriesKey;
    }

    @Override
    public double getXValueAsDateTime(final int series, final int item) {
        return ohlcDataset.getXValueAsDateTime(series, item);
    }

    @Override
    public int getDateTimeAsItemIndex(final int series, final Date time) {
        return ohlcDataset.getDateTimeAsItemIndex(series, time);
    }

    public void addOrUpdate(final OrderPlottingDataItem item) {
        orderId_item.put(item.getOrderId(), item);
        lastTradeProfit = item.isProfit();
    }

    public void remove(final String orderId) {
        orderId_item.remove(orderId);
    }

    @Override
    public boolean isLegendValueVisible(final int series, final int item) {
        return false;
    }

    public ICloseableIterable<OrderPlottingDataItem> getVisibleItems(final int firstItem, final int lastItem) {
        return new ASkippingIterable<OrderPlottingDataItem>(WrapperCloseableIterable.maybeWrap(orderId_item.values())) {
            @Override
            protected boolean skip(final OrderPlottingDataItem element) {
                return element.getOpenTimeIndex() > lastItem || element.getCloseTimeIndex() < firstItem;
            }
        };
    }

    public boolean isLastTradeProfit() {
        return lastTradeProfit;
    }

    public IndexedDateTimeOHLCDataset getOhlcDataset() {
        return ohlcDataset;
    }

    @Override
    public String getSeriesTitle() {
        return seriesTitle;
    }

    @Override
    public void setSeriesTitle(final String seriesTitle) {
        this.seriesTitle = seriesTitle;
    }

    @Override
    public IIndicatorSeriesProvider getIndicatorSeriesProvider() {
        return indicatorSeriesProvider;
    }

    @Override
    public void setIndicatorSeriesProvider(final IIndicatorSeriesProvider indicatorSeriesProvider) {
        this.indicatorSeriesProvider = indicatorSeriesProvider;
    }

    @Override
    public void setIndicatorSeriesArguments(final IExpression[] indicatorSeriesArguments) {
        this.indicatorSeriesArguments = indicatorSeriesArguments;
    }

    @Override
    public IExpression[] getIndicatorSeriesArguments() {
        return indicatorSeriesArguments;
    }

    @Override
    public IExpressionSeriesProvider getExpressionSeriesProvider() {
        return expressionSeriesProvider;
    }

    @Override
    public void setExpressionSeriesProvider(final IExpressionSeriesProvider expressionSeriesProvider) {
        this.expressionSeriesProvider = expressionSeriesProvider;
    }

    @Override
    public String getExpressionSeriesArguments() {
        return expressionSeriesArguments;
    }

    @Override
    public void setExpressionSeriesArguments(final String expressionSeriesArguments) {
        this.expressionSeriesArguments = expressionSeriesArguments;
    }

}
