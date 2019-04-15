package de.invesdwin.context.jfreechart.dataset;

import javax.annotation.concurrent.Immutable;

import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.XYDataItem;

@Immutable
public class XYDataItemOHLC extends XYDataItem {

    private final OHLCDataItem ohlc;

    public XYDataItemOHLC(final OHLCDataItem ohlc) {
        super(ohlc.getDate().getTime(), ohlc.getClose());
        this.ohlc = ohlc;
    }

    public OHLCDataItem asOHLC() {
        return ohlc;
    }

}
