package com.banmayun.sdk;

import java.io.IOException;
import java.io.InputStream;
import com.banmayun.sdk.util.IOUtil;

public abstract class BMYStreamWriter<E extends Throwable> {

    public abstract void write(NoThrowOutputStream out) throws E;

    public static final class InputStreamCopier extends BMYStreamWriter<IOException> {

        private InputStream source = null;

        public InputStreamCopier(InputStream source) {
            this.source = source;
        }

        @Override
        public void write(NoThrowOutputStream out) throws IOException {
            IOUtil.copyStreamToStream(this.source, out);
        }
    }

    public static final class ByteArrayCopier extends BMYStreamWriter<RuntimeException> {

        private byte[] data = null;
        private int offset = 0;
        private int length = -1;

        public ByteArrayCopier(byte[] data, int offset, int length) {
            if (data == null) {
                throw new IllegalArgumentException("'data' can't be null");
            }
            if (offset < 0 || offset >= data.length) {
                throw new IllegalArgumentException("'offset' is out of bounds");
            }
            if ((offset + length) < offset || (offset + length) > data.length) {
                throw new IllegalArgumentException("'offset+length' is out of bounds");
            }
            this.data = data;
            this.offset = offset;
            this.length = length;
        }

        public ByteArrayCopier(byte[] data) {
            this(data, 0, data.length);
        }

        @Override
        public void write(NoThrowOutputStream out) {
            out.write(this.data, this.offset, this.length);
        }
    }
}
