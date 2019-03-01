package de.invesdwin.context.jfreechart.plot.annotation;

import java.awt.Shape;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.Icon;

import org.jfree.chart.entity.XYAnnotationEntity;
import org.jfree.chart.ui.RectangleAnchor;

@NotThreadSafe
public class XYNoteIconAnnotation extends XYIconAnnotation {

    private final XYNoteAnnotation noteAnnotation;

    public XYNoteIconAnnotation(final double x, final double y, final Icon icon) {
        this(x, y, icon, RectangleAnchor.BOTTOM);
    }

    public XYNoteIconAnnotation(final double x, final double y, final Icon icon, final RectangleAnchor anchor) {
        super(x, y, icon, anchor);
        noteAnnotation = new XYNoteAnnotation(this);
    }

    public String getNote() {
        return noteAnnotation.getTitle().getText();
    }

    public void setNote(final String note) {
        noteAnnotation.getTitle().setText(note);
    }

    @Override
    protected XYAnnotationEntity newEntity(final Shape hotspot, final int rendererIndex, final String toolTipText,
            final String urlText) {
        return new XYNoteIconAnnotationEntity(this, hotspot, rendererIndex, toolTipText, urlText);
    }

    public XYNoteAnnotation getNoteAnnotation() {
        return noteAnnotation;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj == this;
    }

}