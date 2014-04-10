package com.banmayun.sdk.core;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.io.IOException;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.RestoreAction;

import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.DumpWriter;
import com.banmayun.sdk.util.Dumpable;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class Delta extends Dumpable {

    public static class Entry extends Dumpable {

        public String path;
        public Meta meta;

        public Entry(String path, Meta meta) {
            this.path = path;
            this.meta = meta;
        }

        @Override
        protected void dumpFields(DumpWriter out) {
            // TODO Auto-generated method stub
            out.field("path", path);
            out.field("meta", meta);
        }

        public static JsonReader<Entry> Reader = new JsonReader<Entry>() {

            @Override
            public Entry read(JsonParser parser) throws IOException, JsonReadException {

                String path = null;
                Meta meta = null;

                JsonLocation top = JsonReader.expectObjectStart(parser);
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
                            path = JsonReader.StringReader.readField(parser, fieldName, path);
                            break;
                        case FM_meta:
                            meta = Meta.Reader.readField(parser, fieldName, meta);
                            break;
                        default:
                            throw new AssertionError("bad index: " + fi + ", field = \"" + fieldName + "\"");
                        }
                    } catch (JsonReadException ex) {
                        throw ex.addFieldContext(fieldName);
                    }
                }
                JsonReader.expectObjectEnd(parser);

                // TODO: add some checks?
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

    public String cursorId;
    public boolean reset;
    public Entry[] entries;

    public Delta(String cursorId, boolean reset, Entry[] entries) {
        this.cursorId = cursorId;
        this.reset = reset;
        this.entries = entries;
    }

    @Override
    protected void dumpFields(DumpWriter out) {
        // TODO Auto-generated method stub
        out.field("cursor_id", cursorId);
        out.field("reset", reset);
        //not ok
        //out.field("entried", entries);
    }

    public static JsonReader<Delta> Reader = new JsonReader<Delta>() {

        @Override
        public Delta read(JsonParser parser) throws IOException, JsonReadException {

            String cursorId = null;
            boolean reset = false;
            Entry[] entries = null;

            JsonLocation top = JsonReader.expectObjectStart(parser);
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
                        cursorId = JsonReader.StringReader.readField(parser, fieldName, cursorId);
                        break;
                    case FM_reset:
                        reset = JsonReader.readBoolean(parser);
                    case FM_entries:
                        JsonReader.expectArrayStart(parser);
                        ArrayList<Entry> entryList = new ArrayList<>();
                        while (!JsonReader.isArrayEnd(parser)) {
                            Entry entry;
                            entry = Entry.Reader.read(parser);
                            entryList.add(entry);
                        }
                        parser.nextToken();
                        if (entryList.size() > 0) {
                            entries = new Entry[entryList.size()];
                            for (int i = 0; i < entryList.size(); i ++) {
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

            // TODO: add some checks?
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

/*
 * @JsonInclude(Include.NON_NULL) public class Delta {
 * 
 * @JsonProperty("cursor_id") public String cursorId = null;
 * 
 * @JsonProperty("reset") public Boolean reset = null;
 * 
 * @JsonProperty("entries") public List<Entry> entries = null;
 * 
 * @JsonInclude(Include.NON_NULL) public static class Entry {
 * 
 * @JsonProperty("path") public String path = null;
 * 
 * @JsonProperty("meta") public Meta meta = null; } }
 */
