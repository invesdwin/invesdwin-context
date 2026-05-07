package de.invesdwin.context.jasperreports;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

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
