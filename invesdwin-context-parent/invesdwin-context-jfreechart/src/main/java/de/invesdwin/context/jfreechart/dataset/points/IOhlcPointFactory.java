package de.invesdwin.context.jfreechart.dataset.points;

public interface IOhlcPointFactory<E extends IOhlcPoint> {

    E newCopy(IOhlcPoint values);

}
