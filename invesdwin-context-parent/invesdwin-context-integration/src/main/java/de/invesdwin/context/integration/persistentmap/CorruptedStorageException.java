package de.invesdwin.context.integration.persistentmap;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class CorruptedStorageException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CorruptedStorageException() {
        super();
    }

    public CorruptedStorageException(final String message) {
        super(message);
    }

    public CorruptedStorageException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CorruptedStorageException(final Throwable cause) {
        super(cause);
    }
}
