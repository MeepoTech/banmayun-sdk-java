package com.banmayun.sdk;

import java.io.IOException;
import java.io.OutputStream;

public final class NoThrowOutputStream extends OutputStream {

    private OutputStream underlying = null;
    private long bytesWritten = 0;

    public NoThrowOutputStream(OutputStream underlying) {
        this.underlying = underlying;
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("don't call close()");
    }

    @Override
    public void flush() {
        try {
            this.underlying.flush();
        } catch (IOException e) {
            throw new HiddenException(e);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) {
        try {
            this.bytesWritten += len;
            this.underlying.write(b, off, len);
        } catch (IOException e) {
            throw new HiddenException(e);
        }
    }

    @Override
    public void write(byte[] b) {
        try {
            this.bytesWritten += b.length;
            this.underlying.write(b);
        } catch (IOException e) {
            throw new HiddenException(e);
        }
    }

    @Override
    public void write(int b) throws IOException {
        try {
            this.bytesWritten += 1;
            this.underlying.write(b);
        } catch (IOException e) {
            throw new HiddenException(e);
        }
    }

    public static final class HiddenException extends RuntimeException {

        public final IOException underlying;

        public HiddenException(IOException underlying) {
            super(underlying);
            this.underlying = underlying;
        }

        public static final long serialVersionUID = 0L;
    }

    public long getBytesWritten() {
        return this.bytesWritten;
    }
}
