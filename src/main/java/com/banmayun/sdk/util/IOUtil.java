package com.banmayun.sdk.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

public class IOUtil {

    public static final int DEFAULT_COPY_BUFFER_SIZE = 16 * 1024;

    public static void copyStreamToStream(InputStream in, OutputStream out) throws ReadException, WriteException {
        copyStreamToStream(in, out, DEFAULT_COPY_BUFFER_SIZE);
    }

    public static void copyStreamToStream(InputStream in, OutputStream out, byte[] copyBuffer) throws ReadException,
            WriteException {
        while (true) {
            int count;
            try {
                count = in.read(copyBuffer);
            } catch (IOException ex) {
                throw new ReadException(ex);
            }

            if (count == -1) {
                break;
            }

            try {
                out.write(copyBuffer, 0, count);
            } catch (IOException ex) {
                throw new WriteException(ex);
            }
        }
    }

    public static void copyStreamToStream(InputStream in, OutputStream out, int copyBufferSize) throws ReadException,
            WriteException {
        copyStreamToStream(in, out, new byte[copyBufferSize]);
    }

    private static ThreadLocal<byte[]> slurpBuffer = new ThreadLocal<byte[]>() {

        @Override
        protected byte[] initialValue() {
            return new byte[4096];
        }
    };

    public static byte[] slurp(InputStream in, int byteLimit) throws IOException {
        if (byteLimit < 0) {
            throw new RuntimeException("'byteLimit' must be non-negative: " + byteLimit);
        }
        byte[] copyBuffer = slurpBuffer.get();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copyStreamToStream(in, baos, copyBuffer);
        return baos.toByteArray();
    }

    public void copyFileToStream(File fin, OutputStream out) throws ReadException, WriteException {
        copyFileToStream(fin, out, DEFAULT_COPY_BUFFER_SIZE);
    }

    public void copyFileToStream(File fin, OutputStream out, int copyBufferSize) throws ReadException, WriteException {
        FileInputStream in;
        try {
            in = new FileInputStream(fin);
        } catch (IOException ex) {
            throw new ReadException(ex);
        }

        try {
            copyStreamToStream(in, out, copyBufferSize);
        } finally {
            closeInput(in);
        }
    }

    public void copyStreamToFile(InputStream in, File fout) throws ReadException, WriteException {
        copyStreamToFile(in, fout, DEFAULT_COPY_BUFFER_SIZE);
    }

    public void copyStreamToFile(InputStream in, File fout, int copyBufferSize) throws ReadException, WriteException {
        FileOutputStream out;
        try {
            out = new FileOutputStream(fout);
        } catch (IOException ex) {
            throw new WriteException(ex);
        }

        try {
            copyStreamToStream(in, out, copyBufferSize);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                throw new WriteException(ex);
            }
        }
    }

    public static void closeInput(InputStream in) {
        try {
            in.close();
        } catch (IOException e) {
        }
    }

    public static void closeInput(Reader in) {
        try {
            in.close();
        } catch (IOException e) {
        }
    }

    public static abstract class WrappedException extends IOException {

        private static final long serialVersionUID = 1L;

        private IOException underlying = null;
        private String message = null;

        public WrappedException(String message, IOException underlying) {
            this.message = message;
            this.underlying = underlying;
        }

        public WrappedException(IOException underlying) {
            this("", underlying);
        }

        @Override
        public String getMessage() {
            String m = this.underlying.getMessage();
            if (m == null) {
                return "";
            } else {
                return "" + message + ": " + m;
            }
        }

        @Override
        public IOException getCause() {
            return this.underlying;
        }
    }

    public static final class ReadException extends WrappedException {

        private static final long serialVersionUID = 1L;

        public ReadException(String message, IOException underlying) {
            super(message, underlying);
        }

        public ReadException(IOException underlying) {
            super(underlying);
        }
    }

    public static final class WriteException extends WrappedException {

        private static final long serialVersionUID = 1L;

        public WriteException(String message, IOException underlying) {
            super(message, underlying);
        }

        public WriteException(IOException underlying) {
            super(underlying);
        }
    }

    public static final InputStream EMPTY_INPUT_STREAM = new InputStream() {

        @Override
        public int read() {
            return -1;
        }

        @Override
        public int read(byte[] data) {
            return -1;
        }

        @Override
        public int read(byte[] data, int off, int len) {
            return -1;
        }
    };

    public static final OutputStream BLACK_HOLE_OUTPUT_STREAM = new OutputStream() {

        @Override
        public void write(int b) {
        }

        @Override
        public void write(byte[] data) {
        }

        @Override
        public void write(byte[] data, int off, int len) {
        }
    };
}
