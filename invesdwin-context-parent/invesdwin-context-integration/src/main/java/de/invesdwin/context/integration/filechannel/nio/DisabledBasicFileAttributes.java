package de.invesdwin.context.integration.filechannel.nio;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class DisabledBasicFileAttributes implements BasicFileAttributes {

    public static final DisabledBasicFileAttributes INSTANCE = new DisabledBasicFileAttributes();

    private static final FileTime ZERO_TIME = FileTime.fromMillis(0);

    private DisabledBasicFileAttributes() {}

    @Override
    public FileTime lastModifiedTime() {
        return ZERO_TIME;
    }

    @Override
    public FileTime lastAccessTime() {
        return ZERO_TIME;
    }

    @Override
    public FileTime creationTime() {
        return ZERO_TIME;
    }

    @Override
    public boolean isRegularFile() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public boolean isSymbolicLink() {
        return false;
    }

    @Override
    public boolean isOther() {
        return false;
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public Object fileKey() {
        return null;
    }

}
