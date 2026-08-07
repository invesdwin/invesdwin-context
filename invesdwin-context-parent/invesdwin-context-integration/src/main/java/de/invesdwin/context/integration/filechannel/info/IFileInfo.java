package de.invesdwin.context.integration.filechannel.info;

public interface IFileInfo extends IFileChannelInfo {

    boolean isFile();

    boolean isDirectory();

    Object unwrap();

}
