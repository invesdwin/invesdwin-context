package de.invesdwin.context.jfreechart.icon;

import java.awt.Shape;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.entity.XYAnnotationEntity;

@Immutable
public class XYIconAnnotationEntity extends XYAnnotationEntity {

    private final XYIconAnnotation iconAnnotation;

    public XYIconAnnotationEntity(final XYIconAnnotation iconAnnotation, final Shape hotspot, final int rendererIndex,
            final String toolTipText, final String urlText) {
        super(hotspot, rendererIndex, toolTipText, urlText);
        this.iconAnnotation = iconAnnotation;
    }

    public XYIconAnnotation getIconAnnotation() {
        return iconAnnotation;
    }

}
