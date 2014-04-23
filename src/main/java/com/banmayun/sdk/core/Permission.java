package com.banmayun.sdk.core;

import java.io.IOException;

import com.banmayun.sdk.json.JsonBuilder;
import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.DumpWriter;
import com.banmayun.sdk.util.Dumpable;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class Permission extends Dumpable {

    public Boolean insertableToOwner = null;
    public Boolean readableToOwner = null;
    public Boolean writableToOwner = null;
    public Boolean deletableToOwner = null;
    public Boolean insertableToOthers = null;
    public Boolean readableToOthers = null;
    public Boolean writableToOthers = null;
    public Boolean deletableToOthers = null;

    public Permission() {
    }

    public Permission(Boolean insertableToOwner, Boolean readableToOwner, Boolean writableToOwner,
            Boolean deletableToOwner, Boolean insertableToOthers, Boolean readableToOthers, Boolean writableToOthers,
            Boolean deletableToOthers) {
        this.insertableToOwner = insertableToOwner;
        this.readableToOwner = readableToOwner;
        this.writableToOwner = writableToOwner;
        this.deletableToOwner = deletableToOwner;
        this.insertableToOthers = insertableToOthers;
        this.readableToOthers = readableToOthers;
        this.writableToOthers = writableToOthers;
        this.deletableToOthers = deletableToOthers;
    }

    @Override
    protected void dumpFields(DumpWriter out) {
        out.field("insertable_to_owner", this.insertableToOwner);
        out.field("readable_to_owner", this.readableToOwner);
        out.field("writable_to_owner", this.writableToOwner);
        out.field("deletable_to_owner", this.deletableToOwner);
        out.field("insertable_to_others", this.insertableToOthers);
        out.field("readable_to_others", this.readableToOthers);
        out.field("writable_to_others", this.writableToOthers);
        out.field("deletable_to_others", this.deletableToOthers);
    }

    public static JsonReader<Permission> reader = new JsonReader<Permission>() {
        @Override
        public Permission read(JsonParser parser) throws IOException, JsonReadException {
            Boolean insertableToOwner = true;
            Boolean readableToOwner = true;
            Boolean writableToOwner = true;
            Boolean deletableToOwner = true;
            Boolean insertableToOthers = true;
            Boolean readableToOthers = true;
            Boolean writableToOthers = true;
            Boolean deletableToOthers = true;

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
                    case FM_insertable_to_owner:
                        insertableToOwner = JsonReader.readBoolean(parser);
                        break;
                    case FM_readable_to_owner:
                        readableToOwner = JsonReader.readBoolean(parser);
                        break;
                    case FM_writable_to_owner:
                        writableToOwner = JsonReader.readBoolean(parser);
                        break;
                    case FM_deletable_to_owner:
                        deletableToOwner = JsonReader.readBoolean(parser);
                        break;
                    case FM_insertable_to_others:
                        insertableToOthers = JsonReader.readBoolean(parser);
                        break;
                    case FM_readable_to_others:
                        readableToOthers = JsonReader.readBoolean(parser);
                        break;
                    case FM_writable_to_others:
                        writableToOthers = JsonReader.readBoolean(parser);
                        break;
                    case FM_deletable_to_others:
                        deletableToOthers = JsonReader.readBoolean(parser);
                        break;
                    default:
                        throw new AssertionError("bad index: " + fi + ", field = \"" + fieldName + "\"");
                    }
                } catch (JsonReadException ex) {
                    throw ex.addFieldContext(fieldName);
                }
            }
            JsonReader.expectObjectEnd(parser);

            return new Permission(insertableToOwner, readableToOwner, writableToOwner, deletableToOwner,
                    insertableToOthers, readableToOthers, writableToOthers, deletableToOthers);
        }
    };

    private static final int FM_insertable_to_owner = 0;
    private static final int FM_readable_to_owner = 1;
    private static final int FM_writable_to_owner = 2;
    private static final int FM_deletable_to_owner = 3;
    private static final int FM_insertable_to_others = 4;
    private static final int FM_readable_to_others = 5;
    private static final int FM_writable_to_others = 6;
    private static final int FM_deletable_to_others = 7;

    private static final JsonReader.FieldMapping FM;

    static {
        JsonReader.FieldMapping.Builder b = new JsonReader.FieldMapping.Builder();
        b.add("insertable_to_owner", FM_insertable_to_owner);
        b.add("readable_to_owner", FM_readable_to_owner);
        b.add("writable_to_owner", FM_writable_to_owner);
        b.add("deletable_to_owner", FM_deletable_to_owner);
        b.add("insertable_to_others", FM_insertable_to_others);
        b.add("readable_to_others", FM_readable_to_others);
        b.add("writable_to_others", FM_writable_to_others);
        b.add("deletable_to_others", FM_deletable_to_others);

        FM = b.build();
    }

    public String objectToJsonObject() {
        JsonBuilder jb = new JsonBuilder();
        if (insertableToOwner != null) {
            jb.addBoolean("insertable_to_owner", insertableToOwner);
        }
        if (readableToOwner != null) {
            jb.addBoolean("readable_to_owner", readableToOwner);
        }
        if (writableToOwner != null) {
            jb.addBoolean("writable_to_owner", writableToOwner);
        }
        if (deletableToOwner != null) {
            jb.addBoolean("deletable_to_owner", deletableToOwner);
        }
        if (insertableToOthers != null) {
            jb.addBoolean("insertable_to_others", insertableToOthers);
        }
        if (readableToOthers != null) {
            jb.addBoolean("readable_to_others", readableToOthers);
        }
        if (writableToOthers != null) {
            jb.addBoolean("writable_to_others", writableToOthers);
        }
        if (deletableToOthers != null) {
            jb.addBoolean("deletable_to_others", deletableToOthers);
        }
        
        return jb.makeJsonString();
        
    }
}
