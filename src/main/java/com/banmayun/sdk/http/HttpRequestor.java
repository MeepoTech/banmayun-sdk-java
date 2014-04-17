package com.banmayun.sdk.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public abstract class HttpRequestor {

    public abstract Response doGet(String url, Iterable<Header> headers) throws IOException;

    public abstract Response doDelete(String url, Iterable<Header> headers) throws IOException;

    public abstract Uploader startPost(String url, Iterable<Header> headers) throws IOException;

    public abstract Uploader startPut(String url, Iterable<Header> headers) throws IOException;

    public static final class Header {
        public final String key;
        public final String value;

        public Header(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    public static abstract class Uploader {
        public final OutputStream body;

        protected Uploader(OutputStream body) {
            this.body = body;
        }

        public abstract void close();

        public abstract void abort();

        public abstract Response finish() throws IOException;
    }

    public static final class Response {
        public final int statusCode;
        public final InputStream body;
        public final Map<String, ? extends List<String>> headers;

        public Response(int statusCode, InputStream body, Map<String, ? extends List<String>> headers) {
            this.statusCode = statusCode;
            this.body = body;
            this.headers = headers;
        }
    }
}
