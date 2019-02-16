package de.invesdwin.context.jfreechart.panel;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JFrame;

import org.jfree.data.xy.OHLCDataItem;

import de.invesdwin.context.jfreechart.dataset.IndexedDateTimeOHLCDataset;
import de.invesdwin.context.log.error.Err;

@NotThreadSafe
public class CandlestickDemo extends JFrame {

    public CandlestickDemo() {
        super("CandlestickDemo");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final InteractiveChartPanel chartPanel = new InteractiveChartPanel(getDataSet());
        chartPanel.setPreferredSize(new Dimension(1280, 800));
        add(chartPanel);
        this.pack();
    }

    protected IndexedDateTimeOHLCDataset getDataSet() {

        //This is where we go get the data, replace with your own data source
        final List<OHLCDataItem> data = getData();

        //Create a dataset, an Open, High, Low, Close dataset
        final IndexedDateTimeOHLCDataset result = new IndexedDateTimeOHLCDataset("MSFT", data);

        return result;
    }

    //This method uses yahoo finance to get the OHLC data
    protected List<OHLCDataItem> getData() {
        final List<OHLCDataItem> dataItems = new ArrayList<OHLCDataItem>();
        try {
            /*
             * String strUrl=
             * "https://query1.finance.yahoo.com/v7/finance/download/MSFT?period1=1493801037&period2=1496479437&interval=1d&events=history&crumb=y/oR8szwo.9";
             */
            final File f = new File("src/test/java/" + CandlestickDemo.class.getPackage().getName().replace(".", "/")
                    + "/MSFTlong.csv");
            final BufferedReader in = new BufferedReader(new FileReader(f));

            final DateFormat df = new java.text.SimpleDateFormat("y-M-d");

            String inputLine;
            in.readLine();
            while ((inputLine = in.readLine()) != null) {
                final StringTokenizer st = new StringTokenizer(inputLine, ",");

                final Date date = df.parse(st.nextToken());
                final double open = Double.parseDouble(st.nextToken());
                final double high = Double.parseDouble(st.nextToken());
                final double low = Double.parseDouble(st.nextToken());
                final double close = Double.parseDouble(st.nextToken());
                final double adjClose = Double.parseDouble(st.nextToken());
                final double volume = Double.parseDouble(st.nextToken());

                final OHLCDataItem item = new OHLCDataItem(date, open, high, low, adjClose, volume);
                dataItems.add(item);
            }
            in.close();
        } catch (final Exception e) {
            throw Err.process(e);
        }
        return dataItems;
    }

    //CHECKSTYLE:OFF
    public static void main(final String[] args) {
        //CHECKSTYLE:ON
        new CandlestickDemo().setVisible(true);
    }
}