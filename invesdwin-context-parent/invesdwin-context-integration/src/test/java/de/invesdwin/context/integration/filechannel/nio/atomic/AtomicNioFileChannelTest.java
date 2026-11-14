package de.invesdwin.context.integration.filechannel.nio.atomic;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.invesdwin.context.integration.filechannel.info.path.FileChannelPath;
import de.invesdwin.util.concurrent.lock.file.AtomicNioFileChannelContext;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.string.Charsets;

/**
 * Pins that the atomic channel really publishes by a single rename, through every one of its upload paths. The
 * primitive itself is pinned in the util module's Files test; what this test proves is the wiring: a channel that
 * went back to a plain replace-move would still pass every other test here but not this one.
 */
@NotThreadSafe
public class AtomicNioFileChannelTest {

    @TempDir
    private File tempDir;

    @Test
    public void testUploadPathsNeverLeaveTheTargetNameMissingDuringRepublish() throws Exception {
        final int payloadBytes = 100_000;
        final int repetitions = 100;
        final int pollers = 4;
        final File targetFile = new File(tempDir, "chain.index");
        final Path target = targetFile.toPath();
        final File sourceFile = new File(tempDir, "source.index");
        final byte[] payload = new byte[payloadBytes];
        for (int i = 0; i < payloadBytes; i++) {
            payload[i] = (byte) (i * 7);
        }
        Files.write(sourceFile.toPath(), payload);

        final AtomicNioFileChannel channel = new AtomicNioFileChannel(FileChannelPath.newFile(targetFile));
        channel.upload(payload);
        Assertions.assertThat(targetFile).exists();

        final AtomicBoolean stop = new AtomicBoolean(false);
        final AtomicInteger nameMissing = new AtomicInteger();
        final AtomicInteger halfWritten = new AtomicInteger();
        final AtomicInteger wholeReads = new AtomicInteger();
        final CountDownLatch ready = new CountDownLatch(pollers);
        final List<Thread> pollingThreads = new ArrayList<>();
        for (int i = 0; i < pollers; i++) {
            final Thread polling = new Thread(() -> {
                ready.countDown();
                while (!stop.get()) {
                    //stat only: a content read per poll would be slower than the publishes and hide the window
                    if (!Files.exists(target)) {
                        nameMissing.incrementAndGet();
                    } else {
                        try {
                            if (Files.size(target) != payloadBytes) {
                                halfWritten.incrementAndGet();
                            } else {
                                wholeReads.incrementAndGet();
                            }
                        } catch (final NoSuchFileException e) {
                            nameMissing.incrementAndGet();
                        } catch (final IOException e) {
                            halfWritten.incrementAndGet();
                        }
                    }
                }
            }, "atomic-channel-poller");
            pollingThreads.add(polling);
            polling.start();
        }
        ready.await();
        try {
            for (int i = 0; i < repetitions; i++) {
                //all three publish paths of the class, so a regression in any one of them is visible here
                channel.upload(sourceFile);
                channel.upload(payload);
                try (OutputStream out = channel.newUpload()) {
                    out.write(payload);
                }
            }
        } finally {
            stop.set(true);
            for (final Thread polling : pollingThreads) {
                polling.join(10000);
            }
        }
        Assertions.assertThat(nameMissing.get())
                .as("an upload onto an existing name must never leave that name missing for a reader")
                .isZero();
        Assertions.assertThat(halfWritten.get()).as("an upload must never be observable half written").isZero();
        //a green run that never watched an upload would prove nothing
        Assertions.assertThat(wholeReads.get()).as("the pollers must have been watching the uploads")
                .isGreaterThan(1000);
        Assertions.assertThat(Files.readAllBytes(target)).isEqualTo(payload);
    }

    /**
     * Claim mode: a name may be published by one writer only, through each of the three upload paths. The default mode
     * is pinned in the test above (it re-publishes the same name 100 times); this is the other half of the switch in
     * {@code publish(Path, Path)}, and the negative control for anyone who thinks a claim is just a rename.
     */
    @Test
    public void testClaimModeRefusesASecondPublishOfTheSameNameOnEveryUploadPath() throws Exception {
        final File directory = new File(tempDir, "claim");
        Files.forceMkdir(directory);
        final File targetFile = new File(directory, "7_memory.index");
        final AtomicNioFileChannelContext claimContext = new AtomicNioFileChannelContext(true);
        final byte[] first = "FIRST".getBytes(Charsets.UTF_8);
        final byte[] second = "SECOND".getBytes(Charsets.UTF_8);

        new AtomicNioFileChannel(FileChannelPath.newFile(targetFile), claimContext).upload(first);
        Assertions.assertThat(Files.readAllBytes(targetFile.toPath()))
                .as("the first publisher's claim wins")
                .isEqualTo(first);

        assertRefused(() -> new AtomicNioFileChannel(FileChannelPath.newFile(targetFile), claimContext)
                .upload(second), "upload(byte[])");
        final AtomicNioFileChannel filePublisher = new AtomicNioFileChannel(FileChannelPath.newFile(targetFile),
                claimContext);
        final File sourceFile = new File(directory, "source.index");
        Files.write(sourceFile.toPath(), second);
        assertRefused(() -> filePublisher.upload(sourceFile), "upload(File)");
        assertRefused(() -> {
            try (OutputStream out = new AtomicNioFileChannel(FileChannelPath.newFile(targetFile), claimContext)
                    .newUpload()) {
                out.write(second);
            }
        }, "newUpload()");

        Assertions.assertThat(Files.readAllBytes(targetFile.toPath()))
                .as("a refused claim must leave what the winner published exactly as it was")
                .isEqualTo(first);
    }

    /**
     * The mode lives on the context, not the channel, because every with* factory clones the context into the derived
     * channel. If it were a channel field these factories would silently hand back a replace-mode channel.
     */
    @Test
    public void testClaimModeSurvivesTheDerivedChannelFactories() throws Exception {
        final File directory = new File(tempDir, "claimDerived");
        Files.forceMkdir(directory);
        final AtomicNioFileChannel base = new AtomicNioFileChannel(FileChannelPath.newDirectory(directory),
                new AtomicNioFileChannelContext(true));
        final AtomicNioFileChannel derived = base.withFilename("8_memory.index");
        derived.upload("FIRST".getBytes(Charsets.UTF_8));
        assertRefused(() -> derived.upload("SECOND".getBytes(Charsets.UTF_8)), "withFilename(...) derived channel");
    }

    private interface IThrowingRun {
        void run() throws Exception;
    }

    private void assertRefused(final IThrowingRun run, final String path) throws Exception {
        try {
            run.run();
            Assertions.fail("claim mode must refuse a second publish of one name via " + path);
        } catch (final FileAlreadyExistsException e) {
            //newUpload() surfaces it straight from close()
        } catch (final UncheckedIOException e) {
            Assertions.assertThat(e.getCause())
                    .as("upload must report the refusal as the file system's own verdict on " + path)
                    .isInstanceOf(FileAlreadyExistsException.class);
        }
    }
}
