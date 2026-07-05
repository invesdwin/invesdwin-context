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

    protected ALogbackNamePatternConverter(final String name, final String style, final int targetLength) {
        super(name, style);
        this.targetLength = targetLength;
    }

    protected final void abbreviate(final String fqClassName, final StringBuilder destination) {
        if (fqClassName == null) {
            throw new IllegalArgumentException("Class name may not be null");
        }

        final int inLen = fqClassName.length();
        if (inLen < targetLength) {
            destination.append(fqClassName);
            return;
        }

        final int rightMostDotIndex = fqClassName.lastIndexOf(DOT);

        if (rightMostDotIndex == -1) {
            destination.append(fqClassName);
            return;
        }

        // length of last segment including the dot
        final int lastSegmentLength = inLen - rightMostDotIndex;

        int leftSegments_TargetLen = targetLength - lastSegmentLength;
        if (leftSegments_TargetLen < 0) {
            leftSegments_TargetLen = 0;
        }

        final int leftSegmentsLen = inLen - lastSegmentLength;

        // maxPossibleTrim denotes the maximum number of characters we aim to trim
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
                destination.append(c);
                inDotState = true;
            } else {
                if (inDotState) {
                    destination.append(c);
                    inDotState = false;
                } else {
                    trimmed++;
                }
            }
        }
        // Append the remaining segment directly using the bounds, avoiding .substring() allocations
        destination.append(fqClassName, i, inLen);
    }
}