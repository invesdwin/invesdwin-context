package de.invesdwin.context.integration.filechannel.info;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.string.Strings;

@Immutable
public final class FileChannelInfos {

    private FileChannelInfos() {}

    public static String newDirectoryUri(final IFileChannelInfo info) {
        final String serverUti = info.getServerUri();
        final String directory = info.getDirectory();
        return newDirectoryUri(serverUti, directory);
    }

    public static String newDirectoryUri(final String serverUti, final String directory) {
        return Strings.putSuffix(serverUti, "/") + Strings.putSuffix(directory, "/");
    }

    public static String newFileUri(final IFileChannelInfo info) {
        return newFileUri(info.getServerUri(), info.getDirectory(), info.getFilename());
    }

    public static String newFileUri(final String serverUri, final String directory, final String filename) {
        return toString(serverUri, directory, filename);
    }

    public static String newAbsolutePath(final IFileChannelInfo info) {
        final String directory = info.getDirectory();
        final String filename = info.getFilename();
        return newAbsolutePath(directory, filename);
    }

    public static String newAbsolutePath(final String directory, final String filename) {
        return Strings.putSuffix(directory, "/") + filename;
    }

    public static String toString(final IFileChannelInfo info) {
        return toString(info.getServerUri(), info.getDirectory(), info.getFilename());
    }

    public static String toString(final String serverUri, final String directory, final String filename) {
        return Strings.putSuffix(serverUri, "/") + Strings.putSuffix(directory, "/") + filename;
    }

}
