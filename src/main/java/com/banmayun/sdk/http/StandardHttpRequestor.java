package com.banmayun.sdk.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

public class StandardHttpRequestor extends HttpRequestor {

    public static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 35 * 1000;
    public static final int DEFAULT_READ_TIMEOUT_MILLIS = 35 * 1000;

    private static StandardHttpRequestor instance = new StandardHttpRequestor();

    public static StandardHttpRequestor getInstance() {
        return instance;
    }

    private Proxy proxy = null;

    private StandardHttpRequestor() {
        this(Proxy.NO_PROXY);
    }

    private StandardHttpRequestor(Proxy proxy) {
        this.proxy = proxy;
    }

    private static Response toResponse(HttpURLConnection conn) throws IOException {
        int responseCode = conn.getResponseCode();
        InputStream bodyStream;

        if (responseCode >= 400) {
            bodyStream = conn.getErrorStream();
        } else {
            bodyStream = conn.getInputStream();

        }
        return new Response(conn.getResponseCode(), bodyStream, conn.getHeaderFields());
    }

    @Override
    public Response doGet(String url, Iterable<Header> headers) throws IOException {
        HttpURLConnection conn = prepRequest(url, headers);
        conn.setRequestMethod("GET");
        conn.connect();
        return toResponse(conn);
    }

    @Override
    public Response doDelete(String url, Iterable<Header> headers) throws IOException {
        HttpURLConnection conn = prepRequest(url, headers);
        conn.setRequestMethod("DELETE");
        conn.connect();
        return toResponse(conn);
    }

    @Override
    public Uploader startPost(String url, Iterable<Header> headers) throws IOException {
        HttpURLConnection conn = prepRequest(url, headers);
        conn.setRequestMethod("POST");
        return new Uploader(conn);
    }

    @Override
    public Uploader startPut(String url, Iterable<Header> headers) throws IOException {
        HttpURLConnection conn = prepRequest(url, headers);
        conn.setRequestMethod("PUT");
        return new Uploader(conn);
    }

    protected void configureConnection(HttpURLConnection conn) throws IOException {
    }

    private static class Uploader extends HttpRequestor.Uploader {
        private HttpURLConnection conn;

        public Uploader(HttpURLConnection conn) throws IOException {
            super(getOutputStream(conn));
            conn.connect();
            this.conn = conn;
        }

        private static OutputStream getOutputStream(HttpURLConnection conn) throws IOException {
            conn.setDoOutput(true);
            return conn.getOutputStream();
        }

        @Override
        public void abort() {
            if (this.conn == null) {
                throw new IllegalStateException("Can't abort().  Uploader already closed.");
            }
            this.conn.disconnect();
        }

        @Override
        public void close() {
            if (this.conn == null) {
                return;
            }
            this.conn.disconnect();
        }

        @Override
        public Response finish() throws IOException {
            HttpURLConnection conn = this.conn;
            if (conn == null) {
                throw new IllegalStateException("Can't finish().  Uploader already closed.");
            }
            this.conn = null;
            Response response = toResponse(conn);
            return response;
        }
    }

    private HttpURLConnection prepRequest(String url, Iterable<Header> headers) throws IOException {
        URL urlObject = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlObject.openConnection(this.proxy);
        conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS);
        conn.setReadTimeout(DEFAULT_READ_TIMEOUT_MILLIS);
        conn.setUseCaches(false);
        conn.setAllowUserInteraction(false);

        configureConnection(conn);

        for (Header header : headers) {
            conn.addRequestProperty(header.key, header.value);
        }
        return conn;
    }
}
