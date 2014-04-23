package com.banmayun.sdk.core;

import java.io.IOException;

import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.DumpWriter;
import com.banmayun.sdk.util.Dumpable;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class Link extends Dumpable {

    public String id = null;
    public String userId = null;
    public String name = null;
    public String device = null;
    public String token = null;
    public Time expiresAt = null;
    public Time createdAt = null;
    public Boolean isCurrent = null;

    public Link() {
    }

    public Link(String id, String userId, String name, String device, String token, Time expiresAt, Time createdAt,
            Boolean isCurrent) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.device = device;
        this.token = token;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.isCurrent = isCurrent;
    }

    @Override
    protected void dumpFields(DumpWriter out) {
        out.field("id", this.id);
        out.field("user_id", this.userId);
        out.field("name", this.name);
        out.field("device", this.device);
        out.field("token", this.token);
        out.field("expires_at", this.expiresAt);
        out.field("created_at", this.createdAt);
        out.field("is_current", this.isCurrent);
    }

    public static JsonReader<Link> reader = new JsonReader<Link>() {
        @Override
        public Link read(JsonParser parser) throws IOException, JsonReadException {
            String id = null;
            String userId = null;
            String name = null;
            String device = null;
            String token = null;
            Time expiresAt = null;
            Time createdAt = null;
            Boolean isCurrent = null;

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
                    case FM_user_id:
                        userId = JsonReader.STRING_READER.readField(parser, fieldName, userId);
                        break;
                    case FM_name:
                        name = JsonReader.STRING_READER.readField(parser, fieldName, name);
                        break;
                    case FM_device:
                        device = JsonReader.STRING_READER.readField(parser, fieldName, device);
                        break;
                    case FM_token:
                        token = JsonReader.STRING_READER.readField(parser, fieldName, token);
                        break;
                    case FM_expires_at:
                        expiresAt = Time.reader.readField(parser, fieldName, expiresAt);
                        break;
                    case FM_created_at:
                        createdAt = Time.reader.readField(parser, fieldName, createdAt);
                        break;
                    case FM_is_current:
                        isCurrent = JsonReader.readBoolean(parser);
                        break;
                    default:
                        throw new AssertionError("bad index: " + fi + ", field = \"" + fieldName + "\"");
                    }
                } catch (JsonReadException ex) {
                    throw ex.addFieldContext(fieldName);
                }
            }
            JsonReader.expectObjectEnd(parser);

            return new Link(id, userId, name, device, token, expiresAt, createdAt, isCurrent);
        }
    };

    private static final int FM_id = 0;
    private static final int FM_user_id = 1;
    private static final int FM_name = 2;
    private static final int FM_device = 3;
    private static final int FM_token = 4;
    private static final int FM_expires_at = 5;
    private static final int FM_created_at = 6;
    private static final int FM_is_current = 7;

    private static final JsonReader.FieldMapping FM;

    static {
        JsonReader.FieldMapping.Builder b = new JsonReader.FieldMapping.Builder();
        b.add("id", FM_id);
        b.add("user_id", FM_user_id);
        b.add("name", FM_name);
        b.add("device", FM_device);
        b.add("token", FM_token);
        b.add("expires_at", FM_expires_at);
        b.add("created_at", FM_created_at);
        b.add("is_current", FM_is_current);

        FM = b.build();
    }
}
