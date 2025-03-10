package de.invesdwin.context.jasperreports;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;

import javax.annotation.concurrent.Immutable;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.commons.io.input.ClosedInputStream;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import de.invesdwin.context.jfreechart.FiniteTickUnitSource;
import de.invesdwin.context.jfreechart.axis.AxisType;
import de.invesdwin.context.jfreechart.visitor.AJFreeChartVisitor;
import de.invesdwin.context.jfreechart.visitor.JFreeChartFontSizeMultiplier;
import de.invesdwin.context.log.error.Err;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.Closeables;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.streams.ALazyDelegateInputStream;
import de.invesdwin.util.streams.pool.PooledFastByteArrayOutputStream;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@Immutable
public enum JFreeChartExporter {

    JPG(".jpg") {
        @Override
        public void writeChart(final OutputStream out, final JFreeChart chart,
                final JFreeChartExporterSettings settings) throws IOException {
            try {
                customizeChart(chart, settings);
                ChartUtils.writeChartAsJPEG(out, chart, settings.getBounds().width, settings.getBounds().height);
            } catch (final Throwable t) {
                throw new RuntimeException("At: " + chart.getTitle().getID(), t);
            }
        }
    },
    PNG(".png") {
        @Override
        public void writeChart(final OutputStream out, final JFreeChart chart,
                final JFreeChartExporterSettings settings) throws IOException {
            try {
                customizeChart(chart, settings);
                ChartUtils.writeChartAsPNG(out, chart, settings.getBounds().width, settings.getBounds().height);
            } catch (final Throwable t) {
                throw new RuntimeException("At: " + chart.getTitle().getID(), t);
            }
        }
    },
    SVG(".svg") {
        @Override
        public void writeChart(final OutputStream out, final JFreeChart chart,
                final JFreeChartExporterSettings settings) throws IOException {
            try {
                customizeChart(chart, settings);
                // Get a DOMImplementation and create an XML document
                final DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
                final Document document = domImpl.createDocument(null, "svg", null);

                // Create an instance of the SVG Generator
                final SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
                svgGenerator.setSVGCanvasSize(settings.getBounds());

                // draw the chart in the SVG generator
                chart.draw(svgGenerator, new Rectangle(settings.getBounds()));

                // Write svg file
                final OutputStreamWriter writer = new OutputStreamWriter(out);
                svgGenerator.stream(writer, true);
                writer.flush();
                writer.close();
            } catch (final Throwable t) {
                throw new RuntimeException("At: " + chart.getTitle().getID(), t);
            }
        }
    };

    public static final Duration DEFAULT_RENDER_TIMEOUT = new Duration(30, FTimeUnit.MINUTES);

    public static final Dimension DIMENSION_DIN_A4_300_DPI = new Dimension(2475, 3525);
    public static final Dimension DIMENSION_DIN_A4_150_DPI = new Dimension(DIMENSION_DIN_A4_300_DPI.width / 2,
            DIMENSION_DIN_A4_300_DPI.height / 2);
    public static final Dimension DIMENSION_DIN_A4_100_DPI = new Dimension(DIMENSION_DIN_A4_300_DPI.width / 3,
            DIMENSION_DIN_A4_300_DPI.height / 3);
    public static final Dimension DIMENSION_DIN_A4_75_DPI = new Dimension(DIMENSION_DIN_A4_150_DPI.width / 2,
            DIMENSION_DIN_A4_150_DPI.height / 2);
    public static final Dimension DIMENSION_DIN_A4_25_DPI = new Dimension(DIMENSION_DIN_A4_75_DPI.width / 3,
            DIMENSION_DIN_A4_75_DPI.height / 3);
    public static final Dimension DIMENSION_DIN_A4_125_DPI = new Dimension(DIMENSION_DIN_A4_25_DPI.width * 5,
            DIMENSION_DIN_A4_25_DPI.height * 5);
    public static final Dimension DIMENSION_DIN_A4_200_DPI = new Dimension(DIMENSION_DIN_A4_100_DPI.width * 2,
            DIMENSION_DIN_A4_100_DPI.height * 2);

    private final String fileExtension;

    JFreeChartExporter(final String extension) {
        this.fileExtension = extension;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public static void customizeChart(final JFreeChart chart, final JFreeChartExporterSettings settings) {
        new AJFreeChartVisitor() {
            @Override
            protected void processChart(final JFreeChart chart) {
                if (Strings.isBlank(chart.getTitle().getID())) {
                    throw new IllegalArgumentException(
                            "Please provide an ID via chart.getTitle().setID(...) with which you can identify the chart when an exception occurs. This is very helpful during parallel rendering with jasper reports.");
                }
                super.processChart(chart);
            }

            @Override
            public Font processFont(final Font font) {
                if (settings == null || settings.getFontMultiplier() == null) {
                    return font;
                } else {
                    return JFreeChartFontSizeMultiplier.multiplyFont(font, settings.getFontMultiplier());
                }
            }

            @Override
            public void processAxis(final Axis axis, final AxisType axisType) {
                FiniteTickUnitSource.maybeWrap(axis);
                super.processAxis(axis, axisType);
            }
        }.process(chart);
        final AJFreeChartVisitor theme = settings.getTheme();
        if (theme != null) {
            theme.process(chart);
        }
    }

    public abstract void writeChart(OutputStream out, JFreeChart chart, JFreeChartExporterSettings settings)
            throws IOException;

    public void exportToFile(final File file, final JFreeChart chart, final JFreeChartExporterSettings settings) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            writeChart(out, chart, settings);
        } catch (final IOException e) {
            throw Err.process(e);
        } finally {
            Closeables.closeQuietly(out);
        }
    }

    public void exportToFileCallable(final File file, final Callable<JFreeChart> chart,
            final JFreeChartExporterSettings settings) {
        try {
            exportToFile(file, chart.call(), settings);
        } catch (final Exception e) {
            throw Err.process(e);
        }
    }

    public File exportToDir(final File dir, final JFreeChart chart, final JFreeChartExporterSettings settings) {
        Assertions.assertThat(chart.getTitle().getID()).isNotNull();
        final File file = new File(dir, chart.getTitle().getID() + getFileExtension());
        exportToFile(file, chart, settings);
        return file;
    }

    public List<File> exportToDir(final File dir, final Collection<JFreeChart> charts,
            final JFreeChartExporterSettings settings) {
        final List<File> files = new ArrayList<File>();
        for (final JFreeChart e : charts) {
            final JFreeChart chart = e;
            files.add(exportToDir(dir, chart, settings));
        }
        return files;
    }

    /**
     * Callables can be used to decrease the memory footprint while exporting multiple large charts, thus building them
     * on demand.
     */
    public File exportToDirCallable(final File dir, final Callable<JFreeChart> chart,
            final JFreeChartExporterSettings settings) {
        try {
            return exportToDir(dir, chart.call(), settings);
        } catch (final Exception e) {
            throw Err.process(e);
        }
    }

    public List<File> exportToDirCallable(final File dir, final Collection<Callable<JFreeChart>> charts,
            final JFreeChartExporterSettings settings) {
        final List<File> files = new ArrayList<File>();
        for (final Callable<JFreeChart> chart : charts) {
            files.add(exportToDirCallable(dir, chart, settings));
        }
        return files;
    }

    public InputStream exportToStream(final JFreeChart chart, final JFreeChartExporterSettings settings) {
        final PooledFastByteArrayOutputStream out = PooledFastByteArrayOutputStream.newInstance();
        try {
            writeChart(out.asNonClosing(), chart, settings);
            return out.asInputStream();
        } catch (final IOException e) {
            out.close();
            throw new RuntimeException(e);
        }
    }

    public InputStream exportToStreamCallable(final Callable<JFreeChart> chart,
            final JFreeChartExporterSettings settings) {
        return new ALazyDelegateInputStream() {

            @Override
            protected InputStream newDelegate() {
                try {
                    return exportToStream(chart.call(), settings);
                } catch (final Exception e) {
                    throw Err.process(e);
                }
            }

        };
    }

    public InputStream exportToStreamCallable(final Callable<JFreeChart> chart,
            final JFreeChartExporterSettings settings, final WrappedExecutorService parallelRenderer) {
        return exportToStreamCallable(chart, settings, parallelRenderer, DEFAULT_RENDER_TIMEOUT);
    }

    public InputStream exportToStreamCallable(final Callable<JFreeChart> chart,
            final JFreeChartExporterSettings settings, final WrappedExecutorService parallelRenderer,
            final Duration renderTimeout) {
        return new ALazyDelegateInputStream() {

            private final Lock lock = Locks
                    .newReentrantLock(JFreeChartExporter.class.getSimpleName() + "_exportToStreamCallable_lock");
            private final Future<?> future;
            private String chartTitle;
            private Exception initStackTrace;
            private String chartId;

            {
                if (Throwables.isDebugStackTraceEnabled()) {
                    initStackTrace = new Exception();
                    initStackTrace.fillInStackTrace();
                }
                future = parallelRenderer.submit(new Runnable() {
                    @Override
                    public void run() {
                        Assertions.assertThat(getDelegate()).isNotNull();
                    };
                });
            }

            @Override
            public InputStream getDelegate() {
                try {
                    final boolean locked = lock.tryLock(renderTimeout.longValue(FTimeUnit.MILLISECONDS),
                            FTimeUnit.MILLISECONDS.timeUnitValue());
                    if (!locked) {
                        future.cancel(true);
                        Err.process(wrapException(new RuntimeException("Rendering timeout of [" + renderTimeout
                                + "] exceeded, aborting and returning an empty input stream. This might be due to an endless loop in the chart rendering...")));
                        return ClosedInputStream.INSTANCE;
                    }
                    try {
                        return super.getDelegate();
                    } finally {
                        lock.unlock();
                    }
                } catch (final Throwable e) {
                    throw wrapException(e);
                }
            }

            public RuntimeException wrapException(final Throwable e) {
                final StringBuilder message = new StringBuilder();
                message.append("Chart with title [");
                message.append(chartTitle);
                message.append("] and id [");
                message.append(chartId);
                message.append("] encountered an exception");
                if (initStackTrace != null) {
                    message.append(" from initial stacktrace:\n");
                    message.append(Throwables.getFullStackTrace(initStackTrace));
                }
                return new RuntimeException(message.toString(), e);
            }

            @Override
            protected InputStream newDelegate() {
                try {
                    final JFreeChart call = chart.call();
                    chartTitle = call.getTitle().getText();
                    chartId = call.getTitle().getID();
                    return exportToStream(call, settings);
                } catch (final Throwable e) {
                    throw Err.process(wrapException(e));
                }
            }

        };
    }
}
