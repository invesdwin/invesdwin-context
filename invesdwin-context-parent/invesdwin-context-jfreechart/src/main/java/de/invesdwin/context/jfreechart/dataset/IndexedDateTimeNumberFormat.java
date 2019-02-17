package de.invesdwin.context.jfreechart.dataset;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Locale;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.Range;

import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.FDates;
import de.invesdwin.util.time.fdate.FTimeUnit;

@NotThreadSafe
public class IndexedDateTimeNumberFormat extends NumberFormat {
    private static final String DATE_TIME_SEPARATOR = " ";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final int THRESHOLD_MULTIPLIER = 10;
    private static final Duration MILLISECOND_THRESHOLD = Duration.ONE_MINUTE.multiply(THRESHOLD_MULTIPLIER);
    private static final Duration SECOND_THRESHOLD = Duration.ONE_HOUR.multiply(THRESHOLD_MULTIPLIER);
    private static final Duration MINUTE_THRESHOLD = Duration.ONE_DAY.multiply(THRESHOLD_MULTIPLIER);
    private final DateFormat dateFormat = new java.text.SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
    private final DateFormat minuteFormat = new java.text.SimpleDateFormat(DATE_FORMAT + DATE_TIME_SEPARATOR + "HH:mm",
            Locale.ENGLISH);
    private final DateFormat secondFormat = new java.text.SimpleDateFormat(
            DATE_FORMAT + DATE_TIME_SEPARATOR + FDate.FORMAT_ISO_TIME, Locale.ENGLISH);
    private final DateFormat millisecondFormat = new java.text.SimpleDateFormat(
            DATE_FORMAT + DATE_TIME_SEPARATOR + FDate.FORMAT_ISO_TIME_MS, Locale.ENGLISH);
    private final IIndexedDateTimeXYDataset dataset;
    private final NumberAxis domainAxis;

    public IndexedDateTimeNumberFormat(final IIndexedDateTimeXYDataset dataset, final NumberAxis domainAxis) {
        this.dataset = dataset;
        this.domainAxis = domainAxis;
    }

    @Override
    public StringBuffer format(final double number, final StringBuffer toAppendTo, final FieldPosition pos) {
        final int item = (int) number;
        final long time = (long) dataset.getXValueAsDateTime(0, item);
        final long prevTime = (long) dataset.getXValueAsDateTime(0, item - 1);
        final FDate date = new FDate(time);
        final FDate prevDate = new FDate(prevTime);
        final Range range = domainAxis.getRange();
        final double millis = dataset.getXValueAsDateTime(0, (int) range.getUpperBound())
                - dataset.getXValueAsDateTime(0, (int) range.getLowerBound());
        final Duration duration = new Duration((long) millis, FTimeUnit.MILLISECONDS);
        final DateFormat format;
        if (duration.isLessThan(MILLISECOND_THRESHOLD) && FDates.isSameSecond(date, prevDate)) {
            format = millisecondFormat;
        } else if (duration.isLessThan(SECOND_THRESHOLD) && FDates.isSameMinute(date, prevDate)) {
            format = secondFormat;
        } else if (duration.isLessThan(MINUTE_THRESHOLD) && FDates.isSameDay(date, prevDate)) {
            format = minuteFormat;
        } else {
            format = dateFormat;
        }
        //CHECKSTYLE:OFF
        return toAppendTo.append(format.format(new Date(time)));
        //CHECKSTYLE:ON
    }

    @Override
    public StringBuffer format(final long number, final StringBuffer toAppendTo, final FieldPosition pos) {
        final int item = (int) number;
        final long time = (long) dataset.getXValueAsDateTime(0, item);
        final long prevTime = (long) dataset.getXValueAsDateTime(0, item - 1);
        final FDate date = new FDate(time);
        final FDate prevDate = new FDate(prevTime);
        final DateFormat format;
        if (FDates.isSameSecond(date, prevDate)) {
            format = millisecondFormat;
        } else if (FDates.isSameMinute(date, prevDate)) {
            format = secondFormat;
        } else if (FDates.isSameDay(date, prevDate)) {
            format = minuteFormat;
        } else {
            format = dateFormat;
        }
        //CHECKSTYLE:OFF
        return toAppendTo.append(format.format(new Date(time)));
    }

    @Override
    public Number parse(final String source, final ParsePosition parsePosition) {
        throw new UnsupportedOperationException();
    }
}