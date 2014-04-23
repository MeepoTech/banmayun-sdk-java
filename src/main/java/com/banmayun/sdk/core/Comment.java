package com.banmayun.sdk.core;

import java.io.IOException;

import com.banmayun.sdk.json.JsonBuilder;
import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.DumpWriter;
import com.banmayun.sdk.util.Dumpable;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class Comment extends Dumpable {

    public String id = null;
    public String rootId = null;
    public String metaId = null;
    public String contents = null;
    public Time createdAt = null;
    public User createdBy = null;

    public Comment() {
    }

    public Comment(String id, String rootId, String metaId, String contents, Time createdAt, User createdBy) {
        this.id = id;
        this.rootId = rootId;
        this.metaId = metaId;
        this.contents = contents;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }

    @Override
    protected void dumpFields(DumpWriter out) {
        out.field("id", this.id);
        out.field("root_id", this.rootId);
        out.field("meta_id", this.metaId);
        out.field("contents", this.contents);
        out.field("created_at", this.createdAt);
        out.field("created_by", this.createdBy);
    }

    public static JsonReader<Comment> reader = new JsonReader<Comment>() {
        @Override
        public Comment read(JsonParser parser) throws IOException, JsonReadException {
            String id = null;
            String rootId = null;
            String metaId = null;
            String contents = null;
            Time createdAt = null;
            User createdBy = null;

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
                    case FM_contents:
                        contents = JsonReader.STRING_READER.readField(parser, fieldName, contents);
                        break;
                    case FM_created_at:
                        createdAt = Time.reader.readField(parser, fieldName, createdAt);
                        break;
                    case FM_created_by:
                        createdBy = User.reader.readField(parser, fieldName, createdBy);
                        break;
                    default:
                        throw new AssertionError("bad index: " + fi + ", field = \"" + fieldName + "\"");
                    }
                } catch (JsonReadException ex) {
                    throw ex.addFieldContext(fieldName);
                }
            }
            JsonReader.expectObjectEnd(parser);

            return new Comment(id, rootId, metaId, contents, createdAt, createdBy);
        }
    };

    private static final int FM_id = 0;
    private static final int FM_root_id = 1;
    private static final int FM_meta_id = 2;
    private static final int FM_contents = 3;
    private static final int FM_created_at = 4;
    private static final int FM_created_by = 5;

    private static final JsonReader.FieldMapping FM;

    static {
        JsonReader.FieldMapping.Builder b = new JsonReader.FieldMapping.Builder();
        b.add("id", FM_id);
        b.add("root_id", FM_root_id);
        b.add("meta_id", FM_meta_id);
        b.add("contents", FM_contents);
        b.add("created_at", FM_created_at);
        b.add("created_by", FM_created_by);

        FM = b.build();
    }
    
    public String objectToJsonString() {
        JsonBuilder jb = new JsonBuilder();
        if (contents != null) {
            jb.addString("contents", contents);
        }
        return jb.makeJsonString();
    }
}
