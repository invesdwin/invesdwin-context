package de.invesdwin.context.jfreechart.data;

public interface IOhlcPointFactory<E extends IOhlcPoint> {

    E newCopy(IOhlcPoint values);

}
