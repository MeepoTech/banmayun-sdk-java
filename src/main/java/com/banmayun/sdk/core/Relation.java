package com.banmayun.sdk.core;

import java.io.IOException;

import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.DumpWriter;
import com.banmayun.sdk.util.Dumpable;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class Relation extends Dumpable {

    public RelationRole role;
    public boolean isActivated;
    public boolean isBlocked;
    public String remark;
    public Time createdAt;

    public Relation() {
        this.role = null;
        this.isActivated = false;
        this.isBlocked = false;
        this.remark = null;
        this.createdAt = null;
    }

    public Relation(RelationRole role, boolean isActivated, boolean isBlocked, String remark, Time createdAt) {
        this.role = role;
        this.isActivated = isActivated;
        this.isBlocked = isBlocked;
        this.remark = remark;
        this.createdAt = createdAt;
    }

    @Override
    protected void dumpFields(DumpWriter out) {
        // TODO Auto-generated method stub
        out.field("role", role);
        out.field("is_activated", isActivated);
        out.field("is_blocked", isBlocked);
        out.field("remark", remark);
        out.field("created_at", createdAt);
    }

    public static JsonReader<Relation> Reader = new JsonReader<Relation>() {

        @Override
        public Relation read(JsonParser parser) throws IOException, JsonReadException {

            RelationRole role = null;
            boolean isActivated = false;
            boolean isBlocked = false;
            String remark = null;
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
                    case FM_role:
                        role = RelationRole.Reader.readField(parser, fieldName, role);
                        break;
                    case FM_is_activated:
                        isActivated = JsonReader.readBoolean(parser);
                        break;
                    case FM_is_blocked:
                        isBlocked = JsonReader.readBoolean(parser);
                        break;
                    case FM_remark:
                        remark = JsonReader.StringReader.readField(parser, fieldName, remark);
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
            return new Relation(role, isActivated, isBlocked, remark, createdAt);
        }
    };

    private static final int FM_role = 0;
    private static final int FM_is_activated = 1;
    private static final int FM_is_blocked = 2;
    private static final int FM_remark = 3;
    private static final int FM_created_at = 4;

    private static final JsonReader.FieldMapping FM;

    static {
        JsonReader.FieldMapping.Builder b = new JsonReader.FieldMapping.Builder();
        b.add("role", FM_role);
        b.add("is_activated", FM_is_activated);
        b.add("is_blocked", FM_is_blocked);
        b.add("remark", FM_remark);
        b.add("created_at", FM_created_at);

        FM = b.build();
    }
}