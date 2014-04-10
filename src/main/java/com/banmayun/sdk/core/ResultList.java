package com.banmayun.sdk.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.DumpWriter;
import com.banmayun.sdk.util.Dumpable;
import com.banmayun.sdk.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class ResultList<T extends Dumpable> extends Dumpable {

    public int total;
    public int offset;
    public List<T> entries;

    public ResultList(int total, int offset, List<T> entries) {
        this.total = total;
        this.offset = offset;
        this.entries = entries;
    }

    @Override
    protected void dumpFields(DumpWriter out) {
        out.field("total", total);
        out.field("offset", offset);
        // not ok
        // out.field("entries", entries);
    }

    public static class Reader<T extends Dumpable> extends JsonReader<ResultList<T>> {
        public JsonReader<T> metadataReader;

        public Reader(JsonReader<T> metadataReader) {
            this.metadataReader = metadataReader;
        }

        public ResultList<T> read(JsonParser parser) throws IOException, JsonReadException {
            return read(parser, metadataReader);
        }

        public static <T extends Dumpable> ResultList<T> read(JsonParser parser, JsonReader<T> metadataReader)
                throws IOException, JsonReadException {

            int total = -1;
            int offset = -1;
            List<T> entries = null;

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
                    case FM_total:
                        total = (int) JsonReader.readUnsignedLongField(parser, fieldName, total);
                        break;
                    case FM_offset:
                        offset = (int) JsonReader.readUnsignedLongField(parser, fieldName, offset);
                        break;
                    case FM_entries:
                        JsonReader.expectArrayStart(parser);
                        ArrayList<T> metaList = new ArrayList<>();
                        while (!JsonReader.isArrayEnd(parser)) {
                            T meta;
                            meta = metadataReader.read(parser);
                            metaList.add(meta);
                        }
                        parser.nextToken();
                        if (metaList.size() > 0) {
                            entries = new ArrayList<>();
                            entries = metaList;
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

            return new ResultList<T>(total, offset, entries);
        }

        private static final int FM_total = 0;
        private static final int FM_offset = 1;
        private static final int FM_entries = 2;

        private static final JsonReader.FieldMapping FM;

        static {
            JsonReader.FieldMapping.Builder b = new JsonReader.FieldMapping.Builder();
            b.add("total", FM_total);
            b.add("offset", FM_offset);
            b.add("entries", FM_entries);

            FM = b.build();
        }
    }
}