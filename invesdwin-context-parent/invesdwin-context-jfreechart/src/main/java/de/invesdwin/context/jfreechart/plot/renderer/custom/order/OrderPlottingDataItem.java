package de.invesdwin.context.jfreechart.plot.renderer.custom.order;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class OrderPlottingDataItem {

    private final double openPrice;
    private final int openTimeIndex;
    private final int closeTimeIndex;
    private final double closePrice;
    private final boolean closed;
    private final boolean profit;
    private final boolean pending;
    private final boolean tpsl;
    private final String orderId;
    private final String label;
    private final String note;

    //CHECKSTYLE:OFF
    public OrderPlottingDataItem(final double openPrice, final int openTimeIndex, final int closeTimeIndex,
            final double closePrice, final boolean closed, final boolean profit, final boolean pending,
            final boolean tpsl, final String orderId, final String label, final String note) {
        //CHECKSTYLE:ON
        this.openPrice = openPrice;
        this.openTimeIndex = openTimeIndex;
        this.closeTimeIndex = closeTimeIndex;
        this.closePrice = closePrice;
        this.closed = closed;
        this.profit = profit;
        this.pending = pending;
        this.tpsl = tpsl;
        this.orderId = orderId;
        this.label = label;
        this.note = note;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public int getOpenTimeIndex() {
        return openTimeIndex;
    }

    public int getCloseTimeIndex() {
        return closeTimeIndex;
    }

    public double getClosePrice() {
        return closePrice;
    }

    public boolean isClosed() {
        return closed;
    }

    public boolean isProfit() {
        return profit;
    }

    public boolean isPending() {
        return pending;
    }

    public boolean isTpsl() {
        return tpsl;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getLabel() {
        return label;
    }

    public String getNote() {
        return note;
    }

}
