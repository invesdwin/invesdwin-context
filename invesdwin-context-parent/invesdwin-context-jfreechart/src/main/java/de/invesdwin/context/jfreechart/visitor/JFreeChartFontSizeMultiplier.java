package de.invesdwin.context.jfreechart.visitor;

import java.awt.Font;

import javax.annotation.concurrent.Immutable;

@Immutable
public class JFreeChartFontSizeMultiplier extends AJFreeChartVisitor {

    private final double fontMultiplier;

    public JFreeChartFontSizeMultiplier(final double fontMultiplier) {
        this.fontMultiplier = fontMultiplier;
    }

    @Override
    public Font processFont(final Font font) {
        return multiplyFont(font, fontMultiplier);
    }

    public static Font multiplyFont(final Font font, final double fontMultiplier) {
        if (fontMultiplier == 1D) {
            return font;
        } else {
            return font.deriveFont(font.getStyle(), (int) (font.getSize() * fontMultiplier));
        }
    }

}
