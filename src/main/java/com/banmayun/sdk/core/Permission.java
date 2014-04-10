package com.banmayun.sdk.core;

import java.io.IOException;

import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.DumpWriter;
import com.banmayun.sdk.util.Dumpable;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class Permission extends Dumpable {

    public boolean insertableToOwner;
    public boolean readableToOwner;
    public boolean writableToOwner;
    public boolean deletableToOwner;
    public boolean insertableToOthers;
    public boolean readableToOthers;
    public boolean writableToOthers;
    public boolean deletableToOthers;

    Permission(boolean insertableToOwner, boolean readableToOwner, boolean writableToOwner, boolean deletableToOwner,
            boolean insertableToOthers, boolean readableToOthers, boolean writableToOthers, boolean deletableToOthers) {
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
        // TODO Auto-generated method stub
        out.field("insertable_to_owner", insertableToOwner);
        out.field("readable_to_owner", readableToOwner);
        out.field("writable_to_owner", writableToOwner);
        out.field("deletable_to_owner", deletableToOwner);
        out.field("insertable_to_others", insertableToOthers);
        out.field("readable_to_others", readableToOthers);
        out.field("writable_to_others", writableToOthers);
        out.field("deletable_to_others", deletableToOthers);
    }

    public static JsonReader<Permission> Reader = new JsonReader<Permission>() {

        @Override
        public Permission read(JsonParser parser) throws IOException, JsonReadException {

            boolean insertableToOwner = false;
            boolean readableToOwner = false;
            boolean writableToOwner = false;
            boolean deletableToOwner = false;
            boolean insertableToOthers = false;
            boolean readableToOthers = false;
            boolean writableToOthers = false;
            boolean deletableToOthers = false;

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

            // TODO: add some checks?
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
}