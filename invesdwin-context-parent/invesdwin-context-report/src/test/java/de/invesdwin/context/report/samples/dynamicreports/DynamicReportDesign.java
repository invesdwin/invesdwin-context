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
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.grp;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.sbt;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.PageXofYBuilder;
import net.sf.dynamicreports.report.builder.component.TextFieldBuilder;
import net.sf.dynamicreports.report.builder.group.ColumnGroupBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.definition.datatype.DRIDataType;
import net.sf.dynamicreports.report.exception.DRException;

/**
 * @author Ricardo Mariaca (dynamicreports@gmail.com)
 */
@NotThreadSafe
public class DynamicReportDesign {
    private final DynamicReportData data = new DynamicReportData();

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public JasperReportBuilder build() throws DRException {
        final JasperReportBuilder report = report();

        report.setTemplate(DynamicReportTemplates.reportTemplate).title(DynamicReportTemplates.createTitleComponent("DynamicReport"));

        final DynamicReport dynamicReport = data.getDynamicReport();
        final List<DynamicColumn> columns = dynamicReport.getColumns();
        final Map<String, TextColumnBuilder> drColumns = new HashMap<String, TextColumnBuilder>();

        for (final DynamicColumn column : columns) {
            final TextColumnBuilder drColumn = col.column(column.getTitle(), column.getName(),
                    (DRIDataType) type.detectType(column.getType()));
            if (column.getPattern() != null) {
                drColumn.setPattern(column.getPattern());
            }
            if (column.getHorizontalAlignment() != null) {
                drColumn.setHorizontalAlignment(column.getHorizontalAlignment());
            }
            drColumns.put(column.getName(), drColumn);
            report.columns(drColumn);
        }

        for (final String group : dynamicReport.getGroups()) {
            final ColumnGroupBuilder group2 = grp.group(drColumns.get(group));
            report.groupBy(group2);

            for (final String subtotal : dynamicReport.getSubtotals()) {
                report.subtotalsAtGroupFooter(group2, sbt.sum(drColumns.get(subtotal)));
            }
        }

        for (final String subtotal : dynamicReport.getSubtotals()) {
            report.subtotalsAtSummary(sbt.sum(drColumns.get(subtotal)));
        }

        if (dynamicReport.getTitle() != null) {
            final TextFieldBuilder<String> title = cmp.text(dynamicReport.getTitle())
                    .setStyle(DynamicReportTemplates.bold12CenteredStyle)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER);
            report.addTitle(title);
        }
        if (dynamicReport.isShowPageNumber()) {
            final PageXofYBuilder pageXofY = cmp.pageXofY().setStyle(DynamicReportTemplates.boldCenteredStyle);
            report.addPageFooter(pageXofY);
        }
        report.setDataSource(data.createDataSource());

        return report;
    }

}
