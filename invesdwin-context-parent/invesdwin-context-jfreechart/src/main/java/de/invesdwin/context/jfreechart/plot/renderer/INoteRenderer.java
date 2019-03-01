package de.invesdwin.context.jfreechart.plot.renderer;

import java.util.Collection;

import de.invesdwin.context.jfreechart.plot.annotation.XYNoteIconAnnotation;

public interface INoteRenderer {

    Collection<XYNoteIconAnnotation> getVisibleNoteIcons();

}
