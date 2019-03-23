package de.invesdwin.context.jfreechart.panel.helper.config.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;

import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;

import de.invesdwin.context.jfreechart.panel.helper.config.IRendererType;
import de.invesdwin.context.jfreechart.panel.helper.config.LineStyleType;
import de.invesdwin.context.jfreechart.panel.helper.config.LineWidthType;
import de.invesdwin.context.jfreechart.panel.helper.config.PlotConfigurationHelper;
import de.invesdwin.context.jfreechart.panel.helper.config.PriceRendererType;
import de.invesdwin.context.jfreechart.panel.helper.config.SeriesInitialSettings;
import de.invesdwin.context.jfreechart.panel.helper.config.SeriesRendererType;
import de.invesdwin.context.jfreechart.panel.helper.legend.HighlightedLegendInfo;
import de.invesdwin.context.jfreechart.plot.annotation.priceline.IPriceLineRenderer;
import de.invesdwin.context.jfreechart.plot.renderer.IUpDownColorRenderer;
import de.invesdwin.context.jfreechart.plot.renderer.custom.ICustomRendererType;
import de.invesdwin.util.swing.ColorChooserButton;
import de.invesdwin.util.swing.Dialogs;
import de.invesdwin.util.swing.listener.IColorChooserListener;

@NotThreadSafe
public class StyleSettingsPanel extends JPanel {

    private final PlotConfigurationHelper plotConfigurationHelper;
    private final HighlightedLegendInfo highlighted;

    private JComboBox<String> cmb_priceRenderer;
    private JComboBox<String> cmb_seriesRenderer;
    private JComboBox<LineStyleType> cmb_lineStyle;
    private JComboBox<LineWidthType> cmb_lineWidth;
    private JButton btn_seriesColor;
    private JButton btn_upColor;
    private JButton btn_downColor;
    private JButton btn_resetStyle;
    private JCheckBox chk_showPriceLine;

    public StyleSettingsPanel(final PlotConfigurationHelper plotConfigurationHelper,
            final HighlightedLegendInfo highlighted) {
        this.plotConfigurationHelper = plotConfigurationHelper;
        this.highlighted = highlighted;

        initRenderer();
        initLine();
        initColors();
        initShowPriceLine();
        initResetStyle();
        updateRendererVisibility();
        updateLineVisibility();

        popupMenu.add(priceRendererItem);
        popupMenu.add(seriesRendererItem);
        add(cmb_lineStyle);
        add(cmb_lineWidth);
        add(btn_seriesColor);
        add(btn_upColor);
        add(btn_downColor);
        add(chk_showPriceLine);
        add(btn_resetStyle);
    }

    private void initResetStyle() {
        btn_resetStyle = new JButton("Reset Style");
        btn_resetStyle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (highlighted.isPriceSeries()) {
                    plotConfigurationHelper.getPriceInitialSettings().reset();
                } else {
                    plotConfigurationHelper.getSeriesInitialSettings().reset(highlighted);
                }
            }
        });
    }

    private void updateRendererVisibility() {
        if (highlighted.isPriceSeries()) {
            priceRendererItem.setVisible(true);
            seriesRendererItem.setVisible(false);
        } else {
            final SeriesInitialSettings initialSeriesSettings = getOrCreateSeriesInitialSettings();
            if (initialSeriesSettings.isCustomSeriesType()) {
                final ICustomRendererType customRendererType = (ICustomRendererType) initialSeriesSettings
                        .getRendererType();
                customRendererItem.setVisible(true);
                customRendererItem.setText(customRendererType.getName());
            } else {
                customRendererItem.setVisible(false);
            }
            priceRendererItem.setVisible(false);
            seriesRendererItem.setVisible(true);
        }

        if (priceRendererItem.isVisible()) {
            final PriceRendererType rendererType = priceInitialSettings.getCurrentPriceRendererType();
            for (final Component component : priceRendererItem.getMenuComponents()) {
                final JRadioButtonMenuItem menuItem = (JRadioButtonMenuItem) component;
                if (menuItem.getName().equals(rendererType.name())) {
                    menuItem.setSelected(true);
                }
            }
            updateStyleVisibility(rendererType);
        }
        if (seriesRendererItem.isVisible()) {
            final IRendererType rendererType = getSeriesInitialSettings().getCurrentRendererType(highlighted);
            for (final Component component : seriesRendererItem.getMenuComponents()) {
                final JRadioButtonMenuItem menuItem = (JRadioButtonMenuItem) component;
                if (menuItem.getName().equals(rendererType.getSeriesRendererType().name())) {
                    menuItem.setSelected(true);
                }
            }
            seriesRendererItem.setVisible(rendererType.isSeriesRendererTypeConfigurable());
            updateStyleVisibility(rendererType);
        }
    }

    private void updateStyleVisibility(final IRendererType rendererType) {
        cmb_lineStyle.setVisible(rendererType.isLineStyleConfigurable());
        cmb_lineWidth.setVisible(rendererType.isLineWidthConfigurable());
        btn_seriesColor.setVisible(rendererType.isSeriesColorConfigurable());
        btn_seriesColor.setText("Change " + rendererType.getSeriesColorName() + " Color");
        btn_seriesColor.setName(rendererType.getSeriesColorName());
        btn_upColor.setVisible(rendererType.isUpColorConfigurable());
        btn_upColor.setText("Change " + rendererType.getUpColorName() + " Color");
        btn_upColor.setName(rendererType.getUpColorName());
        btn_downColor.setVisible(rendererType.isDownColorConfigurable());
        btn_downColor.setText("Change " + rendererType.getDownColorName() + " Color");
        btn_downColor.setName(rendererType.getDownColorName());
        chk_showPriceLine.setVisible(rendererType.isPriceLineConfigurable());
        chk_showPriceLine.setSelected(highlighted.isPriceLineVisible());
    }

    private void updateLineVisibility() {
        if (cmb_lineStyle.isVisible()) {
            final XYItemRenderer renderer = highlighted.getRenderer();
            final LineStyleType lineStyleType = LineStyleType.valueOf(renderer.getSeriesStroke(0));
            cmb_lineStyle.setSelectedItem(lineStyleType);
        }
        if (cmb_lineWidth.isVisible()) {
            final XYItemRenderer renderer = highlighted.getRenderer();
            final LineWidthType lineWidthType = LineWidthType.valueOf(renderer.getSeriesStroke(0));
            cmb_lineWidth.setSelectedItem(lineWidthType);
        }
    }

    private void initRenderer() {
        priceRendererItem = new JMenu("Change Series Type");
        final ButtonGroup priceRendererGroup = new ButtonGroup();
        for (final PriceRendererType type : PriceRendererType.values()) {
            final JRadioButtonMenuItem item = new JRadioButtonMenuItem(type.toString());
            item.setName(type.name());
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    priceInitialSettings.updatePriceRendererType(type);
                }
            });
            priceRendererGroup.add(item);
            priceRendererItem.add(item);
        }

        seriesRendererItem = new JMenu("Change Series Type");
        final ButtonGroup seriesRendererGroup = new ButtonGroup();
        for (final SeriesRendererType type : SeriesRendererType.values()) {
            if (type == SeriesRendererType.Custom) {
                continue;
            }
            final JRadioButtonMenuItem item = new JRadioButtonMenuItem(type.toString());
            item.setName(type.name());
            item.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final XYItemRenderer renderer = highlighted.getRenderer();
                    final Stroke stroke = renderer.getSeriesStroke(0);
                    final LineStyleType lineStyleType = LineStyleType.valueOf(stroke);
                    final LineWidthType lineWidthType = LineWidthType.valueOf(stroke);
                    final Color color = (Color) renderer.getSeriesPaint(0);
                    final XYDataset dataset = highlighted.getDataset();
                    final boolean priceLineVisible = highlighted.isPriceLineVisible();
                    highlighted.setRenderer(
                            type.newRenderer(dataset, lineStyleType, lineWidthType, color, priceLineVisible));
                }
            });
            seriesRendererGroup.add(item);
            seriesRendererItem.add(item);
        }
        customRendererItem = new JRadioButtonMenuItem(SeriesRendererType.Custom.toString());
        customRendererItem.setName(SeriesRendererType.Custom.name());
        customRendererItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                final XYItemRenderer renderer = highlighted.getRenderer();
                final ICustomRendererType customRenderer = (ICustomRendererType) getSeriesInitialSettings()
                        .getRendererType();
                customRenderer.setSeriesPaint(0, renderer.getSeriesPaint(0));
                customRenderer.setSeriesStroke(0, renderer.getSeriesStroke(0));
                if (customRenderer.isPriceLineConfigurable()
                        && getSeriesInitialSettings().getRendererType().isPriceLineConfigurable()) {
                    final IPriceLineRenderer customPriceLineRenderer = (IPriceLineRenderer) customRenderer;
                    customPriceLineRenderer.setPriceLineVisible(highlighted.isPriceLineVisible());
                }
                highlighted.setRenderer(customRenderer);
            }
        });
        seriesRendererGroup.add(customRendererItem);
        seriesRendererItem.add(customRendererItem);
    }

    private void initLine() {
        cmb_lineStyle = new JComboBox<LineStyleType>();
        cmb_lineStyle.setName("Change Line Style");
        for (final LineStyleType type : LineStyleType.values()) {
            cmb_lineStyle.addItem(type);
        }
        cmb_lineStyle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final XYItemRenderer renderer = highlighted.getRenderer();
                final Stroke stroke = renderer.getSeriesStroke(0);
                final LineStyleType selectedItem = (LineStyleType) cmb_lineWidth.getSelectedItem();
                renderer.setSeriesStroke(0, selectedItem.getStroke(stroke));
            }
        });

        cmb_lineWidth = new JComboBox<LineWidthType>();
        cmb_lineWidth.setName("Change Line Width");
        for (final LineWidthType type : LineWidthType.values()) {
            cmb_lineWidth.addItem(type);
        }
        cmb_lineWidth.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final XYItemRenderer renderer = highlighted.getRenderer();
                final Stroke stroke = renderer.getSeriesStroke(0);
                final LineWidthType selectedItem = (LineWidthType) cmb_lineWidth.getSelectedItem();
                renderer.setSeriesStroke(0, selectedItem.getStroke(stroke));
            }

        });
    }

    private void initColors() {
        final XYItemRenderer renderer = highlighted.getRenderer();
        btn_seriesColor = new ColorChooserButton((Color) renderer.getSeriesPaint(0)) {
            @Override
            protected void chooseColor() {
                showColorChooserDialog(getName() + " Color", getSelectedColor(), this);
            }

            @Override
            public void change(final Color initialColor, final Color newColor) {
                super.change(initialColor, newColor);
                renderer.setSeriesPaint(0, newColor);
            }

            @Override
            public void ok(final Color initialColor, final Color acceptedColor) {
                super.ok(initialColor, acceptedColor);
                renderer.setSeriesPaint(0, acceptedColor);
            }

            @Override
            public void cancel(final Color initialColor, final Color cancelledColor) {
                super.cancel(initialColor, cancelledColor);
                renderer.setSeriesPaint(0, initialColor);
            }
        };
        if (renderer instanceof IUpDownColorRenderer) {
            final IUpDownColorRenderer upDownRenderer = (IUpDownColorRenderer) renderer;
            btn_upColor = new ColorChooserButton(upDownRenderer.getUpColor()) {
                @Override
                protected void chooseColor() {
                    showColorChooserDialog(getName() + " Color", getSelectedColor(), this);
                }

                @Override
                public void change(final Color initialColor, final Color newColor) {
                    super.change(initialColor, newColor);
                    upDownRenderer.setUpColor(newColor);
                }

                @Override
                public void ok(final Color initialColor, final Color acceptedColor) {
                    super.ok(initialColor, acceptedColor);
                    upDownRenderer.setUpColor(acceptedColor);
                }

                @Override
                public void cancel(final Color initialColor, final Color cancelledColor) {
                    super.cancel(initialColor, cancelledColor);
                    upDownRenderer.setUpColor(initialColor);
                }
            };
            btn_downColor = new ColorChooserButton(upDownRenderer.getDownColor()) {
                @Override
                protected void chooseColor() {
                    showColorChooserDialog(getName() + " Color", getSelectedColor(), this);
                }

                @Override
                public void change(final Color initialColor, final Color newColor) {
                    super.change(initialColor, newColor);
                    upDownRenderer.setDownColor(newColor);
                }

                @Override
                public void ok(final Color initialColor, final Color acceptedColor) {
                    super.ok(initialColor, acceptedColor);
                    upDownRenderer.setDownColor(acceptedColor);
                }

                @Override
                public void cancel(final Color initialColor, final Color cancelledColor) {
                    super.cancel(initialColor, cancelledColor);
                    upDownRenderer.setDownColor(initialColor);
                }
            };
        } else {
            btn_upColor = new JButton();
            btn_downColor = new JButton();
        }
    }

    private void initShowPriceLine() {
        chk_showPriceLine = new JCheckBox("Show Price Line");
        chk_showPriceLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                highlighted.setPriceLineVisible(chk_showPriceLine.isSelected());
            }
        });
    }

    protected void showColorChooserDialog(final String name, final Color initialColor,
            final IColorChooserListener listener) {
        Dialogs.showColorChooserDialog(plotConfigurationHelper.getChartPanel(), name, initialColor, true, listener);
    }

}
