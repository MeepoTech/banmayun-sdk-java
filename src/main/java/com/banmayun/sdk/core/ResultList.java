package com.banmayun.sdk.core;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.DumpWriter;
import com.banmayun.sdk.util.Dumpable;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class ResultList<T extends Dumpable> extends Dumpable {

    public Integer total = null;
    public Integer offset = null;
    public List<T> entries = null;

    public ResultList() {
    }

    public ResultList(Integer total, Integer offset, List<T> entries) {
        this.total = total;
        this.offset = offset;
        this.entries = entries;
    }

    @Override
    protected void dumpFields(DumpWriter out) {
        out.field("total", this.total);
        out.field("offset", this.offset);
    }

    public static class reader<T extends Dumpable> extends JsonReader<ResultList<T>> {

        private JsonReader<T> metadataReader = null;

        public reader(JsonReader<T> metadataReader) {
            this.metadataReader = metadataReader;
        }

        @Override
        public ResultList<T> read(JsonParser parser) throws IOException, JsonReadException {
            return read(parser, metadataReader);
        }

        public static <T extends Dumpable> ResultList<T> read(JsonParser parser, JsonReader<T> entryReader)
                throws IOException, JsonReadException {
            Integer total = null;
            Integer offset = null;
            List<T> entries = null;

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
                    case FM_total:
                        total = (int) JsonReader.readUnsignedLong(parser);
                        break;
                    case FM_offset:
                        offset = (int) JsonReader.readUnsignedLong(parser);
                        break;
                    case FM_entries:
                        JsonReader.expectArrayStart(parser);
                        List<T> entryList = new LinkedList<>();
                        while (!JsonReader.isArrayEnd(parser)) {
                            T entry;
                            entry = entryReader.read(parser);
                            entryList.add(entry);
                        }
                        parser.nextToken();
                        if (entryList.size() > 0) {
                            entries = entryList;
                        } else {
                            entries = new LinkedList<>();
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
