package de.invesdwin.context.integration.filechannel;

import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.time.fdate.FDate;

public interface IFileChannel<FILEINFO> extends Closeable, ISerializableValueObject {

    String getDirectory();

    void setFilename(String filename);

    String getFilename();

    byte[] getEmptyFileContent();

    void setEmptyFileContent(byte[] emptyFileContent);

    void createUniqueFile();

    void createUniqueFile(String filenamePrefix, String filenameSuffix);

    void connect();

    boolean isConnected();

    boolean exists();

    long size();

    FDate modified();

    FILEINFO info();

    List<FILEINFO> list();

    List<FILEINFO> listFiles();

    List<FILEINFO> listDirectories();

    void upload(File file);

    void upload(byte[] bytes);

    void upload(InputStream input);

    byte[] download();

    void delete();

    OutputStream uploadOutputStream();

    File getLocalTempFile();

    void reconnect();

    InputStream downloadInputStream();

}
