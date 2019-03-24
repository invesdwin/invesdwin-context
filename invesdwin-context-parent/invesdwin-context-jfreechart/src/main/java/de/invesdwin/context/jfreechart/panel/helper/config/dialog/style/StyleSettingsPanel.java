package de.invesdwin.context.jfreechart.panel.helper.config.dialog.style;

import java.awt.Color;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;

import de.invesdwin.context.jfreechart.panel.helper.config.IRendererType;
import de.invesdwin.context.jfreechart.panel.helper.config.LineStyleType;
import de.invesdwin.context.jfreechart.panel.helper.config.LineWidthType;
import de.invesdwin.context.jfreechart.panel.helper.config.PlotConfigurationHelper;
import de.invesdwin.context.jfreechart.panel.helper.config.PriceInitialSettings;
import de.invesdwin.context.jfreechart.panel.helper.config.PriceRendererType;
import de.invesdwin.context.jfreechart.panel.helper.config.SeriesInitialSettings;
import de.invesdwin.context.jfreechart.panel.helper.config.SeriesRendererType;
import de.invesdwin.context.jfreechart.panel.helper.config.dialog.ISettingsPanelActions;
import de.invesdwin.context.jfreechart.panel.helper.legend.HighlightedLegendInfo;
import de.invesdwin.context.jfreechart.plot.annotation.priceline.IPriceLineRenderer;
import de.invesdwin.context.jfreechart.plot.renderer.IUpDownColorRenderer;
import de.invesdwin.context.jfreechart.plot.renderer.custom.ICustomRendererType;
import de.invesdwin.util.swing.ColorChooserButtonActionListener;

@NotThreadSafe
public class StyleSettingsPanel extends JPanel implements ISettingsPanelActions {

    private final PlotConfigurationHelper plotConfigurationHelper;
    private final HighlightedLegendInfo highlighted;
    private final JDialog dialog;
    private final PriceInitialSettings priceSettingsBefore;
    private final SeriesInitialSettings seriesSettingsBefore;

    private StyleSettingsPanelLayout panel;

    public StyleSettingsPanel(final PlotConfigurationHelper plotConfigurationHelper,
            final HighlightedLegendInfo highlighted, final JDialog dialog) {
        final TitledBorder titleBorder = new TitledBorder(null, "Style", TitledBorder.LEADING, TitledBorder.TOP, null,
                null);
        final EmptyBorder marginBorder = new EmptyBorder(10, 10, 10, 10);
        setBorder(new CompoundBorder(new CompoundBorder(marginBorder, titleBorder), marginBorder));

        this.plotConfigurationHelper = plotConfigurationHelper;
        this.highlighted = highlighted;
        this.dialog = dialog;

        if (highlighted.isPriceSeries()) {
            priceSettingsBefore = new PriceInitialSettings(plotConfigurationHelper.getPriceInitialSettings());
            seriesSettingsBefore = null;
        } else {
            priceSettingsBefore = null;
            seriesSettingsBefore = new SeriesInitialSettings(highlighted.getRenderer());
        }

        initPanel();
    }

    private void initPanel() {
        panel = new StyleSettingsPanelLayout();
        updateRendererVisibility();
        updateLineVisibility();
        updateLabelVisibility();
        initRenderer();
        initLine();
        initColors();
        initShowPriceLine();
        removeAll();
        add(panel);
    }

    protected void updateLayout() {}

    private void initRenderer() {
        if (highlighted.isPriceSeries()) {
            panel.cmb_priceRenderer.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final PriceRendererType selectedItem = (PriceRendererType) panel.cmb_priceRenderer
                            .getSelectedItem();
                    plotConfigurationHelper.getPriceInitialSettings().updatePriceRendererType(selectedItem);
                    initPanel();
                }
            });
        } else {
            panel.cmb_seriesRenderer.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final SeriesRendererItem selectedItem = (SeriesRendererItem) panel.cmb_seriesRenderer
                            .getSelectedItem();
                    final SeriesRendererType type = selectedItem.getType();
                    final XYItemRenderer renderer = highlighted.getRenderer();
                    final Color color = (Color) renderer.getSeriesPaint(0);
                    final Stroke stroke = renderer.getSeriesStroke(0);
                    if (type == SeriesRendererType.Custom) {
                        final ICustomRendererType customRenderer = (ICustomRendererType) plotConfigurationHelper
                                .getSeriesInitialSettings(highlighted)
                                .getRendererType();
                        customRenderer.setSeriesPaint(0, color);
                        customRenderer.setSeriesStroke(0, stroke);
                        if (customRenderer.isPriceLineConfigurable()
                                && plotConfigurationHelper.getSeriesInitialSettings(highlighted)
                                        .getRendererType()
                                        .isPriceLineConfigurable()) {
                            final IPriceLineRenderer customPriceLineRenderer = (IPriceLineRenderer) customRenderer;
                            customPriceLineRenderer.setPriceLineVisible(highlighted.isPriceLineVisible());
                        }
                        highlighted.setRenderer(customRenderer);
                    } else {
                        final LineStyleType lineStyleType = LineStyleType.valueOf(stroke);
                        final LineWidthType lineWidthType = LineWidthType.valueOf(stroke);
                        final XYDataset dataset = highlighted.getDataset();
                        final boolean priceLineVisible = highlighted.isPriceLineVisible();
                        highlighted.setRenderer(
                                type.newRenderer(dataset, lineStyleType, lineWidthType, color, priceLineVisible));
                    }
                    initPanel();
                }
            });
        }
    }

    private void initLine() {
        panel.cmb_lineStyle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final XYItemRenderer renderer = highlighted.getRenderer();
                final Stroke stroke = renderer.getSeriesStroke(0);
                final LineStyleType selectedItem = (LineStyleType) panel.cmb_lineStyle.getSelectedItem();
                renderer.setSeriesStroke(0, selectedItem.getStroke(stroke));
            }
        });

        panel.cmb_lineWidth.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final XYItemRenderer renderer = highlighted.getRenderer();
                final Stroke stroke = renderer.getSeriesStroke(0);
                final LineWidthType selectedItem = (LineWidthType) panel.cmb_lineWidth.getSelectedItem();
                renderer.setSeriesStroke(0, selectedItem.getStroke(stroke));
            }

        });
    }

    private void initColors() {
        panel.btn_seriesColor.addActionListener(new ColorChooserButtonActionListener(panel.btn_seriesColor,
                (Color) highlighted.getRenderer().getSeriesPaint(0)) {

            @Override
            protected String getChooserDialogTitle() {
                return panel.btn_seriesColor.getName() + " Color";
            }

            @Override
            protected int getIconWidth() {
                return super.getIconWidth() * 6;
            }

            @Override
            public void change(final Color initialColor, final Color newColor) {
                super.change(initialColor, newColor);
                final XYItemRenderer renderer = highlighted.getRenderer();
                renderer.setSeriesPaint(0, newColor);
            }

            @Override
            public void ok(final Color initialColor, final Color acceptedColor) {
                super.ok(initialColor, acceptedColor);
                final XYItemRenderer renderer = highlighted.getRenderer();
                renderer.setSeriesPaint(0, acceptedColor);
            }

            @Override
            public void cancel(final Color initialColor, final Color cancelledColor) {
                super.cancel(initialColor, cancelledColor);
                final XYItemRenderer renderer = highlighted.getRenderer();
                renderer.setSeriesPaint(0, initialColor);
            }
        });
        if (highlighted.getRenderer() instanceof IUpDownColorRenderer) {
            panel.btn_upColor.addActionListener(new ColorChooserButtonActionListener(panel.btn_upColor,
                    ((IUpDownColorRenderer) highlighted.getRenderer()).getUpColor()) {

                @Override
                protected String getChooserDialogTitle() {
                    return panel.btn_upColor.getName() + " Color";
                }

                @Override
                protected int getIconWidth() {
                    return super.getIconWidth() * 6;
                }

                @Override
                public void change(final Color initialColor, final Color newColor) {
                    super.change(initialColor, newColor);
                    final IUpDownColorRenderer upDownRenderer = (IUpDownColorRenderer) highlighted.getRenderer();
                    upDownRenderer.setUpColor(newColor);
                }

                @Override
                public void ok(final Color initialColor, final Color acceptedColor) {
                    super.ok(initialColor, acceptedColor);
                    final IUpDownColorRenderer upDownRenderer = (IUpDownColorRenderer) highlighted.getRenderer();
                    upDownRenderer.setUpColor(acceptedColor);
                }

                @Override
                public void cancel(final Color initialColor, final Color cancelledColor) {
                    super.cancel(initialColor, cancelledColor);
                    final IUpDownColorRenderer upDownRenderer = (IUpDownColorRenderer) highlighted.getRenderer();
                    upDownRenderer.setUpColor(initialColor);
                }
            });
            panel.btn_downColor.addActionListener(new ColorChooserButtonActionListener(panel.btn_downColor,
                    ((IUpDownColorRenderer) highlighted.getRenderer()).getDownColor()) {
                @Override
                protected String getChooserDialogTitle() {
                    return panel.btn_downColor.getName() + " Color";
                }

                @Override
                protected int getIconWidth() {
                    return super.getIconWidth() * 6;
                }

                @Override
                public void change(final Color initialColor, final Color newColor) {
                    super.change(initialColor, newColor);
                    final IUpDownColorRenderer upDownRenderer = (IUpDownColorRenderer) highlighted.getRenderer();
                    upDownRenderer.setDownColor(newColor);
                }

                @Override
                public void ok(final Color initialColor, final Color acceptedColor) {
                    super.ok(initialColor, acceptedColor);
                    final IUpDownColorRenderer upDownRenderer = (IUpDownColorRenderer) highlighted.getRenderer();
                    upDownRenderer.setDownColor(acceptedColor);
                }

                @Override
                public void cancel(final Color initialColor, final Color cancelledColor) {
                    super.cancel(initialColor, cancelledColor);
                    final IUpDownColorRenderer upDownRenderer = (IUpDownColorRenderer) highlighted.getRenderer();
                    upDownRenderer.setDownColor(initialColor);
                }
            });
        }
    }

    private void initShowPriceLine() {
        panel.chk_priceLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                highlighted.setPriceLineVisible(panel.chk_priceLine.isSelected());
            }
        });
    }

    private void updateRendererVisibility() {
        if (highlighted.isPriceSeries()) {
            panel.cmb_priceRenderer.setVisible(true);
            panel.cmb_seriesRenderer.setVisible(false);
        } else {
            panel.cmb_priceRenderer.setVisible(false);
            panel.cmb_seriesRenderer.setVisible(true);
        }

        if (panel.cmb_priceRenderer.isVisible()) {
            final PriceRendererType rendererType = plotConfigurationHelper.getPriceInitialSettings()
                    .getCurrentPriceRendererType();
            for (final PriceRendererType type : PriceRendererType.values()) {
                panel.cmb_priceRenderer.addItem(type);
            }
            panel.cmb_priceRenderer.setSelectedItem(rendererType);
            updateStyleVisibility(rendererType);
        }
        if (panel.cmb_seriesRenderer.isVisible()) {
            final IRendererType rendererType = plotConfigurationHelper.getSeriesInitialSettings(highlighted)
                    .getCurrentRendererType(highlighted);
            for (final SeriesRendererType type : SeriesRendererType.values()) {
                if (type == SeriesRendererType.Custom) {
                    final SeriesInitialSettings initialSeriesSettings = plotConfigurationHelper
                            .getOrCreateSeriesInitialSettings(highlighted);
                    if (initialSeriesSettings.isCustomSeriesType()) {
                        final ICustomRendererType customRendererType = (ICustomRendererType) initialSeriesSettings
                                .getRendererType();
                        panel.cmb_seriesRenderer.addItem(new SeriesRendererItem(type, customRendererType.getName()));
                    }
                } else {
                    panel.cmb_seriesRenderer.addItem(new SeriesRendererItem(type, type.name()));
                }
            }
            for (int i = 0; i < panel.cmb_seriesRenderer.getItemCount(); i++) {
                final SeriesRendererItem item = panel.cmb_seriesRenderer.getItemAt(i);
                if (item.getType() == rendererType.getSeriesRendererType()) {
                    panel.cmb_seriesRenderer.setSelectedIndex(i);
                    break;
                }
            }
            panel.cmb_seriesRenderer.setVisible(rendererType.isSeriesRendererTypeConfigurable());
            updateStyleVisibility(rendererType);
        }
    }

    private void updateStyleVisibility(final IRendererType rendererType) {
        panel.cmb_lineStyle.setVisible(rendererType.isLineStyleConfigurable());
        panel.cmb_lineWidth.setVisible(rendererType.isLineWidthConfigurable());
        panel.btn_seriesColor.setVisible(rendererType.isSeriesColorConfigurable());
        panel.lbl_seriesColor.setText(rendererType.getSeriesColorName() + " Color");
        panel.btn_seriesColor.setName(rendererType.getSeriesColorName());
        panel.btn_upColor.setVisible(rendererType.isUpColorConfigurable());
        panel.lbl_upColor.setText(rendererType.getUpColorName() + " Color");
        panel.btn_upColor.setName(rendererType.getUpColorName());
        panel.btn_downColor.setVisible(rendererType.isDownColorConfigurable());
        panel.lbl_downColor.setText(rendererType.getDownColorName() + " Color");
        panel.btn_downColor.setName(rendererType.getDownColorName());
        panel.chk_priceLine.setVisible(rendererType.isPriceLineConfigurable());
        panel.chk_priceLine.setSelected(highlighted.isPriceLineVisible());
    }

    private void updateLineVisibility() {
        if (panel.cmb_lineStyle.isVisible()) {
            for (final LineStyleType type : LineStyleType.values()) {
                panel.cmb_lineStyle.addItem(type);
            }
            final XYItemRenderer renderer = highlighted.getRenderer();
            final LineStyleType lineStyleType = LineStyleType.valueOf(renderer.getSeriesStroke(0));
            panel.cmb_lineStyle.setSelectedItem(lineStyleType);
        }
        if (panel.cmb_lineWidth.isVisible()) {
            for (final LineWidthType type : LineWidthType.values()) {
                panel.cmb_lineWidth.addItem(type);
            }
            final XYItemRenderer renderer = highlighted.getRenderer();
            final LineWidthType lineWidthType = LineWidthType.valueOf(renderer.getSeriesStroke(0));
            panel.cmb_lineWidth.setSelectedItem(lineWidthType);
        }
    }

    private void updateLabelVisibility() {
        panel.updateLayout();
        if (dialog != null) {
            dialog.pack();
        }
    }

    @Override
    public void reset() {
        if (highlighted.isPriceSeries()) {
            plotConfigurationHelper.getPriceInitialSettings().reset();
        } else {
            plotConfigurationHelper.getSeriesInitialSettings(highlighted).reset(highlighted);
        }
        initPanel();
    }

    @Override
    public void ok() {
        //noop
    }

    @Override
    public void cancel() {
        if (highlighted.isPriceSeries()) {
            priceSettingsBefore.reset();
        } else {
            seriesSettingsBefore.reset(highlighted);
        }
    }

}
