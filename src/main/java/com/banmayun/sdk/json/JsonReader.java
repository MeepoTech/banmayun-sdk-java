package com.banmayun.sdk.json;

import com.banmayun.sdk.util.IOUtil;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class JsonReader<T> {

    public abstract T read(JsonParser parser) throws IOException, JsonReadException;

    public final T readField(JsonParser parser, String fieldName, T v) throws IOException, JsonReadException {
        if (v != null) {
            throw new JsonReadException("duplicate field \"" + fieldName + "\"", parser.getTokenLocation());
        }
        return read(parser);
    }

    public final T readOptional(JsonParser parser) throws IOException, JsonReadException {
        if (parser.getCurrentToken() == JsonToken.VALUE_NULL) {
            parser.nextToken();
            return null;
        } else {
            return read(parser);
        }
    }

    public static JsonToken nextToken(JsonParser parser) throws IOException, JsonReadException {
        try {
            return parser.nextToken();
        } catch (JsonParseException ex) {
            throw JsonReadException.fromJackson(ex);
        }
    }

    public static JsonLocation expectObjectStart(JsonParser parser) throws IOException, JsonReadException {
        if (parser.getCurrentToken() != JsonToken.START_OBJECT) {
            throw new JsonReadException("expecting the start of an object (\"{\")", parser.getTokenLocation());
        }
        JsonLocation loc = parser.getTokenLocation();
        nextToken(parser);
        return loc;
    }

    public static void expectObjectEnd(JsonParser parser) throws IOException, JsonReadException {
        if (parser.getCurrentToken() != JsonToken.END_OBJECT) {
            throw new JsonReadException("expecting the end of an object (\"}\")", parser.getTokenLocation());
        }
        nextToken(parser);
    }

    public static JsonLocation expectArrayStart(JsonParser parser) throws IOException, JsonReadException {
        if (parser.getCurrentToken() != JsonToken.START_ARRAY) {
            throw new JsonReadException("expecting the start of an array (\"[\")", parser.getTokenLocation());
        }
        JsonLocation loc = parser.getTokenLocation();
        nextToken(parser);
        return loc;
    }

    public static boolean isArrayEnd(JsonParser parser) {
        return (parser.getCurrentToken() == JsonToken.END_ARRAY);
    }

    public static void skipValue(JsonParser parser) throws IOException, JsonReadException {
        try {
            parser.skipChildren();
            parser.nextToken();
        } catch (JsonParseException ex) {
            throw JsonReadException.fromJackson(ex);
        }
    }

    public static long readUnsignedLong(JsonParser parser) throws IOException, JsonReadException {
        try {
            long v = parser.getLongValue();
            if (v < 0) {
                throw new JsonReadException("expecting a non-negative number, got: " + v, parser.getTokenLocation());
            }
            parser.nextToken();
            return v;
        } catch (JsonParseException ex) {
            throw JsonReadException.fromJackson(ex);
        }
    }

    public static long readUnsignedLongField(JsonParser parser, String fieldName, long v) throws IOException,
            JsonReadException {
        if (v >= 0) {
            throw new JsonReadException("duplicate field \"" + fieldName + "\"", parser.getCurrentLocation());
        }
        return JsonReader.readUnsignedLong(parser);
    }

    public static final JsonReader<String> STRING_READER = new JsonReader<String>() {
        @Override
        public String read(JsonParser parser) throws IOException, JsonReadException {
            try {
                String v = parser.getText();
                parser.nextToken();
                return v;
            } catch (JsonParseException ex) {
                throw JsonReadException.fromJackson(ex);
            }
        }
    };

    public static final JsonReader<Boolean> BOOLEAN_READER = new JsonReader<Boolean>() {
        @Override
        public Boolean read(JsonParser parser) throws IOException, JsonReadException {
            return readBoolean(parser);
        }
    };

    public static boolean readBoolean(JsonParser parser) throws IOException, JsonReadException {
        try {
            boolean b = parser.getBooleanValue();
            parser.nextToken();
            return b;
        } catch (JsonParseException ex) {
            throw JsonReadException.fromJackson(ex);
        }
    }

    public static class FieldMapping {
        private Map<String, Integer> fields = null;

        private FieldMapping(Map<String, Integer> fields) {
            assert fields != null;
            this.fields = fields;
        }

        public int get(String fieldName) {
            Integer i = this.fields.get(fieldName);
            if (i == null) {
                return -1;
            }
            return i;
        }

        public static class Builder {
            private Map<String, Integer> fields = new HashMap<String, Integer>();

            public void add(String fieldName, int expectedIndex) {
                if (this.fields == null) {
                    throw new IllegalStateException("already called build(); can't call add() anymore");
                }
                int i = this.fields.size();
                if (expectedIndex != i) {
                    throw new IllegalStateException("expectedIndex = " + expectedIndex + ", actual = " + i);
                }
                Object displaced = this.fields.put(fieldName, i);
                if (displaced != null) {
                    throw new IllegalStateException("duplicate field name: \"" + fieldName + "\"");
                }
            }

            public FieldMapping build() {
                if (this.fields == null) {
                    throw new IllegalStateException("already called build(); can't call build() again");
                }
                Map<String, Integer> f = this.fields;
                this.fields = null;
                return new FieldMapping(f);
            }
        }
    }

    public static final JsonFactory JSON_FACTORY = new JsonFactory();

    public T readFully(InputStream utf8Body) throws IOException, JsonReadException {
        try {
            JsonParser parser = JSON_FACTORY.createParser(utf8Body);
            return readFully(parser);
        } catch (JsonParseException ex) {
            throw JsonReadException.fromJackson(ex);
        }
    }

    public T readFully(String body) throws JsonReadException {
        try {
            JsonParser parser = JSON_FACTORY.createParser(body);
            try {
                return this.readFully(parser);
            } finally {
                parser.close();
            }
        } catch (JsonParseException ex) {
            throw JsonReadException.fromJackson(ex);
        } catch (IOException ex) {
            AssertionError ae = new AssertionError("Got IOException reading from String");
            ae.initCause(ae);
            throw ae;
        }
    }

    public T readFully(byte[] utf8Body) throws JsonReadException {
        try {
            JsonParser parser = JSON_FACTORY.createParser(utf8Body);
            try {
                return this.readFully(parser);
            } finally {
                parser.close();
            }
        } catch (JsonParseException ex) {
            throw JsonReadException.fromJackson(ex);
        } catch (IOException ex) {
            AssertionError ae = new AssertionError("Got IOException reading from byte[]");
            ae.initCause(ae);
            throw ae;
        }
    }

    public T readFromFile(String filePath) throws FileLoadException {
        return this.readFromFile(new File(filePath));
    }

    public T readFromFile(File file) throws FileLoadException {
        try {
            InputStream in = new FileInputStream(file);
            try {
                return this.readFully(in);
            } finally {
                IOUtil.closeInput(in);
            }
        } catch (JsonReadException ex) {
            throw new FileLoadException.JsonError(file, ex);
        } catch (IOException ex) {
            throw new FileLoadException.IOError(file, ex);
        }
    }

    public static abstract class FileLoadException extends Exception {
        private static final long serialVersionUID = 1L;

        protected FileLoadException(String message) {
            super(message);
        }

        public static final class IOError extends FileLoadException {
            private static final long serialVersionUID = 1L;

            private IOException reason = null;

            public IOError(File file, IOException reason) {
                super("unable to read file \"" + file.getPath() + "\": " + reason.getMessage());
                this.reason = reason;
            }

            public IOException getReason() {
                return this.reason;
            }
        }

        public static final class JsonError extends FileLoadException {
            private static final long serialVersionUID = 1L;

            private JsonReadException reason = null;

            public JsonError(File file, JsonReadException reason) {
                super(file.getPath() + ": " + reason.getMessage());
                this.reason = reason;
            }

            public JsonReadException getReason() {
                return this.reason;
            }
        }
    }

    public T readFully(JsonParser parser) throws IOException, JsonReadException {
        parser.nextToken();
        T value = this.read(parser);
        if (parser.getCurrentToken() != null) {
            throw new AssertionError("The JSON library should ensure there's no tokens after the main value: "
                    + parser.getCurrentToken() + "@" + parser.getCurrentLocation());
        }
        return value;
    }
}
