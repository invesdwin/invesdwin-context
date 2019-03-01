package de.invesdwin.context.jfreechart.plot.annotation;

import java.awt.Shape;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.entity.XYAnnotationEntity;

@Immutable
public class XYNoteIconAnnotationEntity extends XYAnnotationEntity {

    private final XYNoteIconAnnotation noteIconAnnotation;

    public XYNoteIconAnnotationEntity(final XYNoteIconAnnotation iconAnnotation, final Shape hotspot,
            final int rendererIndex, final String toolTipText, final String urlText) {
        super(hotspot, rendererIndex, toolTipText, urlText);
        this.noteIconAnnotation = iconAnnotation;
    }

    public XYNoteIconAnnotation getNoteIconAnnotation() {
        return noteIconAnnotation;
    }

    public String getNote() {
        return noteIconAnnotation.getNote();
    }

}
