package de.invesdwin.context.jfreechart.panel.helper.config.dialog.style;

import java.awt.GridLayout;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.invesdwin.context.jfreechart.panel.helper.config.LineStyleType;
import de.invesdwin.context.jfreechart.panel.helper.config.LineWidthType;
import de.invesdwin.context.jfreechart.panel.helper.config.PriceRendererType;

@NotThreadSafe
public class StyleSettingsLayoutPanel extends JPanel {

    //CHECKSTYLE:OFF
    public final JLabel lbl_priceRenderer;
    public final JComboBox<PriceRendererType> cmb_priceRenderer;
    public final JLabel lbl_seriesRenderer;
    public final JComboBox<SeriesRendererItem> cmb_seriesRenderer;
    public final JLabel lbl_lineStyle;
    public final JComboBox<LineStyleType> cmb_lineStyle;
    public final JLabel lbl_lineWidth;
    public final JComboBox<LineWidthType> cmb_lineWidth;
    public final JLabel lbl_seriesColor;
    public final JButton btn_seriesColor;
    public final JLabel lbl_upColor;
    public final JButton btn_upColor;
    public final JLabel lbl_downColor;
    public final JButton btn_downColor;
    public final JLabel lbl_priceLine;
    public final JCheckBox chk_priceLine;
    public final JButton btn_resetStyle;
    //CHECKSTYLE:ON

    public StyleSettingsLayoutPanel() {
        setLayout(new GridLayout(9, 2, 5, 5));

        lbl_priceRenderer = new JLabel("Series Type");
        add(lbl_priceRenderer);
        cmb_priceRenderer = new JComboBox<>();
        add(cmb_priceRenderer);
        lbl_seriesRenderer = new JLabel("Series Type");
        add(lbl_seriesRenderer);
        cmb_seriesRenderer = new JComboBox<>();
        add(cmb_seriesRenderer);
        lbl_lineStyle = new JLabel("Line Style");
        add(lbl_lineStyle);
        cmb_lineStyle = new JComboBox<LineStyleType>();
        add(cmb_lineStyle);
        lbl_lineWidth = new JLabel("Line Width");
        add(lbl_lineWidth);
        cmb_lineWidth = new JComboBox<LineWidthType>();
        add(cmb_lineWidth);
        lbl_seriesColor = new JLabel("Series Color");
        add(lbl_seriesColor);
        btn_seriesColor = new JButton();
        add(btn_seriesColor);
        lbl_upColor = new JLabel("Up Color");
        add(lbl_upColor);
        btn_upColor = new JButton();
        add(btn_upColor);
        lbl_downColor = new JLabel("Down Color");
        add(lbl_downColor);
        btn_downColor = new JButton();
        add(btn_downColor);
        lbl_priceLine = new JLabel("Price Line");
        add(lbl_priceLine);
        chk_priceLine = new JCheckBox();
        add(chk_priceLine);
        add(new JPanel());
        btn_resetStyle = new JButton("Reset Style");
        add(btn_resetStyle);
    }

}
