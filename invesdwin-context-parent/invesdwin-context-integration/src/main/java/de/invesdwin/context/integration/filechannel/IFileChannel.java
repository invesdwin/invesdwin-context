package de.invesdwin.context.integration.filechannel;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import de.invesdwin.context.integration.filechannel.info.IFileChannelInfo;
import de.invesdwin.context.integration.filechannel.info.IFileInfo;
import de.invesdwin.util.streams.closeable.ISafeCloseable;

public interface IFileChannel extends ISafeCloseable, IFileChannelInfo {

    IFileChannel setFilename(String filename);

    /**
     * Sets the relative sub-directory within the base path.
     */
    IFileChannel setSubDirectory(String subDirectory);

    byte[] getEmptyFileContent();

    IFileChannel setEmptyFileContent(byte[] emptyFileContent);

    IFileChannel createUniqueFile();

    IFileChannel createUniqueFile(String filenamePrefix, String filenameSuffix);

    IFileChannel connect();

    boolean isConnected();

    boolean exists();

    IFileInfo info();

    List<? extends IFileInfo> list();

    default List<? extends IFileInfo> listFiles() {
        final List<? extends IFileInfo> list = list();
        final List<IFileInfo> files = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            final IFileInfo file = list.get(i);
            if (file.isFile()) {
                files.add(file);
            }
        }
        return files;
    }

    default List<? extends IFileInfo> listDirectories() {
        final List<? extends IFileInfo> list = list();
        final List<IFileInfo> directories = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            final IFileInfo directory = list.get(i);
            if (directory.isDirectory()) {
                directories.add(directory);
            }
        }
        return directories;
    }

    IFileChannel upload(File file);

    IFileChannel upload(byte[] bytes);

    IFileChannel upload(InputStream input);

    IFileChannel download(File destination);

    IFileChannel rename(String filename);

    byte[] download();

    IFileChannel delete();

    OutputStream newUpload();

    File getLocalTempFile();

    IFileChannel reconnect();

    InputStream newDownload();

    /**
     * Creates a new instance with the given relative sub-directory.
     */
    IFileChannel withSubDirectory(String subDirectory);

}