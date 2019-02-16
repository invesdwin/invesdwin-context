package de.invesdwin.context.jfreechart.dataset.data;

public interface IOhlcPointFactory<E extends IOhlcPoint> {

    E newCopy(IOhlcPoint values);

}
