package de.invesdwin.context.log.progress;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.log.Log;
import de.invesdwin.util.concurrent.loop.LoopInterruptedCheck;
import de.invesdwin.util.lang.string.ProcessedEventsRateString;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.AEstimatedRemainingDuration;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public class TimeRangedProgressLogger {

    private final LoopInterruptedCheck loopCheck = new LoopInterruptedCheck(Duration.ONE_SECOND);

    private final Log log;
    private final String items;
    private final String forName;
    private final FDate from;
    private final FDate to;
    private final Instant start;
    private final AEstimatedRemainingDuration estimatedRemainingDuration = new AEstimatedRemainingDuration() {
        @Override
        protected Percent getProgressPercent() {
            return progress;
        }

        @Override
        protected Duration getElapsedDuration() {
            return duration;
        }
    };
    private Percent progress;
    private Duration duration;

    public TimeRangedProgressLogger(final Log log, final String items, final String forName, final FDate from,
            final FDate to) {
        this.log = log;
        this.items = items;
        this.forName = forName;
        this.from = from;
        this.to = to;
        this.start = new Instant();
    }

    public void maybePrintProgress(final String action, final FDate curTime, final int count) {
        if (loopCheck.checkNoInterrupt()) {
            printProgress(action, curTime, count);
        }
    }

    public void printProgress(final String action, final FDate curTime, final int count) {
        if (!log.isInfoEnabled()) {
            return;
        }

        progress = new Percent(new Duration(from, curTime), new Duration(from, to))
                .orLower(Percent.ONE_HUNDRED_PERCENT);
        duration = start.toDuration();

        final StringBuilder sb = new StringBuilder();
        sb.append(action);
        sb.append(" ");
        sb.append(items);
        sb.append(" for [");
        sb.append(forName);
        sb.append("] at ");
        sb.append(progress.toString(PercentScale.PERCENT));
        sb.append(" reaching [");
        sb.append(from);
        sb.append("]->[");
        sb.append(curTime);
        sb.append("]->[");
        sb.append(to);
        sb.append("].");
        if (count > 0) {
            sb.append(" Processed ");
            sb.append(count);
            sb.append(" ");
            sb.append(items);
            sb.append(" at ");
            sb.append(new ProcessedEventsRateString(count, duration));
            sb.append(" during ");
            sb.append(duration);
            sb.append(". Estimated remaining duration: ");
            sb.append(estimatedRemainingDuration.getEstimatedRemainingDuration().toString(FTimeUnit.SECONDS));
        }

        log.info("%s", sb);
    }

}
