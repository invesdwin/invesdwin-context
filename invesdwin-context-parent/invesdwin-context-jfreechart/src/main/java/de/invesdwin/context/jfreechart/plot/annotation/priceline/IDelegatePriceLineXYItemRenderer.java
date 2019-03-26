package de.invesdwin.context.jfreechart.plot.annotation.priceline;

public interface IDelegatePriceLineXYItemRenderer extends IPriceLineXYItemRenderer {

    IPriceLineRenderer getDelegatePriceLineRenderer();

    @Override
    default boolean isPriceLabelEnabled() {
        return getDelegatePriceLineRenderer().isPriceLabelEnabled();
    }

    @Override
    default boolean isPriceLineVisible() {
        return getDelegatePriceLineRenderer().isPriceLineVisible();
    }

    @Override
    default void setPriceLabelEnabled(final boolean priceLabelEnabled) {
        getDelegatePriceLineRenderer().setPriceLabelEnabled(priceLabelEnabled);
    }

    @Override
    default void setPriceLineVisible(final boolean visible) {
        getDelegatePriceLineRenderer().setPriceLineVisible(visible);
    }

}
