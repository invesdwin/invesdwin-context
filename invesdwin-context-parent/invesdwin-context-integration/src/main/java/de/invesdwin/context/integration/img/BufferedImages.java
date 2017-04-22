package de.invesdwin.context.integration.img;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;
import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;

import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public final class BufferedImages {

    public static final Set<String> SUPPORTED_FORMAT_NAMES;

    static {
        final Set<String> supportedFileExtensions = new HashSet<String>();
        for (final String writerFormatName : ImageIO.getWriterFormatNames()) {
            supportedFileExtensions.add(writerFormatName.toLowerCase());
        }
        SUPPORTED_FORMAT_NAMES = Collections.unmodifiableSet(supportedFileExtensions);
    }

    private BufferedImages() {}

    public static void resize(final File imgFile, final Dimension newDimension) throws IOException {
        final String formatName = FilenameUtils.getExtension(imgFile.getName()).toLowerCase();
        Assertions.assertThat(formatName).isIn(SUPPORTED_FORMAT_NAMES);
        final BufferedImage original = ImageIO.read(imgFile);
        final BufferedImage resized = resize(original, newDimension);
        ImageIO.write(resized, formatName, imgFile);
    }

    public static boolean isSupportedFormatName(final File imgFile) {
        final String formatName = FilenameUtils.getExtension(imgFile.getName()).toLowerCase();
        return SUPPORTED_FORMAT_NAMES.contains(formatName);
    }

    public static BufferedImage resize(final BufferedImage originalImage, final Dimension newDimension) {
        int type = originalImage.getType();
        if (type == 0) {
            type = BufferedImage.TYPE_INT_ARGB;
        }

        final BufferedImage resizedImage = new BufferedImage(newDimension.width, newDimension.height, type);
        final Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, newDimension.width, newDimension.height, null);
        g.dispose();
        g.setComposite(AlphaComposite.Src);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        return resizedImage;
    }

}
