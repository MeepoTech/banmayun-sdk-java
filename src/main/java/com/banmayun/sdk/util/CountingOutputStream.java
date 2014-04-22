package com.banmayun.sdk.util;

import java.io.IOException;
import java.io.OutputStream;

public class CountingOutputStream extends OutputStream {

    private OutputStream out = null;
    private long bytesWritten = 0L;

    public CountingOutputStream(OutputStream out) {
        this.out = out;
    }

    public long getBytesWritten() {
        return bytesWritten;
    }

    @Override
    public void write(int b) throws IOException {
        this.bytesWritten++;
        this.out.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.bytesWritten += b.length;
        this.out.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.bytesWritten += len;
        this.out.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        this.out.flush();
    }

    @Override
    public void close() throws IOException {
        throw new UnsupportedOperationException("You aren't allowed to call close() on this object.");
    }
}
