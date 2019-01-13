package de.invesdwin.context.jfreechart.visitor;

import java.awt.Font;

import javax.annotation.concurrent.Immutable;

@Immutable
public class JFreeChartFontMultiplier extends AJFreeChartVisitor {

    private final double fontMultiplier;

    public JFreeChartFontMultiplier(final double fontMultiplier) {
        this.fontMultiplier = fontMultiplier;
    }

    @Override
    protected Font processFont(final Font font) {
        if (fontMultiplier == 1D) {
            return font;
        } else {
            return font.deriveFont(font.getStyle(), (int) (font.getSize() * fontMultiplier));
        }
    }

}
