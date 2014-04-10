package com.banmayun.sdk.core;

import java.io.IOException;

import com.banmayun.sdk.json.JsonReadException;
import com.banmayun.sdk.json.JsonReader;
import com.banmayun.sdk.util.DumpWriter;
import com.banmayun.sdk.util.Dumpable;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class Root extends Dumpable {
    
    public String id;
    public String type;
    public Size used;
    public Size quota;
    public Permission defaultPermission;
    public int fileCount;
    public long byteCount;

    public Root(String id, String type, Size used, Size quota, Permission defaultPermission, int fileCount,
            long byteCount) {
        this.id = id;
        this.type = type;
        this.used = used;
        this.quota = quota;
        this.defaultPermission = defaultPermission;
        this.fileCount = fileCount;
        this.byteCount = byteCount;
    }

    @Override
    protected void dumpFields(DumpWriter out) {
        // TODO Auto-generated method stub
        out.field("id", id);
        out.field("type", type);
        out.field("used", used);
        out.field("quota", quota);
        out.field("default_permission", defaultPermission);
        out.field("file_count", fileCount);
        out.field("byte_count", byteCount);
    }

    public static JsonReader<Root> Reader = new JsonReader<Root>() {
        
        @Override
        public Root read(JsonParser parser) throws IOException, JsonReadException {

            String id = null;
            String type = null;
            Size used = null;
            Size quota = null;
            Permission defaultPermission = null;
            int fileCount = -1;
            long byteCount = -1;

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
                    case FM_type:
                        type = JsonReader.StringReader.readField(parser, fieldName, type);
                        break;
                    case FM_used:
                        used = Size.Reader.readField(parser, fieldName, used);
                        break;
                    case FM_quota:
                        quota = Size.Reader.readField(parser, fieldName, quota);
                        break;
                    case FM_default_permission:
                        defaultPermission = Permission.Reader.readField(parser, fieldName, defaultPermission);
                        break;
                    case FM_file_count:
                        fileCount = (int) JsonReader.readUnsignedLongField(parser, fieldName, fileCount);
                        break;
                    case FM_byte_count:
                        byteCount = JsonReader.readUnsignedLongField(parser, fieldName, byteCount);
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
            return new Root(id, type, used, quota, defaultPermission, fileCount, byteCount);
        }
    };

    private static final int FM_id = 0;
    private static final int FM_type = 1;
    private static final int FM_used = 2;
    private static final int FM_quota = 3;
    private static final int FM_default_permission = 4;
    private static final int FM_file_count = 5;
    private static final int FM_byte_count = 6;

    private static final JsonReader.FieldMapping FM;

    static {
        JsonReader.FieldMapping.Builder b = new JsonReader.FieldMapping.Builder();
        b.add("id", FM_id);
        b.add("type", FM_type);
        b.add("used", FM_used);
        b.add("quota", FM_quota);
        b.add("default_permission", FM_default_permission);
        b.add("file_count", FM_file_count);
        b.add("byte_count", FM_byte_count);
        
        FM = b.build();
    }
}