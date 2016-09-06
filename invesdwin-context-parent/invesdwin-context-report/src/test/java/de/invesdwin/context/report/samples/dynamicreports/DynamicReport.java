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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * @author Ricardo Mariaca (dynamicreports@gmail.com)
 */
@NotThreadSafe
public class DynamicReport {
    private String title;
    private List<DynamicColumn> columns;
    private List<String> groups;
    private List<String> subtotals;
    private boolean showPageNumber;

    public DynamicReport() {
        columns = new ArrayList<DynamicColumn>();
        groups = new ArrayList<String>();
        subtotals = new ArrayList<String>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public List<DynamicColumn> getColumns() {
        return columns;
    }

    public void setColumns(final List<DynamicColumn> columns) {
        this.columns = columns;
    }

    public void addColumn(final DynamicColumn column) {
        this.columns.add(column);
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(final List<String> groups) {
        this.groups = groups;
    }

    public void addGroup(final String column) {
        this.groups.add(column);
    }

    public List<String> getSubtotals() {
        return subtotals;
    }

    public void setSubtotals(final List<String> subtotals) {
        this.subtotals = subtotals;
    }

    public void addSubtotal(final String column) {
        this.subtotals.add(column);
    }

    public boolean isShowPageNumber() {
        return showPageNumber;
    }

    public void setShowPageNumber(final boolean showPageNumber) {
        this.showPageNumber = showPageNumber;
    }

}
