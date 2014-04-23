package com.banmayun.sdk.core;

import java.io.IOException;

import com.banmayun.sdk.json.JsonBuilder;
import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.DumpWriter;
import com.banmayun.sdk.util.Dumpable;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class Relation extends Dumpable {

    public EnumElement role = null;
    public Boolean isActivated = null;
    public Boolean isBlocked = null;
    public String remarks = null;
    public Time createdAt = null;

    public Relation() {
    }

    public Relation(EnumElement role, Boolean isActivated, Boolean isBlocked, String remarks, Time createdAt) {
        this.role = role;
        this.isActivated = isActivated;
        this.isBlocked = isBlocked;
        this.remarks = remarks;
        this.createdAt = createdAt;
    }

    @Override
    protected void dumpFields(DumpWriter out) {
        out.field("role", this.role);
        out.field("is_activated", this.isActivated);
        out.field("is_blocked", this.isBlocked);
        out.field("remarks", this.remarks);
        out.field("created_at", this.createdAt);
    }

    public static JsonReader<Relation> reader = new JsonReader<Relation>() {
        @Override
        public Relation read(JsonParser parser) throws IOException, JsonReadException {
            EnumElement role = null;
            Boolean isActivated = null;
            Boolean isBlocked = null;
            String remark = null;
            Time createdAt = null;

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
                    case FM_role:
                        role = EnumElement.reader.readField(parser, fieldName, role);
                        break;
                    case FM_is_activated:
                        isActivated = JsonReader.readBoolean(parser);
                        break;
                    case FM_is_blocked:
                        isBlocked = JsonReader.readBoolean(parser);
                        break;
                    case FM_remarks:
                        remark = JsonReader.STRING_READER.readField(parser, fieldName, remark);
                        break;
                    case FM_created_at:
                        createdAt = Time.reader.readField(parser, fieldName, createdAt);
                        break;
                    default:
                        throw new AssertionError("bad index: " + fi + ", field = \"" + fieldName + "\"");
                    }
                } catch (JsonReadException ex) {
                    throw ex.addFieldContext(fieldName);
                }
            }
            JsonReader.expectObjectEnd(parser);

            return new Relation(role, isActivated, isBlocked, remark, createdAt);
        }
    };

    private static final int FM_role = 0;
    private static final int FM_is_activated = 1;
    private static final int FM_is_blocked = 2;
    private static final int FM_remarks = 3;
    private static final int FM_created_at = 4;

    private static final JsonReader.FieldMapping FM;

    static {
        JsonReader.FieldMapping.Builder b = new JsonReader.FieldMapping.Builder();
        b.add("role", FM_role);
        b.add("is_activated", FM_is_activated);
        b.add("is_blocked", FM_is_blocked);
        b.add("remarks", FM_remarks);
        b.add("created_at", FM_created_at);

        FM = b.build();
    }
    
    public String objectToJsonString() {
        JsonBuilder jb = new JsonBuilder();
        if (role != null) {
            jb.addEnumElement("role", role);
        }
        if (isActivated != null) {
            jb.addBoolean("is_activated", isActivated);
        }
        if (isBlocked != null) {
            jb.addBoolean("is_blocked", isBlocked);
        }
        return jb.makeJsonString();
    }
}
