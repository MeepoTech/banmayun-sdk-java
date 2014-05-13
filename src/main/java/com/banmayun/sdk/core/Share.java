package com.banmayun.sdk.core;

import java.io.IOException;

import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.DumpWriter;
import com.banmayun.sdk.util.Dumpable;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class Share extends Dumpable {

    public String id = null;
    public String rootId = null;
    public String metaId = null;
    public Time expiresAt = null;
    public Time createdAt = null;
    public User createdBy = null;
    public Meta meta = null;

    public Share() {
    }

    public Share(String id, String rootId, String metaId, Time expiresAt, Time createdAt, User createdBy, Meta meta) {
        this.id = id;
        this.rootId = rootId;
        this.metaId = metaId;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.meta = meta;
    }

    @Override
    protected void dumpFields(DumpWriter out) {
        out.field("id", this.id);
        out.field("root_id", this.rootId);
        out.field("meta_id", this.metaId);
        out.field("expires_at", this.expiresAt);
        out.field("created_at", this.createdAt);
        out.field("created_by", this.createdBy);
    }

    public static JsonReader<Share> reader = new JsonReader<Share>() {

        @Override
        public Share read(JsonParser parser) throws IOException, JsonReadException {
            String id = null;
            String rootId = null;
            String metaId = null;
            Time expiresAt = null;
            Time createdAt = null;
            User createdBy = null;
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
                    case FM_id:
                        id = JsonReader.STRING_READER.readField(parser, fieldName, id);
                        break;
                    case FM_root_id:
                        rootId = JsonReader.STRING_READER.readField(parser, fieldName, rootId);
                        break;
                    case FM_meta_id:
                        metaId = JsonReader.STRING_READER.readField(parser, fieldName, metaId);
                        break;
                    case FM_expires_at:
                        expiresAt = Time.reader.readField(parser, fieldName, expiresAt);
                        break;
                    case FM_created_at:
                        createdAt = Time.reader.readField(parser, fieldName, createdAt);
                        break;
                    case FM_created_by:
                        createdBy = User.reader.readField(parser, fieldName, createdBy);
                        break;
                    case FM_meta:
                        JsonReader.expectArrayStart(parser);
                        meta = Meta.reader.read(parser);
                        break;
                    default:
                        throw new AssertionError("bad index: " + fi + ", field = \"" + fieldName + "\"");
                    }
                } catch (JsonReadException ex) {
                    throw ex.addFieldContext(fieldName);
                }
            }
            JsonReader.expectObjectEnd(parser);

            return new Share(id, rootId, metaId, expiresAt, createdAt, createdBy, meta);
        }
    };

    private static final int FM_id = 0;
    private static final int FM_root_id = 1;
    private static final int FM_meta_id = 2;
    private static final int FM_expires_at = 3;
    private static final int FM_created_at = 4;
    private static final int FM_created_by = 5;
    private static final int FM_meta = 6;

    private static final JsonReader.FieldMapping FM;

    static {
        JsonReader.FieldMapping.Builder b = new JsonReader.FieldMapping.Builder();
        b.add("id", FM_id);
        b.add("root_id", FM_root_id);
        b.add("meta_id", FM_meta_id);
        b.add("expires_at", FM_expires_at);
        b.add("created_at", FM_created_at);
        b.add("created_by", FM_created_by);
        b.add("meta", FM_meta);

        FM = b.build();
    }
}
