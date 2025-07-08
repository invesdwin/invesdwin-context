package de.invesdwin.context.log.logback;

import javax.annotation.concurrent.NotThreadSafe;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.util.FileSize;
import de.invesdwin.context.ContextProperties;

@NotThreadSafe
public class ConfiguredFileAppender extends RollingFileAppender<ILoggingEvent> {

    //  <property name="file.prefix" value="log/" />
    //  <property name="file.postfix" value=".log" />
    //  <property name="file.postfix.archive" value="${file.postfix}.%i.gz" />
    //  <property name="layout.pattern" value="%-40(%d{ISO8601} [%.-14thread]) %-5level %-60.-60(%logger{45}.%method) - %msg%n" />
    //  <property name="file.maxhistory" value="3" />
    //  <property name="file.maxsize" value="5MB" />

    @Override
    public void start() {
        //  <appender name="file_invesdwin" class="ch.qos.logback.core.rolling.RollingFileAppender">
        //      <File>${file.prefix}invesdwin${file.postfix}</File>
        final String fileBase = ContextProperties.getLogDirectory() + "/" + getName();
        final String file = fileBase + ".log";
        final String fileNamePattern = fileBase + "_%i.log";

        setFile(file);

        //      <rollingPolicy class="ch.qos.logback.core.rolling.FixedWindowRollingPolicy">
        //          <fileNamePattern>${file.prefix}invesdwin${file.postfix.archive}</fileNamePattern>
        //          <minIndex>1</minIndex>
        //          <maxIndex>${file.maxhistory}</maxIndex>
        //      </rollingPolicy>
        final FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
        rollingPolicy.setFileNamePattern(fileNamePattern);
        rollingPolicy.setMinIndex(1);
        rollingPolicy.setMaxIndex(9);
        rollingPolicy.setParent(this);
        rollingPolicy.setContext(getContext());
        rollingPolicy.start();
        setRollingPolicy(rollingPolicy);

        //      <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
        //          <maxFileSize>${file.maxsize}</maxFileSize>
        //      </triggeringPolicy>
        final SizeBasedTriggeringPolicy<ILoggingEvent> triggeringPolicy = new SizeBasedTriggeringPolicy<ILoggingEvent>();
        triggeringPolicy.setMaxFileSize(FileSize.valueOf("20MB"));
        triggeringPolicy.setContext(getContext());
        triggeringPolicy.start();
        setTriggeringPolicy(triggeringPolicy);

        //      <encoder>
        //          <Pattern>${layout.pattern}</Pattern>
        //      </encoder>
        final PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern(LogbackProperties.ENCODER_PATTERN);
        encoder.setContext(getContext());
        encoder.start();
        setEncoder(encoder);

        //  </appender>
        super.start();
    }
}
