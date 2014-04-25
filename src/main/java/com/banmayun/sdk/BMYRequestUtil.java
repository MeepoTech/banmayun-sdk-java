package com.banmayun.sdk;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.banmayun.sdk.core.ErrorResponse;
import com.banmayun.sdk.http.HttpRequestor;
import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.IOUtil;
import com.banmayun.sdk.util.StringUtil;

public class BMYRequestUtil {

    public static String encodeUrlParam(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            AssertionError ae = new AssertionError("UTF-8 not supported?  Should never happen, right?");
            ae.initCause(ex);
            throw ae;
        }
    }

    private static String encodeUrlParams(String token, String userLocale, String[] params) {
        StringBuilder buf = new StringBuilder();
        String sep = "";
        if (token != null) {
            buf.append(sep);
            sep = "&";
            buf.append("token=").append(token);
        }
        if (userLocale != null) {
            buf.append(sep);
            sep = "&";
            buf.append("locale=").append(userLocale);
        }

        if (params != null) {
            if (params.length % 2 != 0) {
                throw new IllegalArgumentException("'params.length' is " + params.length
                        + "; expecting a multiple of two");
            }
            for (int i = 0; i < params.length;) {
                String key = params[i++];
                String value = params[i++];
                if (value != null) {
                    buf.append(sep);
                    sep = "&";
                    buf.append(encodeUrlParam(key));
                    buf.append("=");
                    buf.append(encodeUrlParam(value));
                }
            }
        }

        return buf.toString();
    }

    public static String buildUri(String host, String path) {
        return "http://" + host + "/" + path;
    }

    public static String buildUrlWithParams(String host, String path, String token, String userLocale, String[] params) {
        String url = buildUri(host, path) + "?" + encodeUrlParams(token, userLocale, params);
        return url;
    }

    public static List<HttpRequestor.Header> addUserAgentHeader(List<HttpRequestor.Header> headers,
            BMYRequestConfig requestConfig) {
        if (headers == null) {
            headers = new ArrayList<HttpRequestor.Header>();
        }
        headers.add(buildUserAgentHeader(requestConfig));
        return headers;
    }

    public static HttpRequestor.Header buildUserAgentHeader(BMYRequestConfig requestConfig) {
        return new HttpRequestor.Header("User-Agent", requestConfig.clientIdentifier + " Banmayun-Java-SDK/"
                + BMYSdkVersion.Version);
    }

    public static HttpRequestor.Response startGet(BMYRequestConfig requestConfig, String host, String path,
            String token, String[] params, List<HttpRequestor.Header> headers) throws BMYException.NetworkIO {
        headers = addUserAgentHeader(headers, requestConfig);

        String url = buildUrlWithParams(host, path, token, requestConfig.userLocale, params);
        try {
            return requestConfig.httpRequestor.doGet(url, headers);
        } catch (IOException ex) {
            throw new BMYException.NetworkIO(ex);
        }
    }

    public static HttpRequestor.Response startDelete(BMYRequestConfig requestConfig, String host, String path,
            String token, String[] params, List<HttpRequestor.Header> headers) throws BMYException.NetworkIO {
        headers = addUserAgentHeader(headers, requestConfig);

        String url = buildUrlWithParams(host, path, token, requestConfig.userLocale, params);
        try {
            return requestConfig.httpRequestor.doDelete(url, headers);
        } catch (IOException ex) {
            throw new BMYException.NetworkIO(ex);
        }
    }

    public static HttpRequestor.Response startPost(BMYRequestConfig requestConfig, String host, String path,
            String token, String[] params, String body, List<HttpRequestor.Header> headers) throws BMYException {
        String url = buildUrlWithParams(host, path, token, requestConfig.userLocale, params);
        headers = addUserAgentHeader(headers, requestConfig);
        headers.add(new HttpRequestor.Header("Content-Type", "application/json"));

        try {
            HttpRequestor.Uploader uploader = requestConfig.httpRequestor.startPost(url, headers);
            try {
                if (body != null) {
                    uploader.getBody().write(body.getBytes());
                }
                return uploader.finish();
            } finally {
                uploader.close();
            }
        } catch (IOException ex) {
            throw new BMYException.NetworkIO(ex);
        }
    }

    public static byte[] loadErrorBody(HttpRequestor.Response response) throws BMYException.NetworkIO {
        // Slurp the body into memory (up to 4k; anything past that is probably
        // not useful).
        try {
            return IOUtil.slurp(response.body, 4096);
        } catch (IOException ex) {
            throw new BMYException.NetworkIO(ex);
        }
    }

    public static String parseErrorBody(int statusCode, byte[] body) throws BMYException.BadResponse {
        // Read the error message from the body.
        try {
            return StringUtil.utf8ToString(body);
        } catch (CharacterCodingException e) {
            throw new BMYException.BadResponse("Got non-UTF8 response body: " + statusCode + ": " + e.getMessage());
        }
    }

    public static BMYException unexpectedStatus(HttpRequestor.Response response) throws BMYException.NetworkIO,
            BMYException.BadResponse {
        byte[] body = loadErrorBody(response);
        String message = parseErrorBody(response.statusCode, body);
        BufferedReader br = new BufferedReader(new InputStreamReader(response.body));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw new BMYException.BadResponse(message, e);
        }
        ErrorResponse errorResponse = readJsonFromResponse(ErrorResponse.reader, new ByteArrayInputStream(body));
        if (errorResponse == null) {
            return new BMYException.BadResponse(message);
        }

        if (response.statusCode == 400) {
            return new BMYException.BadRequest(errorResponse);
        }
        if (response.statusCode == 401) {
            return new BMYException.InvalidToken(errorResponse);
        }
        if (response.statusCode == 403) {
            return new BMYException.AccessDenied(errorResponse);
        }
        if (response.statusCode == 404) {
            return new BMYException.NotFound(errorResponse);
        }
        if (response.statusCode == 409) {
            return new BMYException.AlreadyExists(errorResponse);
        }
        if (response.statusCode == 422) {
            return new BMYException.UnacceptableRequest(errorResponse);
        }
        if (response.statusCode == 424) {
            return new BMYException.OperationNotAllowed(errorResponse);
        }
        if (response.statusCode == 500) {
            return new BMYException.ServerError(errorResponse);
        }
        if (response.statusCode == 503) {
            return new BMYException.RetryLater(errorResponse);
        }
        if (response.statusCode == 507) {
            return new BMYException.QuotaOutage(errorResponse);
        }

        return new BMYException.BadResponseCode("unexpected HTTP status code: " + response.statusCode + ": " + message,
                response.statusCode);
    }

    public static <T> T readJsonFromResponse(JsonReader<T> reader, InputStream body) throws BMYException.BadResponse,
            BMYException.NetworkIO {
        try {
            return reader.readFully(body);
        } catch (JsonReadException ex) {
            throw new BMYException.BadResponse("error in response JSON: " + ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new BMYException.NetworkIO(ex);
        }
    }

    public static abstract class ResponseHandler<T> {
        public abstract T handle(HttpRequestor.Response response) throws BMYException;
    }

    public static Map<String, String> parseAsQueryString(InputStream in) throws BMYException {
        // TODO: Maybe just slurp string up to a max limit.
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(in).useDelimiter("&");
        Map<String, String> result = new HashMap<String, String>();
        try {
            while (scanner.hasNext()) {
                String pair = scanner.next();

                // The 'Scanner' class masks any IOExceptions that happen on
                // '.next()', so we have to check for them explicitly.
                IOException ioe = scanner.ioException();
                if (ioe != null) {
                    throw new BMYException.NetworkIO(ioe);
                }

                String[] parts = pair.split("=");
                if (parts.length < 2) {
                    throw new BMYException.BadResponse("expecting a name-value pair, but there's no '=': \"" + pair
                            + "\"");
                } else if (parts.length > 2) {
                    throw new BMYException.BadResponse(
                            "expecting a single name-value pair, but there's more than one '=': \"" + pair + "\"");
                }
                String displaced = result.put(parts[0], parts[1]);
                if (displaced != null) {
                    throw new BMYException.BadResponse("duplicate query parameter name: \"" + parts[0] + "\"");
                }
            }
        } finally {
            scanner.close();
        }

        return result;
    }

    public static <T> T doGet(BMYRequestConfig requestConfig, String host, String path, String token, String[] params,
            List<HttpRequestor.Header> headers, ResponseHandler<T> handler) throws BMYException {
        HttpRequestor.Response response = startGet(requestConfig, host, path, token, params, headers);
        try {
            return handler.handle(response);
        } finally {
            try {
                response.body.close();
            } catch (IOException ex) {
                throw new BMYException.NetworkIO(ex);
            }
        }
    }

    public static <T> T doDelete(BMYRequestConfig requestConfig, String host, String path, String token,
            String[] params, List<HttpRequestor.Header> headers, ResponseHandler<T> handler) throws BMYException {
        HttpRequestor.Response response = startDelete(requestConfig, host, path, token, params, headers);
        try {
            return handler.handle(response);
        } finally {
            try {
                response.body.close();
            } catch (IOException ex) {
                throw new BMYException.NetworkIO(ex);
            }
        }
    }

    public static <T> T doPost(BMYRequestConfig requestConfig, String host, String path, String token, String[] params,
            String body, List<HttpRequestor.Header> headers, ResponseHandler<T> handler) throws BMYException {
        HttpRequestor.Response response = startPost(requestConfig, host, path, token, params, body, headers);
        return finishResponse(response, handler);
    }

    public static HttpRequestor.Uploader getUploaderWithPost(BMYRequestConfig requestConfig, String host, String path,
            String token, String[] params, List<HttpRequestor.Header> headers) throws BMYException {
        String url = buildUrlWithParams(host, path, token, requestConfig.userLocale, params);
        headers = addUserAgentHeader(headers, requestConfig);

        try {
            HttpRequestor.Uploader uploader = requestConfig.httpRequestor.startPost(url, headers);
            return uploader;
        } catch (IOException e) {
            throw new BMYException.NetworkIO(e);
        }
    }

    public static <T> T finishResponse(HttpRequestor.Response response, ResponseHandler<T> handler) throws BMYException {
        try {
            if (handler != null) {
                return handler.handle(response);
            } else {
                return null;
            }
        } finally {
            IOUtil.closeInput(response.body);
        }
    }

    public static String getFirstHeader(HttpRequestor.Response response, String name) throws BMYException {
        List<String> values = response.headers.get(name);
        if (values == null) {
            throw new BMYException.BadResponse("missing HTTP header \"" + name + "\"");
        }
        assert !values.isEmpty();
        return values.get(0);
    }

    public static String getFirstHeaderMaybe(HttpRequestor.Response response, String name) throws BMYException {
        List<String> values = response.headers.get(name);
        if (values == null) {
            return null;
        }
        assert !values.isEmpty();
        return values.get(0);
    }

    public static abstract class RequestMaker<T, E extends Throwable> {
        public abstract T run() throws BMYException, E;
    }

    public static <T, E extends Throwable> T runAndRetry(int maxTries, RequestMaker<T, E> requestMaker)
            throws BMYException, E {
        int numTries = 0;
        while (true) {
            try {
                numTries++;
                return requestMaker.run();
            } catch (BMYException ex) {
                // If we can't retry, just let this exception through.
                if (!isRetriableException(ex) || numTries >= maxTries) {
                    throw ex;
                }
                // Otherwise, run through the loop again.
            }
        }
    }

    private static boolean isRetriableException(BMYException ex) {
        return ex instanceof BMYException.RetryLater || ex instanceof BMYException.ServerError;
    }
}
