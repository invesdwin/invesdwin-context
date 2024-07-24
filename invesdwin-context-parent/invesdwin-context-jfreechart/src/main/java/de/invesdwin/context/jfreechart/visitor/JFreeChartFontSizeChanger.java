package de.invesdwin.context.jfreechart.visitor;

import java.awt.Font;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.swing.Fonts;

@Immutable
public class JFreeChartFontSizeChanger extends AJFreeChartVisitor {

    private final int fixedSize;

    public JFreeChartFontSizeChanger(final int fixedSize) {
        this.fixedSize = fixedSize;
    }

    @Override
    public Font processFont(final Font font) {
        return Fonts.resizeFont(font, fixedSize);
    }

}
