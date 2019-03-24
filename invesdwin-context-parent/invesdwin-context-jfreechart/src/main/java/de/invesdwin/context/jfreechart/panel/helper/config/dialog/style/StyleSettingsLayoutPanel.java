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
        lbl_priceRenderer = new JLabel("Series Type");
        cmb_priceRenderer = new JComboBox<>();
        lbl_seriesRenderer = new JLabel("Series Type");
        cmb_seriesRenderer = new JComboBox<>();
        lbl_lineStyle = new JLabel("Line Style");
        cmb_lineStyle = new JComboBox<LineStyleType>();
        lbl_lineWidth = new JLabel("Line Width");
        cmb_lineWidth = new JComboBox<LineWidthType>();
        lbl_seriesColor = new JLabel("Series Color");
        btn_seriesColor = new JButton();
        lbl_upColor = new JLabel("Up Color");
        btn_upColor = new JButton();
        lbl_downColor = new JLabel("Down Color");
        btn_downColor = new JButton();
        lbl_priceLine = new JLabel("Price Line");
        chk_priceLine = new JCheckBox();
        btn_resetStyle = new JButton("Reset Style");

        add(lbl_priceRenderer);
        add(cmb_priceRenderer);
        add(lbl_seriesRenderer);
        add(cmb_seriesRenderer);
        add(lbl_lineStyle);
        add(cmb_lineStyle);
        add(lbl_lineWidth);
        add(cmb_lineWidth);
        add(lbl_seriesColor);
        add(btn_seriesColor);
        add(lbl_upColor);
        add(btn_upColor);
        add(lbl_downColor);
        add(btn_downColor);
        add(lbl_priceLine);
        add(chk_priceLine);
        add(new JPanel());
        add(btn_resetStyle);

        setLayout(new GridLayout(9, 2, 5, 5));
    }

    //CHECKSTYLE:OFF
    public void updateLayout() {
        //CHECKSTYLE:ON
        removeAll();
        int rows = 0;
        if (cmb_priceRenderer.isVisible()) {
            add(lbl_priceRenderer);
            add(cmb_priceRenderer);
            rows++;
        }
        if (cmb_seriesRenderer.isVisible()) {
            add(lbl_seriesRenderer);
            add(cmb_seriesRenderer);
            rows++;
        }
        if (cmb_lineStyle.isVisible()) {
            add(lbl_lineStyle);
            add(cmb_lineStyle);
            rows++;
        }
        if (cmb_lineWidth.isVisible()) {
            add(lbl_lineWidth);
            add(cmb_lineWidth);
            rows++;
        }
        if (btn_seriesColor.isVisible()) {
            add(lbl_seriesColor);
            add(btn_seriesColor);
            rows++;
        }
        if (btn_upColor.isVisible()) {
            add(lbl_upColor);
            add(btn_upColor);
            rows++;
        }
        if (btn_downColor.isVisible()) {
            add(lbl_downColor);
            add(btn_downColor);
            rows++;
        }
        if (chk_priceLine.isVisible()) {
            add(lbl_priceLine);
            add(chk_priceLine);
            rows++;
        }
        if (btn_resetStyle.isVisible()) {
            add(new JPanel());
            add(btn_resetStyle);
            rows++;
        }

        setLayout(new GridLayout(rows, 2, 5, 5));
    }

}
