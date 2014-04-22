package com.banmayun.sdk.core;

import java.io.IOException;
import java.util.ArrayList;

import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.DumpWriter;
import com.banmayun.sdk.util.Dumpable;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class Delta extends Dumpable {

    public static class Entry extends Dumpable {
        public String path = null;
        public Meta meta = null;

        public Entry() {
        }

        public Entry(String path, Meta meta) {
            this.path = path;
            this.meta = meta;
        }

        @Override
        protected void dumpFields(DumpWriter out) {
            out.field("path", this.path);
            out.field("meta", this.meta);
        }

        public static JsonReader<Entry> reader = new JsonReader<Entry>() {
            @Override
            public Entry read(JsonParser parser) throws IOException, JsonReadException {
                String path = null;
                Meta meta = null;

                JsonReader.expectObjectStart(parser);
                while (parser.getCurrentToken() == JsonToken.FIELD_NAME) {
                    String fieldName = parser.getCurrentName();
                    parser.nextToken();
                    try {
                        int fi = FM.get(fieldName);
                        switch (fi) {
                        case -1:
                            JsonReader.skipValue(parser);
                            break;
                        case FM_path:
                            path = JsonReader.STRING_READER.readField(parser, fieldName, path);
                            break;
                        case FM_meta:
                            meta = Meta.reader.readField(parser, fieldName, meta);
                            break;
                        default:
                            throw new AssertionError("bad index: " + fi + ", field = \"" + fieldName + "\"");
                        }
                    } catch (JsonReadException ex) {
                        throw ex.addFieldContext(fieldName);
                    }
                }
                JsonReader.expectObjectEnd(parser);

                return new Entry(path, meta);
            }
        };

        private static final int FM_path = 0;
        private static final int FM_meta = 1;

        private static final JsonReader.FieldMapping FM;

        static {
            JsonReader.FieldMapping.Builder b = new JsonReader.FieldMapping.Builder();
            b.add("path", FM_path);
            b.add("meta", FM_meta);

            FM = b.build();
        }
    }

    public String cursorId = null;
    public Boolean reset = null;
    public Entry[] entries = null;

    public Delta() {
    }

    public Delta(String cursorId, boolean reset, Entry[] entries) {
        this.cursorId = cursorId;
        this.reset = reset;
        this.entries = entries;
    }

    @Override
    protected void dumpFields(DumpWriter out) {
        out.field("cursor_id", this.cursorId);
        out.field("reset", this.reset);
    }

    public static JsonReader<Delta> Reader = new JsonReader<Delta>() {
        @Override
        public Delta read(JsonParser parser) throws IOException, JsonReadException {
            String cursorId = null;
            boolean reset = false;
            Entry[] entries = null;

            JsonReader.expectObjectStart(parser);
            while (parser.getCurrentToken() == JsonToken.FIELD_NAME) {
                String fieldName = parser.getCurrentName();
                parser.nextToken();
                try {
                    int fi = FM.get(fieldName);
                    switch (fi) {
                    case -1:
                        JsonReader.skipValue(parser);
                        break;
                    case FM_cursor_id:
                        cursorId = JsonReader.STRING_READER.readField(parser, fieldName, cursorId);
                        break;
                    case FM_reset:
                        reset = JsonReader.readBoolean(parser);
                    case FM_entries:
                        JsonReader.expectArrayStart(parser);
                        ArrayList<Entry> entryList = new ArrayList<>();
                        while (!JsonReader.isArrayEnd(parser)) {
                            Entry entry;
                            entry = Entry.reader.read(parser);
                            entryList.add(entry);
                        }
                        parser.nextToken();
                        if (entryList.size() > 0) {
                            entries = new Entry[entryList.size()];
                            for (int i = 0; i < entryList.size(); i++) {
                                entries[i] = entryList.get(i);
                            }
                        } else {
                            entries = null;
                        }
                        break;
                    default:
                        throw new AssertionError("bad index: " + fi + ", field = \"" + fieldName + "\"");
                    }
                } catch (JsonReadException ex) {
                    throw ex.addFieldContext(fieldName);
                }
            }
            JsonReader.expectObjectEnd(parser);

            return new Delta(cursorId, reset, entries);
        }
    };

    private static final int FM_cursor_id = 0;
    private static final int FM_reset = 1;
    private static final int FM_entries = 2;

    private static final JsonReader.FieldMapping FM;

    static {
        JsonReader.FieldMapping.Builder b = new JsonReader.FieldMapping.Builder();
        b.add("cursor_id", FM_cursor_id);
        b.add("reset", FM_reset);
        b.add("entries", FM_entries);

        FM = b.build();
    }
}
