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

import javax.annotation.concurrent.NotThreadSafe;

import net.sf.dynamicreports.report.constant.HorizontalAlignment;

/**
 * @author Ricardo Mariaca (dynamicreports@gmail.com)
 */
@NotThreadSafe
public class DynamicColumn {
    private String name;
    private String title;
    private String type;
    private String pattern;
    private HorizontalAlignment horizontalAlignment;

    public DynamicColumn(final String title, final String name, final String type) {
        this.name = name;
        this.type = type;
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(final String pattern) {
        this.pattern = pattern;
    }

    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public void setHorizontalAlignment(final HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

}
