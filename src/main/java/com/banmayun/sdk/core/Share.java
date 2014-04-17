package com.banmayun.sdk.core;

import java.io.IOException;
import java.util.ArrayList;

import javax.jws.soap.SOAPBinding.Use;

import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.DumpWriter;
import com.banmayun.sdk.util.Dumpable;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class Share extends Dumpable {

    public String id;
    public String rootId;
    public String metaId;
    public Time expiresAt;
    public Time createdAt;
    public User createdBy;
    public Meta[] meta;

    public Share(String id, String rootId, String metaId, Time expiresAt, Time createdAt, User createdBy, Meta[] meta) {
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
        out.field("id", id);
        out.field("root_id", rootId);
        out.field("meta_id", metaId);
        out.field("expires_at", expiresAt);
        out.field("created_at", createdAt);
        out.field("created_by", createdBy);
        // TODO: not ok
        // out.field("meta", meta);
    }

    public static JsonReader<Share> Reader = new JsonReader<Share>() {

        @Override
        public Share read(JsonParser parser) throws IOException, JsonReadException {

            String id = null;
            String rootId = null;
            String metaId = null;
            Time expiresAt = null;
            Time createdAt = null;
            User createdBy = null;
            Meta[] meta = null;

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
                    case FM_root_id:
                        rootId = JsonReader.StringReader.readField(parser, fieldName, rootId);
                        break;
                    case FM_meta_id:
                        metaId = JsonReader.StringReader.readField(parser, fieldName, metaId);
                        break;
                    case FM_expires_at:
                        expiresAt = Time.Reader.readField(parser, fieldName, expiresAt);
                        break;
                    case FM_created_at:
                        createdAt = Time.Reader.readField(parser, fieldName, createdAt);
                        break;
                    case FM_created_by:
                        createdBy = User.Reader.readField(parser, fieldName, createdBy);
                        break;
                    case FM_meta:
                        JsonReader.expectArrayStart(parser);
                        ArrayList<Meta> metaList = new ArrayList<>();
                        while (!JsonReader.isArrayEnd(parser)) {
                            Meta temp;
                            temp = Meta.Reader.read(parser);
                            metaList.add(temp);
                        }
                        parser.nextToken();
                        if (metaList.size() > 0) {
                            meta = new Meta[metaList.size()];
                            for (int i = 0; i < metaList.size(); i++) {
                                meta[i] = metaList.get(i);
                            }
                        } else {
                            meta = null;
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

    public void print() {
        System.out.println(this.id + " " + this.metaId + " " + this.rootId);
    }
}