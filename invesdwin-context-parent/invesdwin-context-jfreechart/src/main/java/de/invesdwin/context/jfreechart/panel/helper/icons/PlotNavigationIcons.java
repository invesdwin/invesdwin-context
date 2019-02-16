package de.invesdwin.context.jfreechart.panel.helper.icons;

import javax.annotation.concurrent.Immutable;
import javax.swing.ImageIcon;

import de.invesdwin.context.jfreechart.icon.AlphaImageIcon;

@Immutable
public enum PlotNavigationIcons {
    PAN_LEFT("panLeft.png"),
    ZOOM_OUT("zoomOut.png"),
    RESET("reset.png"),
    ZOOM_IN("zoomIn.png"),
    PAN_RIGHT("panRight.png");

    private String name;

    PlotNavigationIcons(final String name) {
        this.name = name;
    }

    public ImageIcon newIcon(final int size, final float alpha) {
        final ImageIcon icon = new ImageIcon(PlotNavigationIcons.class.getResource(name));
        final ImageIcon scaled = new ImageIcon(
                icon.getImage().getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH));
        return new AlphaImageIcon(scaled, alpha);
    }

}
