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

    void setFilename(String filename);

    void setDirectory(String directory);

    byte[] getEmptyFileContent();

    void setEmptyFileContent(byte[] emptyFileContent);

    void createUniqueFile();

    void createUniqueFile(String filenamePrefix, String filenameSuffix);

    void connect();

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

    void upload(File file);

    void upload(byte[] bytes);

    void upload(InputStream input);

    void download(File destination);

    /**
     * Actually moves the file and overwrites if it already exists, though might use a safe rename if target file does
     * not exist.
     */
    void rename(String filename);

    byte[] download();

    void delete();

    OutputStream uploadOutputStream();

    File getLocalTempFile();

    void reconnect();

    InputStream downloadInputStream();

    /**
     * Creates a new instance with the given directory
     */
    IFileChannel withDirectory(String directory);

}
