/**
 * DynamicReports - Free Java reporting library for creating reports dynamically
 * 
 * Copyright (C) 2010 - 2012 Ricardo Mariaca http://dynamicreports.sourceforge.net
 * 
 * This file is part of DynamicReports.
 * 
 * DynamicReports is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * DynamicReports is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with DynamicReports. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package de.invesdwin.context.report.samples.dynamicreports;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.hyperLink;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.tableOfContentsCustomizer;
import static net.sf.dynamicreports.report.builder.DynamicReports.template;

import java.awt.Color;
import java.util.Locale;

import javax.annotation.concurrent.NotThreadSafe;

import net.sf.dynamicreports.report.base.expression.AbstractValueFormatter;
import net.sf.dynamicreports.report.builder.HyperLinkBuilder;
import net.sf.dynamicreports.report.builder.ReportTemplateBuilder;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.datatype.BigDecimalType;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.builder.tableofcontents.TableOfContentsCustomizerBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.VerticalAlignment;
import net.sf.dynamicreports.report.definition.ReportParameters;

/**
 * @author Ricardo Mariaca (dynamicreports@gmail.com)
 */
@NotThreadSafe
//CHECKSTYLE:OFF
public final class DynamicReportTemplates {
    public static final StyleBuilder rootStyle;
    public static final StyleBuilder boldStyle;
    public static final StyleBuilder italicStyle;
    public static final StyleBuilder boldCenteredStyle;
    public static final StyleBuilder bold12CenteredStyle;
    public static final StyleBuilder bold18CenteredStyle;
    public static final StyleBuilder bold22CenteredStyle;
    public static final StyleBuilder columnStyle;
    public static final StyleBuilder columnTitleStyle;
    public static final StyleBuilder groupStyle;
    public static final StyleBuilder subtotalStyle;

    public static final ReportTemplateBuilder reportTemplate;
    public static final CurrencyType currencyType;
    public static final ComponentBuilder<?, ?> dynamicReportsComponent;
    public static final ComponentBuilder<?, ?> footerComponent;

    static {
        rootStyle = stl.style().setPadding(2);
        boldStyle = stl.style(rootStyle).bold();
        italicStyle = stl.style(rootStyle).italic();
        boldCenteredStyle = stl.style(boldStyle).setAlignment(HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE);
        bold12CenteredStyle = stl.style(boldCenteredStyle).setFontSize(12);
        bold18CenteredStyle = stl.style(boldCenteredStyle).setFontSize(18);
        bold22CenteredStyle = stl.style(boldCenteredStyle).setFontSize(22);
        columnStyle = stl.style(rootStyle).setVerticalAlignment(VerticalAlignment.MIDDLE);
        columnTitleStyle = stl.style(columnStyle)
                .setBorder(stl.pen1Point())
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setBackgroundColor(Color.LIGHT_GRAY)
                .bold();
        groupStyle = stl.style(boldStyle).setHorizontalAlignment(HorizontalAlignment.LEFT);
        subtotalStyle = stl.style(boldStyle).setTopBorder(stl.pen1Point());

        final StyleBuilder crosstabGroupStyle = stl.style(columnTitleStyle);
        final StyleBuilder crosstabGroupTotalStyle = stl.style(columnTitleStyle).setBackgroundColor(
                new Color(170, 170, 170));
        final StyleBuilder crosstabGrandTotalStyle = stl.style(columnTitleStyle).setBackgroundColor(
                new Color(140, 140, 140));
        final StyleBuilder crosstabCellStyle = stl.style(columnStyle).setBorder(stl.pen1Point());

        final TableOfContentsCustomizerBuilder tableOfContentsCustomizer = tableOfContentsCustomizer().setHeadingStyle(
                0, stl.style(rootStyle).bold());

        reportTemplate = template().setLocale(Locale.ENGLISH)
                .setColumnStyle(columnStyle)
                .setColumnTitleStyle(columnTitleStyle)
                .setGroupStyle(groupStyle)
                .setGroupTitleStyle(groupStyle)
                .setSubtotalStyle(subtotalStyle)
                .highlightDetailEvenRows()
                .crosstabHighlightEvenRows()
                .setCrosstabGroupStyle(crosstabGroupStyle)
                .setCrosstabGroupTotalStyle(crosstabGroupTotalStyle)
                .setCrosstabGrandTotalStyle(crosstabGrandTotalStyle)
                .setCrosstabCellStyle(crosstabCellStyle)
                .setTableOfContentsCustomizer(tableOfContentsCustomizer);

        currencyType = new CurrencyType();

        final HyperLinkBuilder link = hyperLink("http://dynamicreports.sourceforge.net");
        dynamicReportsComponent = cmp.horizontalList(
                cmp.image(DynamicReportTemplates.class.getResource("images/dynamicreports.png")).setFixedDimension(60, 60),
                cmp.verticalList(
                        cmp.text("DynamicReports")
                                .setStyle(bold22CenteredStyle)
                                .setHorizontalAlignment(HorizontalAlignment.LEFT),
                        cmp.text("http://dynamicreports.sourceforge.net").setStyle(italicStyle).setHyperLink(link)));

        footerComponent = cmp.pageXofY().setStyle(stl.style(boldCenteredStyle).setTopBorder(stl.pen1Point()));
    }

    private DynamicReportTemplates() {}

    /**
     * Creates custom component which is possible to add to any report band component
     */
    public static ComponentBuilder<?, ?> createTitleComponent(final String label) {
        return cmp.horizontalList()
                .add(dynamicReportsComponent,
                        cmp.text(label).setStyle(bold18CenteredStyle).setHorizontalAlignment(HorizontalAlignment.RIGHT))
                .newRow()
                .add(cmp.line())
                .newRow()
                .add(cmp.verticalGap(10));
    }

    public static CurrencyValueFormatter createCurrencyValueFormatter(final String label) {
        return new CurrencyValueFormatter(label);
    }

    public static class CurrencyType extends BigDecimalType {
        private static final long serialVersionUID = 1L;

        @Override
        public String getPattern() {
            return "$ #,###.00";
        }
    }

    private static class CurrencyValueFormatter extends AbstractValueFormatter<String, Number> {
        private static final long serialVersionUID = 1L;

        private final String label;

        public CurrencyValueFormatter(final String label) {
            this.label = label;
        }

        public String format(final Number value, final ReportParameters reportParameters) {
            return label + currencyType.valueToString(value, reportParameters.getLocale());
        }
    }
}