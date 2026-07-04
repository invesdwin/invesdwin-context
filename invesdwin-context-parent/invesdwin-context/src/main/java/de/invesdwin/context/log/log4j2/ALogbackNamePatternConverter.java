package de.invesdwin.context.log.log4j2;

import javax.annotation.concurrent.Immutable;

import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

/**
 * Derived from: ch.qos.logback.classic.pattern.TargetLengthBasedClassNameAbbreviator
 */
@Immutable
public abstract class ALogbackNamePatternConverter extends LogEventPatternConverter {

    private static final char DOT = '.';
    private final int targetLength;

    /**
     * Constructor.
     *
     * @param name
     *            name of converter.
     * @param style
     *            style name for associated output.
     * @param options
     *            options, may be null, first element will be interpreted as an abbreviation pattern.
     */
    protected ALogbackNamePatternConverter(final String name, final String style, final int targetLength) {
        super(name, style);
        this.targetLength = targetLength;
    }

    /**
     * Abbreviate name in string buffer.
     *
     * @param original
     *            string containing name.
     * @param destination
     *            the StringBuilder to write to
     */
    protected final void abbreviate(final String original, final StringBuilder destination) {
        final String abbreviated = abbreviate(original);
        destination.append(abbreviated);
    }

    private String abbreviate(final String fqClassName) {
        if (fqClassName == null) {
            throw new IllegalArgumentException("Class name may not be null");
        }

        final int inLen = fqClassName.length();
        if (inLen < targetLength) {
            return fqClassName;
        }

        final StringBuilder buf = new StringBuilder(inLen);

        final int rightMostDotIndex = fqClassName.lastIndexOf(DOT);

        if (rightMostDotIndex == -1) {
            return fqClassName;
        }

        // length of last segment including the dot
        final int lastSegmentLength = inLen - rightMostDotIndex;

        int leftSegments_TargetLen = targetLength - lastSegmentLength;
        if (leftSegments_TargetLen < 0) {
            leftSegments_TargetLen = 0;
        }

        final int leftSegmentsLen = inLen - lastSegmentLength;

        // maxPossibleTrim denotes the maximum number of characters we aim to trim
        // the actual number of character trimmed may be higher since segments, when
        // reduced, are reduced to just one character
        final int maxPossibleTrim = leftSegmentsLen - leftSegments_TargetLen;

        int trimmed = 0;
        boolean inDotState = true;

        int i = 0;
        //CHECKSTYLE:OFF
        for (; i < rightMostDotIndex; i++) {
            //CHECKSTYLE:ON
            final char c = fqClassName.charAt(i);
            if (c == DOT) {
                // if trimmed too many characters, let us stop
                if (trimmed >= maxPossibleTrim) {
                    break;
                }
                buf.append(c);
                inDotState = true;
            } else {
                if (inDotState) {
                    buf.append(c);
                    inDotState = false;
                } else {
                    trimmed++;
                }
            }
        }
        // append from the position of i which may include the last seen DOT
        buf.append(fqClassName.substring(i));
        return buf.toString();
    }
}
