package com.banmayun.sdk.core;

import java.io.IOException;

import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.DumpWriter;
import com.banmayun.sdk.util.Dumpable;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class ChunkedUpload extends Dumpable {

    public String id;
    public long offset;
    public Size size;
    public Time expiresAt;
    public Time createdAt;

    public ChunkedUpload(String id, long offset, Size size, Time expiresAt, Time createdAt) {
        this.id = id;
        this.offset = offset;
        this.size = size;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    @Override
    protected void dumpFields(DumpWriter out) {
        // TODO Auto-generated method stub
        out.field("id", id);
        out.field("offset", offset);
        out.field("size", size);
        out.field("expires_at", expiresAt);
        out.field("created_at", createdAt);
    }

    public static JsonReader<ChunkedUpload> Reader = new JsonReader<ChunkedUpload>() {

        @Override
        public ChunkedUpload read(JsonParser parser) throws IOException, JsonReadException {

            String id = null;
            long offset = -1;
            Size size = null;
            Time expiresAt = null;
            Time createdAt = null;

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
                    case FM_id:
                        id = JsonReader.StringReader.readField(parser, fieldName, id);
                        break;
                    case FM_offset:
                        offset = JsonReader.readUnsignedLongField(parser, fieldName, offset);
                        break;
                    case FM_size:
                        size = Size.Reader.readField(parser, fieldName, size);
                        break;
                    case FM_expires_at:
                        expiresAt = Time.Reader.readField(parser, fieldName, expiresAt);
                        break;
                    case FM_created_at:
                        createdAt = Time.Reader.readField(parser, fieldName, createdAt);
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
            return new ChunkedUpload(id, offset, size, expiresAt, createdAt);
        }
    };

    private static final int FM_id = 0;
    private static final int FM_offset = 1;
    private static final int FM_size = 2;
    private static final int FM_expires_at = 3;
    private static final int FM_created_at = 4;

    private static final JsonReader.FieldMapping FM;

    static {
        JsonReader.FieldMapping.Builder b = new JsonReader.FieldMapping.Builder();
        b.add("id", FM_id);
        b.add("offset", FM_offset);
        b.add("size", FM_size);
        b.add("expires_at", FM_expires_at);
        b.add("created_at", FM_created_at);

        FM = b.build();
    }
}