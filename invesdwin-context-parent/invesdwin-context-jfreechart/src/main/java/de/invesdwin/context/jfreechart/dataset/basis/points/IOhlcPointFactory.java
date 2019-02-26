package de.invesdwin.context.jfreechart.dataset.basis.points;

public interface IOhlcPointFactory<E extends IOhlcPoint> {

    E newCopy(IOhlcPoint values);

}
