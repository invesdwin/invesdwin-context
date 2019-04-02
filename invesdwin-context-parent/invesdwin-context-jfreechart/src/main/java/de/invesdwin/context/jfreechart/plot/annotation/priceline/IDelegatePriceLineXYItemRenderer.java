package de.invesdwin.context.jfreechart.plot.annotation.priceline;

import de.invesdwin.context.jfreechart.plot.renderer.IDatasetSourceXYItemRenderer;

public interface IDelegatePriceLineXYItemRenderer extends IPriceLineXYItemRenderer, IDatasetSourceXYItemRenderer {

    IPriceLineRenderer getDelegatePriceLineRenderer();

    @Override
    default boolean isPriceLabelVisible() {
        return getDelegatePriceLineRenderer().isPriceLabelVisible();
    }

    @Override
    default boolean isPriceLineVisible() {
        return getDelegatePriceLineRenderer().isPriceLineVisible();
    }

    @Override
    default void setPriceLabelVisible(final boolean priceLabelVisible) {
        getDelegatePriceLineRenderer().setPriceLabelVisible(priceLabelVisible);
    }

    @Override
    default void setPriceLineVisible(final boolean priceLineVisible) {
        getDelegatePriceLineRenderer().setPriceLineVisible(priceLineVisible);
    }

}
