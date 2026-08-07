package de.invesdwin.context.integration.filechannel.info;

import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.time.date.FDate;

public interface IFileChannelInfo extends ISerializableValueObject {

    String getServerUri();

    default String getDirectoryUri() {
        return FileChannelInfos.newDirectoryUri(this);
    }

    default String getFileUri() {
        return FileChannelInfos.newFileUri(this);
    }

    String getDirectory();

    String getFilename();

    default String getAbsolutePath() {
        return FileChannelInfos.newAbsolutePath(this);
    }

    FDate lastModified();

    long length();

}
