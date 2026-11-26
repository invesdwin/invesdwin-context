package de.invesdwin.context.integration.filechannel.nio.atomic.properties;

import java.io.File;

import javax.annotation.concurrent.Immutable;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.system.properties.IProperties;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDateBuilder;
import de.invesdwin.util.time.date.millis.FDateNanos;

/**
 * A property key containing a slash is written into a subdirectory of the properties folder, because the key is turned
 * into a file name through {@link de.invesdwin.util.lang.Files#normalizePath(String)} and that leaves slashes as path
 * separators. The directory scan that decides which keys exist used to list only the folder itself and to name keys by
 * their leaf, so every nested key was write-only: the file sits on disk, a later process does not know the key, and
 * since each read is gated on that key set the value is answered absent.
 * <p>
 * Red first: the nested cells below fail against the flat scan, the flat cell pins that the ordinary case is unchanged.
 */
@Immutable
public class AtomicFilesPropertiesNestedKeyTest {

    private static final FDate VALUE = FDateBuilder.newDate(2026, 11, 17);
    private static final String NESTED_KEY = "XG/XGH04.firstValueFloor";
    private static final String DEEP_KEY = "Iqfeed/Commodities/XG/XGH04.lastBarEndTimeFinished";
    private static final String FLAT_KEY = "heartbeatOwner";

    @Test
    public void aNestedKeyWrittenByOneInstanceIsReadableFromTheNext() {
        final File folder = newTempFolder("nested-key");
        try {
            final IProperties writer = new AtomicFilesProperties(folder);
            writer.setDate(NESTED_KEY, VALUE);
            Assertions.assertThat(writer.getDateOptional(NESTED_KEY, null))
                    .as("the writing instance always saw its own key")
                    .isEqualTo(VALUE);

            final IProperties reader = new AtomicFilesProperties(folder);
            Assertions.assertThat(reader.containsKey(NESTED_KEY))
                    .as("a fresh instance over the same folder must know the key the file on disk names")
                    .isTrue();
            Assertions.assertThat(reader.getDateOptional(NESTED_KEY, null))
                    .as("and must answer the value it holds")
                    .isEqualTo(VALUE);
        } finally {
            deleteRecursively(folder);
        }
    }

    @Test
    public void nestedKeysAreEnumeratedAndMakeTheFolderNonEmpty() {
        final File folder = newTempFolder("nested-enumerate");
        try {
            final IProperties writer = new AtomicFilesProperties(folder);
            writer.setDate(NESTED_KEY, VALUE);
            writer.setString(DEEP_KEY, "deep");

            final IProperties reader = new AtomicFilesProperties(folder);
            Assertions.assertThat(reader.isEmpty())
                    .as("a folder whose only content is nested property files is not empty")
                    .isFalse();
            Assertions.assertThat(reader.getKeys())
                    .as("key enumeration yields the key as the caller spelled it, not the leaf name")
                    .containsExactlyInAnyOrder(NESTED_KEY, DEEP_KEY);
            Assertions.assertThat(reader.getString(DEEP_KEY))
                    .as("two levels deep is found too, not just the first subdirectory")
                    .isEqualTo("deep");
        } finally {
            deleteRecursively(folder);
        }
    }

    /**
     * The nesting limit is not the folder layout's - a caller may keep appending levels to a key forever, so the scan
     * has to follow them. 24 levels is well past any depth a store uses today, which is what makes this cell the
     * regression guard for the descent being unbounded.
     */
    @Test
    public void anArbitrarilyDeepKeyRoundTrips() {
        final StringBuilder key = new StringBuilder();
        for (int i = 0; i < 24; i++) {
            key.append("level").append(i).append('/');
        }
        key.append("leafKey");
        final String deepKey = key.toString();

        final File folder = newTempFolder("deep-key");
        try {
            new AtomicFilesProperties(folder).setString(deepKey, "value");

            final IProperties reader = new AtomicFilesProperties(folder);
            Assertions.assertThat(reader.getString(deepKey))
                    .as("a key nested 24 levels deep is found by a fresh instance")
                    .isEqualTo("value");
            Assertions.assertThat(reader.getKeys()).containsExactly(deepKey);
        } finally {
            deleteRecursively(folder);
        }
    }

    @Test
    public void aFlatKeyStillRoundTrips() {
        final File folder = newTempFolder("flat-key");
        try {
            new AtomicFilesProperties(folder).setDate(FLAT_KEY, VALUE);

            final IProperties reader = new AtomicFilesProperties(folder);
            Assertions.assertThat(reader.getDateOptional(FLAT_KEY, null)).isEqualTo(VALUE);
            Assertions.assertThat(reader.getKeys()).containsExactly(FLAT_KEY);
        } finally {
            deleteRecursively(folder);
        }
    }

    @Test
    public void aRemovedNestedKeyStaysRemovedInTheNextInstance() {
        final File folder = newTempFolder("nested-remove");
        try {
            final IProperties writer = new AtomicFilesProperties(folder);
            writer.setDate(NESTED_KEY, VALUE);
            writer.remove(NESTED_KEY);
            Assertions.assertThat(writer.getDateOptional(NESTED_KEY, null)).isNull();

            final IProperties reader = new AtomicFilesProperties(folder);
            Assertions.assertThat(reader.containsKey(NESTED_KEY))
                    .as("the scan must not resurrect a key whose file is gone")
                    .isFalse();
            Assertions.assertThat(reader.isEmpty())
                    .as("and the folder is empty again")
                    .isTrue();
        } finally {
            deleteRecursively(folder);
        }
    }

    private static File newTempFolder(final String name) {
        final File folder = new File(ContextProperties.TEMP_DIRECTORY,
                name + "-" + Long.toHexString(FDateNanos.elapsedNanos()));
        folder.mkdirs();
        Assertions.assertThat(folder).isDirectory();
        return folder;
    }

    private static void deleteRecursively(final File file) {
        final File[] children = file.listFiles();
        if (children != null) {
            for (final File child : children) {
                deleteRecursively(child);
            }
        }
        file.delete();
    }

}
