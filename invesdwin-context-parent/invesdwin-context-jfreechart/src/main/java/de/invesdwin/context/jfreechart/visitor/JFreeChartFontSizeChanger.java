package de.invesdwin.context.jfreechart.visitor;

import java.awt.Font;

import javax.annotation.concurrent.Immutable;

@Immutable
public class JFreeChartFontSizeChanger extends AJFreeChartVisitor {

    private final int fixedSize;

    public JFreeChartFontSizeChanger(final int fixedSize) {
        this.fixedSize = fixedSize;
    }

    @Override
    protected Font processFont(final Font font) {
        return changeFontSize(font, fixedSize);
    }

    public static Font changeFontSize(final Font font, final int fixedSize) {
        if (font.getSize() != fixedSize) {
            return font.deriveFont(font.getStyle(), fixedSize);
        } else {
            return font;
        }
    }

}
