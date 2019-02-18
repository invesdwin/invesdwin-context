package de.invesdwin.context.jfreechart.panel.helper.icons;

import java.net.URL;

import javax.annotation.concurrent.Immutable;
import javax.swing.ImageIcon;

import de.invesdwin.context.jfreechart.icon.AlphaImageIcon;

@Immutable
public enum PlotIcons {
    //navigation
    PAN_LEFT("panLeft_128.png"),
    ZOOM_OUT("zoomOut_128.png"),
    RESET("reset_128.png"),
    ZOOM_IN("zoomIn_128.png"),
    PAN_RIGHT("panRight_128.png"),

    //legend
    ADD("add_256.png"),
    REMOVE("remove_256.png"),
    TRASH("trash_256.png");

    private String name;

    PlotIcons(final String name) {
        this.name = name;
    }

    public ImageIcon newIcon(final int size, final float alpha) {
        final URL resource = PlotIcons.class.getResource(name);
        if (resource == null) {
            throw new IllegalArgumentException(name + " not found");
        }
        final ImageIcon icon = new ImageIcon(resource);
        final ImageIcon scaled = new ImageIcon(
                icon.getImage().getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH));
        return new AlphaImageIcon(scaled, alpha);
    }

}
