package de.invesdwin.context.report.jfreechart.data;

public interface IOhlcPointFactory<E extends IOhlcPoint> {

    E newCopy(IOhlcPoint values);

}
