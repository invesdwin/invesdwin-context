package de.invesdwin.context.jfreechart.plot.annotation;

import java.awt.Shape;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.entity.XYAnnotationEntity;

@Immutable
public class XYNoteAnnotationEntity extends XYAnnotationEntity {

    private final XYNoteAnnotation noteAnnotation;

    public XYNoteAnnotationEntity(final XYNoteAnnotation iconAnnotation, final Shape hotspot, final int rendererIndex,
            final String toolTipText, final String urlText) {
        super(hotspot, rendererIndex, toolTipText, urlText);
        this.noteAnnotation = iconAnnotation;
    }

    public XYNoteAnnotation getNoteAnnotation() {
        return noteAnnotation;
    }

    public String getNote() {
        return noteAnnotation.getTitle().getText();
    }

}
